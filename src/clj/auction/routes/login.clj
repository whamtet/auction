(ns auction.routes.login
  (:require
    [auction.render :as render]
    [auction.service.qr :as qr]
    [auction.service.users :as users]
    [auction.util :as util]
    [simpleui.core :as simpleui]
    [simpleui.response :as response]))

(simpleui/defcomponent ^:endpoint username-prompt [req username]
  (util/with-req req
    (if (-> "/code" value qr/password-match?)
      (if (and post? (users/add-user username))
        (assoc
          (response/hx-redirect "/notice")
          :session {:username username})
        [:form {:id id :hx-post "username-prompt"}
         [:h3 "Please choose a username"]
         [:input {:type "hidden" :name "code" :value (value "/code")}]
         [:input.my-2 {:type "text" :name "username" :placeholder "Username"}] [:br]
         [:input {:type "submit"}] [:br]
         (when post? [:span.badge.badge-warning "Username taken.  Please choose another"])])
      [:div "Invalid code"])))

(defn login-routes []
  (simpleui/make-routes
    "/login"
    (fn [req]
      (render/html5-response
        [:div.container
         (username-prompt req "")]))))
