(ns mist.es-utils.get-test
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query         :as q]
            [clojurewerkz.elastisch.rest.response :as esrsp]
            [mist.es-utils.config :as config]
            [clojure.pprint :as pp]))

(defn hits [& {:keys [platform metric-type env test-name response-size] :or {response-size 10000}}]
  (let [es-host (config/lookup :es :host)
        es-port (config/lookup :es :port)
        es-user (config/lookup :es :username)
        es-pw (config/lookup :es :password)
        index (str platform "-client-" metric-type "-" env "-v1")
        url (str "http://" es-user ":" es-pw "@" es-host ":" es-port)]
    (println (format "url: %s" url))
    (let [conn (esr/connect url)
          res  (esd/search conn index "mist-sdk" :query { :match { :TestID test-name }} :size response-size)
          n    (esrsp/total-hits res)
          hits (esrsp/hits-from res)]
      (println (format "Total hits: %d" n))
      hits)))

(defn run [env test-name platform]
  (doseq [metric-type ["location" "wifi" "sensor" "beacon"]]
    (println (str "env: " env " test-name: " test-name " platform: " platform " metric-type: " metric-type ))
    (let [hits (hits :platform platform :metric-type metric-type :env env :test-name test-name :response-size 2)]
      (pp/pprint (mapv #(% :_source) hits)))))
