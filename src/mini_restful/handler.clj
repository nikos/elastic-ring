(ns mini-restful.handler
  (:use compojure.core
        ring.middleware.json)
  (:import (com.fasterxml.jackson.core JsonGenerator))
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.util.response :refer [response]]
            [cheshire.generate :refer [add-encoder]]
            [mini-restful.models.events :as events]
            [mini-restful.auth :refer [auth-backend user-can user-isa user-has-id authenticated-user unauthorized-handler make-token!]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.accessrules :refer [restrict]]))

; Strip namespace from namespaced-qualified keywwords, which is how we represent user levels
(add-encoder clojure.lang.Keyword
             (fn [^clojure.lang.Keyword kw ^JsonGenerator gen]
               (.writeString gen (name kw))))

;; =====================================================================

(defn get-events [_]
  {:status 200
   :body   {:count   (events/count-events)
            :results (events/find-all)}})

(defn create-event [{event :body}]
  (let [new-event (events/create event)]
    {:status  201
     :headers {"Location" (str "/events/" (:id new-event))}}))

(defn find-event [{{:keys [id]} :params}]
  (response (events/find-by-id (read-string id))))

(defn delete-event [{{:keys [id]} :params}]
  (events/delete-event {:id (read-string id)})
  {:status  204
   :headers {"Location" "/events"}})

;; =====================================================================


(defroutes app-routes
           ;; EVENTS
           (context "/events" []
             (GET "/" [] get-events)
             ;; (restrict {:handler  {:and [authenticated-user (user-can "manage-events")]}
             ;;           :on-error unauthorized-handler}))

             (POST "/" [] create-event)

             (context "/:id" [id]
               (restrict
                 (routes
                   (GET "/" [] find-event))
                 {:handler  {:and [authenticated-user (user-can "manage-events")]}
                  :on-error unauthorized-handler}))

             (DELETE "/:id" [id] (-> delete-event
                                     (restrict {:handler  {:and [authenticated-user (user-can "manage-users")]}
                                                :on-error unauthorized-handler}))))

           (POST "/sessions" {{:keys [user-id password]} :body}
             (let [userid-int (Integer/parseInt user-id)]
               (if (and (= userid-int 42) (= password "42"))
                 {:status 201
                  :body   {:auth-token (make-token! userid-int)}}
                 {:status 409
                  :body   {:status  "error"
                           :message "invalid username or password"}})))

           (route/not-found (response {:message "Page not found"})))

(defn wrap-log-request [handler]
  (fn [req]
    (println req)
    (handler req)))

(def app
  (-> app-routes
      (wrap-authentication auth-backend)
      (wrap-authorization auth-backend)
      wrap-json-response
      (wrap-json-body {:keywords? true})))
