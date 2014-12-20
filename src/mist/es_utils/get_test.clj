(ns mist.es-utils.get-test
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query         :as q]
            [clojurewerkz.elastisch.rest.response :as esrsp]
            [mist.es-utils.config :as config]
            [clojure.pprint :as pp]))

(defn run [env test-name]
  (let [es-host (config/lookup :es :host)
        es-port (config/lookup :es :port)
        es-user (config/lookup :es :username)
        es-pw (config/lookup :es :password)
        url (str "http://" es-user ":" es-pw "@" es-host ":" es-port)]
    (println (format "url: %s" url))
    (let [conn (esr/connect url)
          res  (esd/search conn "android-client-*-staging-v1" "mist-sdk" :query { :match { :TestID test-name }})
          n    (esrsp/total-hits res)
          hits (esrsp/hits-from res)]
      (println (format "Total hits: %d" n))
      (pp/pprint (:_source (first hits))))))
