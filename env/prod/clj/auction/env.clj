(ns auction.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[auction started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[auction has shut down successfully]=-"))
   :middleware identity})
