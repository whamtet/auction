(ns auction.routes.notice
  (:require
    [auction.render :as render]
    [simpleui.core :as simpleui]))

(defn notice-routes []
  (simpleui/make-routes
    "/notice"
    (fn [req]
      (render/html5-response
        [:div.container
         [:div.row.my-2
          [:div.col
           "Bids are legally binding.  Please bid in good faith"]]
         [:a.btn.btn-primary {:href "/bid"} "I agree"]]))))
