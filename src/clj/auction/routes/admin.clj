(ns auction.routes.admin
  (:require
    [auction.render :as render]
    [auction.service.auction :as auction]
    [auction.service.items :as items]
    [auction.service.qr :as qr]
    [auction.util :as util]
    [simpleui.core :as simpleui]
    [simpleui.response :as response]
    [simpleui.rt :as rt]))

(defn valid-login? [username password]
  (= ["admin" "rummikub"]
     [username password]))

(simpleui/defcomponent ^:endpoint login-form [req username password]
  (util/with-req req
    (if (and post? (valid-login? username password))
      (assoc response/hx-refresh :session {:admin true})
      [:form.mt-3 {:id id :hx-post "login-form"}
       [:h2 "Admin Login"]
       [:h6.my-3 "Built with " [:a {:href "https://simpleui.io" :target "_blank"} "SimpleUI"] ". Light, fast, secure"]
       [:div.row.mb-2
        [:label.col-2 "Username"]
        [:input.col-2 {:type "text"
                       :name "username"
                       :value username
                       :required true
                       :placeholder "Username"}]]
       [:div.row.mb-2
        [:label.col-2 "Password"]
        [:input.col-2 {:type "password"
                       :name "password"
                       :required true}]]
       [:input.mr-2 {:type "submit"}]
       (when post?
         [:span.badge.badge-danger "Wrong username or password"])])))

(simpleui/defcomponent ^:endpoint start-stop [req]
  (util/with-req req
    (when post? (auction/toggle-bid))
    [:div {:id id}
     [:button.btn.btn-primary.mt-2
      {:hx-post "start-stop" :hx-target (hash ".")}
      (if (auction/bidding?) "Stop Bidding" "Start Bidding")]]))

(simpleui/defcomponent ^:endpoint qr-code-form [req password]
  (util/with-req req
    (let [src (str "/api/qr?password=" password)]
      (when (and post? (:admin session))
        (qr/set-password password))
      [:form.mt-3 {:id id :hx-post "qr-code-form"}
       [:input.mr-2 {:type "text"
                     :name "password"
                     :placeholder "QR Password"
                     :required true}]
       [:input.mr-2 {:type "submit" :value "Update"}]
       (when post?
         [:span.badge.badge-success "Saved"])
       [:div.my-2
        [:a {:href src :target "_blank"}
         [:img.img-thumbnail
          {:src src}]]
        [:b.ml-5 "Scan this code with mobile device"]]])))

(simpleui/defcomponent item [req i {:keys [title src content-type bids price]}]
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

(simpleui/defcomponent ^:endpoint items [req title img ^:float price]
  (util/with-req req
    (when (:admin session)
      (when post? (items/add-item title img price))
      (when delete?
        (-> "i" value rt/parse-long items/remove-item))
      (when patch?
        (-> "i" value rt/parse-long items/unbid))
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
                 :name "title"
                 :value title
                 :placeholder "Title"
                 :required true}] [:br]
        [:label.mr-2.my-2 "Image"]
        [:input {:type "file"
                 :name "img"
                 :accept "image/*"
                 :required true}] [:br]
        [:input {:type "number"
                 :name "price"
                 :value price
                 :placeholder "Price"
                 :required true}] [:br]
        [:input.mt-2 {:type "submit"}]]])))

(simpleui/defcomponent ^:endpoint panel [req]
  (util/with-req req
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

(simpleui/defcomponent page [req]
  (util/with-req req
    (if (:admin session)
      (panel req)
      (login-form req "" ""))))

(defn admin-routes []
  (simpleui/make-routes
    "/admin"
    (fn [req]
      (render/html5-response
        [:div.container {:hx-sse "connect:/api/sse?page=admin"}
         (page req)]))))
