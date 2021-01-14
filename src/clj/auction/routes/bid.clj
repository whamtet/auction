(ns auction.routes.bid
  (:require
    [auction.render :as render]
    [auction.service.items :as items]
    [auction.util :as util]
    [ctmx.core :as ctmx]
    [ctmx.rt :as rt]
    [ring.util.response :as response]))

(ctmx/defcomponent item [req i {:keys [title src content-type bids price]}]
  (let [src (if content-type
              (format "/api/img?src=%s&content-type=%s" src content-type)
              src)
        last-price (or (-> bids peek :price) price)
        bids (if bids
               (for [{:keys [name price]} (take-last 3 bids)]
                 [:div name " " price])
               price)]
    [:div.card {:style "width: 18rem;"}
     [:img.card-img-top.img-thumbnail {:src src}]
     [:div.card-body
      [:h5.card-title title]
      [:div.card-text bids]
      [:button.btn.btn-primary.mt-2
       {:hx-post "panel"
        :hx-target (hash "../..")
        :hx-vals (util/write-str
                   {(path "../../i") i})}
       "Bid " (+ last-price items/increment)]]]))

(ctmx/defcomponent ^:endpoint panel [req]
  (ctmx/with-req req
    (let [{:keys [username]} session]
      (when (and post? username)
        (-> "i" value rt/parse-int (items/bid username)))
      [:div {:id id :hx-sse "connect:/api/sse"}
       [:div {:hx-get "panel" :hx-target (hash ".") :hx-trigger "sse:panel"}]
       [:h3.my-2 "Welcome " username]
       (rt/map-indexed item req (items/get-items))])))

(defn bid-routes []
  (ctmx/make-routes
    "/bid"
    (fn [req]
      (ctmx/with-req req
        (if (:username session)
          (render/html5-response
            [:div.container
             (panel req)])
          (response/redirect "/"))))))
