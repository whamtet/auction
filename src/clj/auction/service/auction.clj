(ns auction.service.auction
  (:require
    [auction.service.sse :as sse]))

(defonce ^:private bid? (atom false))

(defn toggle-bid []
  (swap! bid? not)
  (sse/send! "bid"))

(defn bidding? [] @bid?)
