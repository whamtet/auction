(ns auction.service.qr
  (:require
    [auction.util :as util]
    [clojure.java.io :as io]
    [clojure.java.shell :refer [sh]])
  (:import
    net.glxn.qrgen.javase.QRCode))

(->> "ifconfig"
    sh
    :out
    (re-find #"192.168\S+")
    (def server))

(def endpoint (str "http://" server ":3000/login?code="))
(defn- s-password [password]
  {:qr (.file (QRCode/from (str endpoint password)))
   :password password})

(def password
  (atom (s-password (util/rand-string 5))))

(defn set-password [pass]
  (reset! password (s-password pass)))

(defn input-stream []
  (-> @password :qr io/input-stream))
(defn password-match? [candidate]
  (-> @password :password (= candidate)))
