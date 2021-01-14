(ns auction.routes.api
  (:require
    [auction.service.qr :as qr]))

(defn api-routes []
  ["/api"
   ["/qr" {:get (fn [_]
                  {:status 200
                   :headers {"Content-Type" "image/png"}
                   :body (qr/input-stream)})}]])
