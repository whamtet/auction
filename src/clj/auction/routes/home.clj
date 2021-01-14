(ns auction.routes.home
  (:require
    [auction.render :as render]
    [ctmx.core :as ctmx]))

(defn home-routes []
  (ctmx/make-routes
    "/"
    (fn [req]
      (render/html5-response
        [:div.container
         [:h1 "Please scan QR Code"]]))))
