(defproject es-utils "0.1.0-SNAPSHOT"
  :description "Utilities for getting data from Mist Elastic Search"
  :url "http://www.mistsys.com"
  :license {:name "Copyright 2014 Mist Systems Inc."
            :url "http://www.mistsys.com"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clojurewerkz/elastisch "2.1.0"]
                 [org.clojure/data.json "0.2.5"]
                 [org.clojure/data.csv "0.1.2"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/tools.nrepl  "0.2.5"]
                 [org.clojure/tools.cli   "0.3.1"]
                 [spyscope "0.1.5"]
                 [cider/cider-nrepl        "0.8.1"]]

  :main mist.es-utils.core)
