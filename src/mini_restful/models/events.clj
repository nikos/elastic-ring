(ns mini-restful.models.events
  (:require [mini-restful.elastic :as elastic]
            [clj-time.core :as timecore]
            [clj-time.format :as timefmt]
            [schema.core :as s]))

;; TODO: see famito/djangoapp/famito/events/models.py for details

;; Name of mapping type in elasticsearch index
(def mapping-type "event")

;; Definition of elasticsearch mapping
;; Mapping types doc: https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-core-types.html
(def EventMapping {:properties {:title       {:type "string" :store true}
                                :desc        {:type "string" :analyzer "snowball" :term_vector "with_positions_offsets"}
                                :location    {:type "string" :store true} ;; TODO: could be a nested property
                                :coord       {:type "geo_point" :store true}
                                :starttime   {:type "date" :store true :format "date_optional_time"}
                                :_updated_at {:type "date" :default "now" :store true :format "date_optional_time"}
                                :_created_at {:type "date" :default "now" :store true :format "date_optional_time"}}
                   :_timestamp {:enabled true :default "now" :store true}})

;; --------------------------------------------------------------------

(s/defschema Event {:_id                          String
                    :title                        String
                    :desc                         String
                    :location                     String
                    :coord                        String
                    :starttime                    String
                    (s/optional-key :_updated_at) String
                    (s/optional-key :_created_at) String
                    (s/optional-key :_timestamp)  String})

(s/defschema NewEvent (dissoc Event :_id :_created_at :_updated_at :_timestamp))

;; --------------------------------------------------------------------

;; TODO: just dummy data
(declare my-events)

;; Use as format: "2015-08-20T18:43:39.747Z"
(def datetime-formatter (timefmt/formatters :date-time))

(defn now [] (timefmt/unparse datetime-formatter (timecore/now)))

(defn create [event]
  (let [creation-timestamp (now)
        doc {:location    "Hamburg"
             :title       "Tanz in den Mai"
             :coord       "53.5511,9.99164"
             :starttime   creation-timestamp
             :_created_at creation-timestamp
             :desc        "... will come ..."}]
    (s/validate NewEvent doc)
    (elastic/add-doc mapping-type doc)
    doc))

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

(defn create-idx []
  (elastic/create-index {mapping-type EventMapping}))

(defn delete-idx []
  (elastic/delete-index))