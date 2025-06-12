(ns auction.routes.home
  (:require
    [auction.render :as render]
    [simpleui.core :as simpleui]))

(defn home-routes []
  (simpleui/make-routes
    "/"
    (fn [req]
      (render/html5-response
        [:div.container
         [:h1 "Please scan QR Code"]
         [:h5 "Your event organizer will provide this"]]))))
