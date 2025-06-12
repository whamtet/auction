(ns auction.render
  (:require
    [hiccup.core :as hiccup]
    [hiccup.page :refer [html5]]))

(defn html-response [body]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body body})

(defn html5-response
  ([body] (html5-response nil body))
  ([js body]
   (html-response
     (html5
       [:head
        [:meta {:charset "utf-8"}]
        [:meta {:name "viewport"
                :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]

        [:title "Auction App"]
        [:meta {:property "og:title" :content "Auction App"}]
        [:meta {:property "og:type" :content "website"}]
        [:meta {:property "og:url" :content "https://auction.simpleui.io/"}]
        [:meta {:property "og:image" :content "https://auction.simpleui.io/favicon.ico"}]

        [:link {:rel "stylesheet"
                :href "https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css"
                :integrity "sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2"
                :crossorigin "anonymous"}]]
       [:body body]
       [:script {:src "https://code.jquery.com/jquery-3.5.1.slim.min.js"
                 :integrity "sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj"
                 :crossorigin "anonymous"}]
       [:script {:src "https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/js/bootstrap.bundle.min.js"
                 :integrity "sha384-ho+j7jyWK8fNQe+A12Hb8AhRq26LrZ/JpcUGGOn+Y7RsweNrtN/tE3MoK7ZeZDyx"
                 :crossorigin "anonymous"}]
       [:script {:src "/js/htmx.min.js"}]
       [:script {:src "/js/helpers.js"}]
       (when js [:script {:src (str "/js" js)}])))))
