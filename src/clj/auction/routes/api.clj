(ns auction.routes.api
  (:require
    [auction.service.items :as items]
    [auction.service.qr :as qr]
    [auction.service.sse :as sse]
    clojure.pprint
    [ctmx.render :as render]
    [org.httpkit.server :as httpkit]))

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

(defn sse [req]
  (httpkit/with-channel req channel
    (httpkit/send!
      channel
      {:status 200
       :headers {"Content-Type" "text/event-stream"
                 "Cache-Control" "no-cache"}}
      false)
    (sse/add-connection channel)
    (httpkit/on-close channel (fn [_] (sse/remove-connection channel)))))

(defn pprint [s]
  (with-out-str
    (clojure.pprint/pprint s)))

(defn api-routes []
  ["/api"
   ["" {:get (fn [req] (->> req pprint (vector :pre) render/snippet-response))}]
   ["/img" {:get item-img}]
   ["/qr" {:get qr}]
   ["/sse" sse]])
