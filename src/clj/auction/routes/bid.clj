(ns auction.routes.bid
  (:require
    [auction.render :as render]
    [auction.service.auction :as auction]
    [auction.service.items :as items]
    [auction.util :as util]
    [simpleui.core :as simpleui :refer [defcomponent]]
    [simpleui.rt :as rt]
    [ring.util.response :as response]))

(defcomponent item [req i {:keys [title src content-type bids price]}]
  (let [src (if content-type
              (format "/api/img?src=%s&content-type=%s" src content-type)
              src)
        last-price (or (-> bids peek :price) price)
        bids (if bids
               (for [{:keys [name price]} (take-last 3 bids)]
                 [:div name " " price])
               price)
        bidding? (auction/bidding?)]
    [:div.card {:style "width: 18rem;"}
     [:img.card-img-top.img-thumbnail {:src src}]
     [:div.card-body
      [:h5.card-title title]
      [:div.card-text bids]
      [:button.btn.btn-primary.mt-2
       {:hx-post "panel"
        :hx-target (hash "../..")
        :hx-vals (util/write-str
                   {(path "../../i") i})
        :disabled (not bidding?)}
       (if bidding?
         (str
           "Bid " (+ last-price items/increment))
         "Bidding paused")]]]))

(defcomponent ^:endpoint panel [req]
  (util/with-req req
    (let [{:keys [username]} session]
      (when (and post? username)
        (-> "i" value rt/parse-long (items/bid username)))
      [:div {:id id}
       [:div {:hx-get "panel" :hx-target (hash ".") :hx-trigger "sse:update"}]
       [:h3.my-2 "Welcome " username]
       (rt/map-indexed item req (items/get-items))])))

(defn bid-routes []
  (simpleui/make-routes
    "/bid"
    (fn [req]
      (util/with-req req
        (if (:username session)
          (render/html5-response
            [:div.container {:hx-sse "connect:/api/sse?page=bid"}
             (panel req)])
          (response/redirect "/"))))))
