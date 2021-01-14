(ns auction.routes.api
  (:require
    [auction.service.qr :as qr]))

(defn api-routes []
  ["/api"
   ["" {:get (fn [req]
               {:status 200
                :headers {"Content-Type" "text/html"}
                :body (-> req :query-string pr-str)})}]
   ["/qr" {:get (fn [_]
                  {:status 200
                   :headers {"Content-Type" "image/png"}
                   :body (qr/input-stream)})}]])
