(ns mist.es-utils.config
  (:require [clojure.edn :as edn]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]))

(defn read-config [filename]
  (-> filename io/resource slurp edn/read-string))

(def current (atom (read-config "config.edn.dev")))

(defn init [filename]
  (reset! current (read-config filename)))

(defn lookup [& ks]
  (get-in @current ks :config-not-found))


