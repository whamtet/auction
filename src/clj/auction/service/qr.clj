(ns auction.service.qr
  (:require
    [clojure.java.io :as io])
  (:import
    net.glxn.qrgen.javase.QRCode))

(def endpoint "http://localhost:3000/login?code=")
(defn- s-password [password]
  (.file (QRCode/from (str endpoint password))))

(def file (atom (s-password "")))

(defn set-password [password]
  (reset! file (s-password password)))

(defn input-stream []
  (io/input-stream @file))
