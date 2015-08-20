(ns mini-restful.elastic
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query :as q]
            [clojurewerkz.elastisch.aggregation :as a]
            [clojurewerkz.elastisch.rest.response :as esrsp]
            [clojure.pprint :as pp]
            [environ.core :refer [env]]))

;; TODO: Make use of persistent connections
;; https://github.com/dakrone/clj-http#using-persistent-connections
;;(esr/connect "http://127.0.0.1:9200"
;;             {:connection-manager (clj-http.conn-mgr/make-reusable-conn-manager {:timeout 10})})

(def conn (esr/connect (get env :elastic-host)
                       {:basic-auth   [(get env :elastic-user) (get env :elastic-pass)]
                        :conn-timeout 5000}))

;; The name of the elasticsearch index
(def index "mini_dev")



;; TODO: this would loop over all model types initially
(defn create-index [mapping-types]
  (esi/create conn index
              :settings {"index" {"number_of_shards"   3
                                  "number_of_replicas" 0}}
              :mappings mapping-types))

(defn delete-index []
  (esi/delete conn index))

(defn add-doc [mapping-type doc]
  (esd/create conn index mapping-type doc))


(defn qp
  ;; query and print
  [q]
  (let [res (esd/search conn index "event"
                        :query q
                        :sort {"_geo_distance" {"coord" {:lat 53.5
                                                         :lon 10.0}
                                                :order  "desc"
                                                :unit   "km"}})
        n (esrsp/total-hits res)
        hits (esrsp/hits-from res)]
    (println (format "Total hits: %d" n))
    (pp/pprint hits)))

;; TODO: better use bounding box


;; https://www.elastic.co/guide/en/elasticsearch/guide/current/sorting-by-distance.html

;;(def distance
;;  :query (q/filtered :query {:match_all {}}
;;                     :filter {:geo_distance {:distance "20km"
;;                                             "coord"   {:lat 53.5
;;                                                        :lon 10.0}}}))