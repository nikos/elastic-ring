# mini-restful

An example application on how to integrate elasticsearch with a clojure REST back-end
allowing to store and query for sample data.


## Preparation

Install elasticsearch (for example via homebrew with `brew install elasticsearch`).
The install plugins to adminster elasticsearch via a comfortable admin UI, from
your `ES_HOME` (i.e. `/usr/local/Cellar/elasticsearch/1.7.1`) directory:

    bin/plugin --install mobz/elasticsearch-head

Then you can navigate your web browser to:
http://localhost:9200/_plugin/head/


Elasticsearch HQ (Monitoring and Management Web Application for ElasticSearch instances and clusters)

    bin/plugin --install royrusso/elasticsearch-HQ
    http://localhost:9200/_plugin/HQ/

Bigdesk (Live charts and statistics for elasticsearch cluster):

    bin/plugin --install lukas-vlcek/bigdesk

Kopf (web admin interface for elasticsearch):

    bin/plugin --install lmenezes/elasticsearch-kopf      -> /_plugin/kopf

For easier HTTP interaction, we are going to use httpie (see http://httpie.org),
a user-friendly alternative to curl.


## Usage

To start the web application:

    lein ring server-headless

See all import initial data:

    http localhost:3000/events

Search in a boundary box:

   http -v localhost:3000/events/by-location top_right_lat==49.9 top_right_lon==51.1 bottom_left_lat==9.7 bottom_left_lon==10.1

Delete one event:

    http -a admin:secret DELETE localhost:3000/events/<event-id>


## REPL session

    lein repl
    (use 'mini-restful.models.events)




## Elasticsearch tipps

Local Elasticsearch log file (homebrew compliant):

    tail -f /usr/local/var/log/elasticsearch/elasticsearch_niko.log


## Elasticsearch resources

Date Formats:
https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-date-format.html

Query syntax:
https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-query-string-query.html

Elastisch example:
https://github.com/codemomentum/es-plygrnd/blob/master/src/es_plygrnd/core.clj#L24



## License

Copyright © 2015 Niko Schmuck

Distributed under the MIT Open-Source License.
