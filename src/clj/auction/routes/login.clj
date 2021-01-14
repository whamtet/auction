(ns auction.routes.login
  (:require
    [auction.render :as render]
    [auction.service.qr :as qr]
    [auction.service.users :as users]
    [ctmx.core :as ctmx]))

(ctmx/defcomponent ^:endpoint username-prompt [req username]
  (ctmx/with-req req
    (if (-> "/code" value qr/password-match?)
      (if (and post? (users/add-user username))
        "fuck"
        [:form {:id id :hx-post "username-prompt"}
         [:h3 "Please choose a username"]
         [:input {:type "hidden" :name "code" :value (value "/code")}]
         [:input.my-2 {:type "text" :name (path "username") :placeholder "Username"}] [:br]
         [:input {:type "submit"}] [:br]
         (when post? [:span.badge.badge-warning "Username taken.  Please choose another"])])
      [:div "Invalid code"])))

(defn login-routes []
  (ctmx/make-routes
    "/login"
    (fn [req]
      (render/html5-response
        [:div.container
         (username-prompt req "")]))))
