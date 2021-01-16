(ns auction.service.sse
  (:require
    [org.httpkit.server :as httpkit]))

(defonce connections (atom {}))

(defn- safe-conj [s v]
  (conj (or s #{}) v))
(defn add-connection [page connection]
  (swap! connections update page safe-conj connection))
(defn remove-connection [page connection]
  (swap! connections update page disj connection))

(defn send! [& msgs]
  (doseq [msg msgs
          connection (@connections msg)]
    (httpkit/send! connection (str "event: update\ndata: \n\n") false)))
