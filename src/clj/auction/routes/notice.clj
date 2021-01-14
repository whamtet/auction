(ns auction.routes.notice
  (:require
    [auction.render :as render]
    [ctmx.core :as ctmx]))

(defn notice-routes []
  (ctmx/make-routes
    "/notice"
    (fn [req]
      (render/html5-response
        [:div.container
         [:div.row.my-2
          [:div.col
           "Bids are legally binding.  Please bid in good faith"]]
         [:a.btn.btn-primary {:href "/bid"} "I agree"]]))))
