(ns mini-restful.elastic
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query :as q]
            [clojurewerkz.elastisch.aggregation :as a]
            [clojurewerkz.elastisch.rest.response :as esrsp]
            [clojure.pprint :as pp]
            [environ.core :refer [env]]))

(def conn (esr/connect (get env :elastic-host)
                       {:basic-auth   [(get env :elastic-user) (get env :elastic-pass)]
                        :conn-timeout 5000}))


(def index "events_dev")

;; Persistent connections
;; https://github.com/dakrone/clj-http#using-persistent-connections
;;(esr/connect "http://127.0.0.1:9200"
;;             {:connection-manager (clj-http.conn-mgr/make-reusable-conn-manager {:timeout 10})})


(defn create-index []
  (let [mapping-types {"event" {:properties {:city       {:type "string" :store "yes"}
                                             :title      {:type "string" :store "yes"}
                                             :coord      {:type "geo_point", :store "yes"}
                                             :updated_at {:type "date", :store "yes", :format "dateOptionalTime"}
                                             :desc       {:type "string" :analyzer "snowball" :term_vector "with_positions_offsets"}}}}]
    (esi/create conn index
                :settings {"index" {"number_of_shards"   3
                                    "number_of_replicas" 0}}
                :mappings mapping-types)))

(defn delete-index []
  (esi/delete conn index))

(defn add-doc []
  (let [doc {:city       "Hamburg"
             :title      "Tanz in den Mai"
             :coord      "53.5511,9.99164"
             :updated_at "2015-07-30T10:00:00+00:00"
             :desc       "... will come ..."}]
    (println (esd/create conn index "event" doc))))


(defn qp
  ;query and print
  [q]
  (let [res (esd/search conn index "event" :query q)
        n (esrsp/total-hits res)
        hits (esrsp/hits-from res)]
    (println (format "Total hits: %d" n))
    (pp/pprint hits)))