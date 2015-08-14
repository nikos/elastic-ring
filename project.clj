(defproject mini-restful "0.1.0-SNAPSHOT"
  :description "An example RESTful application written in Clojure using elasticseach as back-end"
  :url "https://github.com/nikos/mini-restful"
  :license {:name "MIT"
            :url  "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [clojurewerkz/elastisch "2.1.0"]
                 [compojure "1.4.0"]
                 [cheshire "5.5.0"]
                 [ring/ring-json "0.4.0"]
                 [environ "1.0.0"]
                 [buddy/buddy-hashers "0.6.0"]
                 [buddy/buddy-auth "0.6.0"]
                 [crypto-random "1.2.0"]]

  ; The lein-ring plugin allows us to easily start a development web server
  ; with "lein ring server". It also allows us to package up our application
  ; as a standalone .jar or as a .war for deployment to a servlet container
  ; (I know... SO 2005).
  :plugins [[lein-ring "0.9.6"]
            [lein-environ "1.0.0"]]

  ; See https://github.com/weavejester/lein-ring#web-server-options for the
  ; various options available for the lein-ring plugin
  :ring {:handler mini-restful.handler/app
         :nrepl   {:start? true
                   :port   9998}}

  :profiles
  {:dev  {:dependencies [[javax.servlet/servlet-api "2.5"]
                         [ring-mock "0.1.5"]]
          ; Since we are using environ, we can override these values with
          ; environment variables in production.
          :env          {:elastic-host "http://127.0.0.1:9200"
                         :elastic-user "user"
                         :elastic-pass "pass"}}

   :test {:env {:elastic-host "http://127.0.0.1:9200"
                :elastic-user "user"
                :elastic-pass "pass"}}})
