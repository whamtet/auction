(ns auction.routes.bid
  (:require
    [auction.render :as render]
    [ctmx.core :as ctmx]
    [ctmx.response :as response]))

(ctmx/defcomponent panel [req]
  (ctmx/with-req req
    (if-let [username (:username session)]
      [:div
       [:h3.my-2 "Welcome " username]]
      (response/hx-redirect "/"))))

(defn bid-routes []
  (ctmx/make-routes
    "/bid"
    (fn [req]
      (render/html5-response
        [:div.container
         (panel req)]))))
