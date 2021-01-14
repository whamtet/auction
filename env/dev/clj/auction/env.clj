(ns auction.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [auction.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[auction started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[auction has shut down successfully]=-"))
   :middleware wrap-dev})
