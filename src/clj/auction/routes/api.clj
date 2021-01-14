(ns auction.routes.api
  (:require
    [auction.service.items :as items]
    [auction.service.qr :as qr]))

(defn item-img [req]
  (let [{:keys [src content-type]} (:params req)]
    {:status 200
     :headers {"Content-Type" content-type}
     :body (items/input-stream src)}))

(defn qr [req]
  (if (-> req :session :admin)
    {:status 200
     :headers {"Content-Type" "image/png"}
     :body (qr/input-stream)}
    {:status 401
     :headers {}
     :body ""}))

(defn api-routes []
  ["/api"
   ["" {:get (fn [req]
               {:status 200
                :headers {"Content-Type" "text/html"}
                :body (-> req pr-str)})}]
   ["/img" {:get item-img}]
   ["/qr" {:get qr}]])
