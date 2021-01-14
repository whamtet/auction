(ns auction.routes.admin
  (:require
    [auction.render :as render]
    [auction.service.qr :as qr]
    [ctmx.core :as ctmx]
    [ctmx.response :as response]))

(ctmx/defcomponent ^:endpoint form [req username password]
  (ctmx/with-req req
    (when (and post? (= username "whamtet"))
      (qr/set-password password))
    [:form.mt-3 {:id id :hx-post "form"}
     [:label.mr-2 "Username"]
     [:input {:type "text" :name (path "username") :placeholder "Username"}] [:br]
     [:label.mr-2 "Password"]
     [:input {:type "password" :name (path "password")}] [:br]
     [:input.mr-2 {:type "submit"}]
     (when post?
       (if (= username "whamtet")
         [:span.badge.badge-success "Success"]
         [:span.badge.badge-danger "Wrong Username"]))]))

(defn admin-routes []
  (ctmx/make-routes
    "/admin"
    (fn [req]
      (render/html5-response
        [:div.container
         (form req "" "")]))))