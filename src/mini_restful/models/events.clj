(ns mini-restful.models.events)

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