(ns auction.service.users)

(defonce ^:private users (ref #{}))

(defn add-user [user]
  (dosync
    (and
      (not (contains? @users user))
      (commute users conj user)
      true)))
