(ns auction.util
  (:require
    [clojure.data.json :as json]
    [clojure.string :as string]))

(defmacro kw [& syms]
  (zipmap (map keyword syms) syms))

(defn- rand-char []
  (rand-nth "abcdefghijklmnopqrstuvwxyz"))
(defn rand-string [len]
  (string/join (repeatedly len rand-char)))

(def write-str json/write-str)

(defn dissoc-i [v i]
  (->>
    (assoc v i nil)
    (filter identity)
    vec))
