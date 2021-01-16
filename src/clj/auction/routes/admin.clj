(ns auction.routes.admin
  (:require
    [auction.render :as render]
    [auction.service.auction :as auction]
    [auction.service.items :as items]
    [auction.service.qr :as qr]
    [auction.util :as util]
    [ctmx.core :as ctmx]
    [ctmx.response :as response]
    [ctmx.rt :as rt]))

(defn valid-login? [username password]
  (= ["whamtet" "getmein"]
     [username password]))

(ctmx/defcomponent ^:endpoint login-form [req username password]
  (ctmx/with-req req
    (if (and post? (valid-login? username password))
      (assoc response/hx-refresh :session {:admin true})
      [:form.mt-3 {:id id :hx-post "login-form"}
       [:label.mr-2 "Username"]
       [:input {:type "text"
                :name (path "username")
                :value (value "username")
                :placeholder "Username"}] [:br]
       [:label.mr-2 "Password"]
       [:input {:type "password"
                :name (path "password")}] [:br]
       [:input.mr-2 {:type "submit"}]
       (when post?
         [:span.badge.badge-danger "Wrong username or password"])])))

(ctmx/defcomponent ^:endpoint start-stop [req]
  (ctmx/with-req req
    (when post? (auction/toggle-bid))
    [:div {:id id}
     [:button.btn.btn-primary.mt-2
      {:hx-post "start-stop" :hx-target (hash ".")}
      (if (auction/bidding?) "Stop" "Start")]]))

(ctmx/defcomponent ^:endpoint qr-code-form [req password]
  (ctmx/with-req req
    (let [src (str "/api/qr?password=" password)]
      (when (and post? (:admin session))
        (qr/set-password password))
      [:form.mt-3 {:id id :hx-post "qr-code-form"}
       [:input.mr-2 {:type "text"
                     :name (path "password")
                     :placeholder "QR Password"
                     :required true}]
       [:input.mr-2 {:type "submit" :value "Update"}]
       (when post?
         [:span.badge.badge-success "Saved"])
       [:div.my-2
        [:a {:href src :target "_blank"}
         [:img.img-thumbnail
          {:src src}]]]])))

(ctmx/defcomponent item [req i {:keys [title src content-type bids price]}]
  (let [src (if content-type
              (format "/api/img?src=%s&content-type=%s" src content-type)
              src)
        bids-div
        (if bids
          (for [{:keys [name price]} (take-last 3 bids)]
            [:div name " " price])
          price)]
    [:div.card {:style "width: 18rem;"}
     [:img.card-img-top.img-thumbnail {:src src}]
     [:div.card-body
      [:h5.card-title title]
      [:div.card-text bids-div]
      (when bids
        (list
          [:button.btn.btn-primary.mt-2
           {:hx-patch "items"
            :hx-target (hash "../..")
            :hx-vals (util/write-str
                       {(path "../../i") i})}
           "Delete bid"] [:br]))
      [:button.btn.btn-primary.mt-2
       {:hx-confirm (format "Delete %s?" title)
        :hx-delete "items"
        :hx-target (hash "../..")
        :hx-vals (util/write-str
                   {(path "../../i") i})}
       "Delete item"]]]))

(ctmx/defcomponent ^:endpoint items [req title img ^:float price]
  (ctmx/with-req req
    (when (:admin session)
      (when post? (items/add-item title img price))
      (when delete?
        (-> "i" value rt/parse-int items/remove-item))
      (when patch?
        (-> "i" value rt/parse-int items/unbid))
      [:div {:id id}
       [:div {:hx-get "items" :hx-target (hash ".") :hx-trigger "sse:update"}]
       [:h1 "Items"]
       (rt/map-indexed item req (items/get-items))
       [:hr]
       [:h5 "New Item"]
       [:form {:hx-post "items"
               :hx-encoding "multipart/form-data"
               :hx-target (hash ".")}
        [:input {:type "text"
                 :name (path "title")
                 :value title
                 :placeholder "Title"
                 :required true}] [:br]
        [:label.mr-2.my-2 "Image"]
        [:input {:type "file"
                 :name (path "img")
                 :accept "image/*"
                 :required true}] [:br]
        [:input {:type "number"
                 :name (path "price")
                 :value price
                 :placeholder "Price"
                 :required true}] [:br]
        [:input.mt-2 {:type "submit"}]]])))

(ctmx/defcomponent ^:endpoint panel [req]
  (ctmx/with-req req
    (if delete?
      (assoc response/hx-refresh :session nil)
      [:div {:id id}
       [:button.btn.btn-primary.float-right.mt-3
        {:hx-delete "panel"}
        "Logout"]
       [:br] [:br]
       (start-stop req)
       (qr-code-form req "")
       [:hr]
       (items req "" nil nil)])))

(ctmx/defcomponent page [req]
  (ctmx/with-req req
    (if (:admin session)
      (panel req)
      (login-form req "" ""))))

(defn admin-routes []
  (ctmx/make-routes
    "/admin"
    (fn [req]
      (render/html5-response
        [:div.container {:hx-sse "connect:/api/sse?page=admin"}
         (page req)]))))
