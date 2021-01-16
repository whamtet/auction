(ns auction.service.qr
  (:require
    [auction.config :refer [env]]
    [auction.util :as util]
    [clojure.java.io :as io]
    [clojure.java.shell :refer [sh]])
  (:import
    net.glxn.qrgen.javase.QRCode))

(defn server []
  (if (:dev env)
    (str
      "http://"
      (->> "ifconfig"
           sh
           :out
           (re-find #"192.168\S+"))
      ":3000/login?code=")
    "https://whamtet-auction.herokuapp.com/login?code="))

(defn- s-password [password]
  {:qr (.file (QRCode/from (str (server) password)))
   :password password})

(def password (atom nil))

(defn set-password
  ([] (set-password (util/rand-string 5)))
  ([pass]
   (reset! password (s-password pass))))

(defn input-stream []
  (-> @password :qr io/input-stream))
(defn password-match? [candidate]
  (-> @password :password (= candidate)))
