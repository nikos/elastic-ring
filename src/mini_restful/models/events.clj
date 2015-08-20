(ns mini-restful.models.events
  (:require [mini-restful.elastic :as elastic]
            [clj-time.core :as timecore]
            [clj-time.format :as timefmt]
            [schema.core :as s]
            [clojure.tools.logging :as log]))

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

;; Use ISO data format: "2015-08-20T18:43:39.747Z"
(def datetime-formatter (timefmt/formatters :date-time))

(defn now [] (timefmt/unparse datetime-formatter (timecore/now)))

(defn create [doc]
  (let [creation-timestamp (now)
        event (assoc doc :_created_at creation-timestamp)]
    (s/validate NewEvent event)
    (elastic/add-doc mapping-type event)
    event))

(defn find-all []
  (elastic/find-all-docs mapping-type))

(defn find-by-id [id]
  (elastic/find-by-id mapping-type id))

(defn count-events []
  (elastic/total-docs mapping-type))

(defn delete-event [event]
  (log/warn "UNIMPLEMENTED"))

(defn create-idx []
  (elastic/create-index {mapping-type EventMapping}))

(defn delete-idx []
  (elastic/delete-index))


;; === Populate some initial data

;;(if (= count-events 0)
(defn init-db []
  (do
    (log/info "No events found, let's create some...")

    (delete-idx)
    (create-idx)

    (create {:location  "Hamburg"
             :title     "Tanz im August"
             :desc      "... will come ..."
             :coord     "53.5511,9.99164"
             :starttime "2015-08-20T20:00"})

    (create {:location  "Lüneburg"
             :title     "Altstadtführung"
             :desc      "Packt die Schuhe ein"
             :coord     "53.5511,10.214"
             :starttime "2015-08-21T10:00"})))
