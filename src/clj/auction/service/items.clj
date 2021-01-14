(ns auction.service.items
  (:require
    [auction.util :as util]
    [clojure.java.io :as io])
  (:import
    java.io.File))

(def ^:private pics (File. "pics"))
(.mkdir pics)

(def ^:private count-lock (Object.))
(defn- fname []
  (locking count-lock
    (format "f%03d." (count (.list pics)))))

(defonce items (atom []))

(defn add-item [title {:keys [filename tempfile content-type]} price]
  (let [src (str (fname) (last (.split filename "\\.")))]
    (io/copy tempfile (File. pics src))
    (swap! items conj (util/kw title src price content-type))
    nil))
(defn get-items [] @items)

(defn input-stream [src]
  (io/input-stream
    (File. pics src)))

(defn remove-item [i]
  (swap! items util/dissoc-i i))
