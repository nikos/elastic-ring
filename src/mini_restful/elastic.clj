(ns mini-restful.elastic
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query :as q]
            [clojurewerkz.elastisch.rest.response :as esrsp]
            [clojure.pprint :as pp]
            [environ.core :refer [env]]))

;; TODO: Make use of persistent connections
;; https://github.com/dakrone/clj-http#using-persistent-connections
;;(esr/connect "http://127.0.0.1:9200"
;;             {:connection-manager (clj-http.conn-mgr/make-reusable-conn-manager {:timeout 10})})

(def conn (esr/connect (get env :elastic-url)
                       {:basic-auth   [(get env :elastic-user) (get env :elastic-pass)]
                        :conn-timeout 5000}))

;; The name of the elasticsearch index
(def index "mini_dev")



;; TODO: this would make use of all available model types
(defn create-index [mapping-types]
  (esi/create conn index
              :settings {"index" {"number_of_shards"   3
                                  "number_of_replicas" 0}}
              :mappings mapping-types))

(defn index-exists? []
  (esi/exists? conn index))

(defn delete-index []
  (esi/delete conn index))

(defn add-doc [mapping-type doc]
  (esd/create conn index mapping-type doc))

(defn find-by-id [mapping-type id]
  (esd/search conn index mapping-type
              :query {:_id id}))

(defn find-by-bounding-box [mapping-type bounding-box]
  (esd/search conn index mapping-type
              :query (q/filtered :query (q/match-all)
                                 :filter {:geo_bounding_box {"coord" bounding-box}})))  ;; TODO: this field is event specific

(defn find-all-docs [mapping-type]
  (let [results (esd/search conn index mapping-type
                            :query (q/match-all))
        docs (esrsp/hits-from results)]
    docs))

(defn total-docs [mapping-type]
  (let [results (esd/search conn index mapping-type
                            :query (q/match-all) :size 0)
        nr-hits (esrsp/total-hits results)]
    nr-hits))


;; TODO: move over to events
(defn qp
  ;; query and print
  [q]
  (let [res (esd/search conn index "event"
                        :query q
                        ;; TODO: better use bounding box
                        :sort {"_geo_distance" {"coord" {:lat 53.5
                                                         :lon 10.0}
                                                :order  "desc"
                                                :unit   "km"}})
        n (esrsp/total-hits res)
        hits (esrsp/hits-from res)]
    (println (format "Total hits: %d" n))
    (pp/pprint hits)))



;; https://www.elastic.co/guide/en/elasticsearch/guide/current/sorting-by-distance.html

;;(def distance
;;  :query (q/filtered :query {:match_all {}}
;;                     :filter {:geo_distance {:distance "20km"
;;                                             "coord"   {:lat 53.5
;;                                                        :lon 10.0}}}))