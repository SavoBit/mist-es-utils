(ns mist.es-utils.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.nrepl.server :as nrepl]
            [clojure.pprint :as pp]
            [mist.es-utils.config :as config]
            [mist.es-utils.get-test :as get-test]
            [cider.nrepl :refer (cider-nrepl-handler)]))

(def cli-options
  [["-n" "--nrepl-port PORT" "nREPL Port Number"
    :default 8888
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-e" "--env ENV" "Environment"
    :default "staging"]
   ["-t" "--test-name TESTNAME" "Name of Test to pull"
    :default "Test6"]
   ["-p" "--platform PLATFORM_NAME" "Name of Platofrom (ios or android)"
    :default "android"]
   ["-h" "--help"]])

(defn -main [& args]
  (let [{:keys [options]} (parse-opts args cli-options)]
    (config/init (str "config.edn." (:env options)))
    (if (:nrepl-port options)
      (do
        (println "Starting nREPL server on 8888")
        (nrepl/start-server :port 8888
                            :handler cider-nrepl-handler))
      (do
        (println "Running workflow")
        (get-test/run (:env options) (:test-name options) (:platform options))
        (System/exit 0)))))
