(ns auction.service.sse
  (:require
    [org.httpkit.server :as httpkit]))

(defonce connections (atom #{}))
(defn add-connection [connection]
  (swap! connections conj connection))
(defn remove-connection [connection]
  (swap! connections disj connection))

(defn send! [msg]
  (doseq [connection @connections]
    (httpkit/send! connection (str "event: " msg "\ndata: \n\n") false)))
