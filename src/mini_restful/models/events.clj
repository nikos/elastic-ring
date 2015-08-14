(ns mini-restful.models.events)

;; TODO: see famito/djangoapp/famito/events/models.py for details

(declare my-events)

(defn create [event]
  {:id       815
   :name     "Turbofrankonia"
   :location "Würzburg"})

(defn find-all []
  my-events)

(defn find-by-id [id]
  (filter #(= id (:id %)) my-events))

(defn count-events []
  (count my-events))

(defn delete-event [event]
  )

(def my-events
  [{:id       810
    :name     "Foo"
    :location "Hamburg"}
   {:id       811
    :name     "so und so ..."
    :location "Kasel"}
   {:id       815
    :name     "Turbofrankonia"
    :location "Würzburg"}])