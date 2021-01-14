(ns auction.routes.home
  (:require
    [auction.render :as render]
    [ctmx.core :as ctmx]))

(defn home-routes []
  (ctmx/make-routes
    "/"
    (fn [req]
      (->
        [:div.container
         [:h1 "Please scan barcode"]]
        render/html5-response))))
