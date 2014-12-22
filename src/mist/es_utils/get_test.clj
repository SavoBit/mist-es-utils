(ns mist.es-utils.get-test
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query         :as q]
            [clojurewerkz.elastisch.rest.response :as esrsp]
            [clojure.data.json :as json]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [mist.es-utils.config :as config]
            [clojure.pprint :as pp]))

(defmacro dlet [bindings & body]
  `(let [~@(mapcat (fn [[n v]]
                       (if (or (vector? n) (map? n))
                           [n v]
                         [n v '_ `(println (name '~n) ":" ~v)]))
                   (partition 2 bindings))]
     ~@body))

(defn hits [& {:keys [platform metric-type env test-name response-size] :or {response-size 10000}}]
  (let [es-host (config/lookup :es :host)
        es-port (config/lookup :es :port)
        es-user (config/lookup :es :username)
        es-pw (config/lookup :es :password)
        index (str platform "-client-" metric-type "-" env "-v1")
        url (str "http://" es-user ":" es-pw "@" es-host ":" es-port)]
    (let [conn (esr/connect url)
          res  (esd/search conn index "mist-sdk" :query { :match { :TestID test-name }} :size response-size)
          n    (esrsp/total-hits res)
          hits (esrsp/hits-from res)]
      (println (format "Total " index " hits: %d" n))
      hits)))

(defn output-json [path data-name data]
  (let [filename (str path "/" data-name ".json")]
    (println (str "Writing json to: " filename))
    (with-open [writer (io/writer filename)]
      (json/write data writer))))

(defn output-csv [path data-name data]
  (let [filename (str path "/" data-name ".csv")
        columns (keys (first data))
        headers (map name columns)
        rows (mapv #(mapv % columns) data)]
    (println (str "Writing csv to: " filename))
    (with-open [file (io/writer filename)]
      (csv/write-csv file (cons headers rows)))))
    ;; (with-open [writer (io/writer filename)]
    ;;   (json/write data writer))))

(defn inject [samples device] 
  (reduce-kv #(assoc %1 %2 (assoc %3 :Device device)) {} samples))

(defn split-sample [sample]
  (let [device (:Device sample)
        sensors (into (sorted-map) (:Sensor sample))]
    (println (str "Inside split-sample device: " device " sensors: " sensors))
    (inject :Device device sensors)))

(defn run [env test-name platform path]
;;  (doseq [metric-type ["location" "wifi" "sensor" "beacon"]]
  (doseq [metric-type ["sensor"]]
    (let [data-name (str env "_" test-name "_" platform "-" metric-type)]
    (let [hits (hits :platform platform :metric-type metric-type :env env :test-name test-name :response-size 2)
          samples (mapv #(% :_source) hits)]
      ;;(pp/pprint (mapv #(% :_source) hits))
      ;; (output-json path data-name samples)
      (if (= metric-type "sensor")
        (do
          (let [split-samples (vec(map #(inject (:Sensor %) (:Device %)) samples))]
;;            (println "split-samples:")
;;            (pp/pprint split-samples)
            (doseq [sensor-samples split-samples]
              (println "sensor-samples:")
              (pp/pprint sensor-samples)
              (let [sensor-names (keys sensor-samples)]
                (println "sensor-names:")
                (pp/pprint sensor-names)
                (doseq [sample sensor-samples]
                  (println "sample: " sample)
                  (doseq [sensor-name sensor-names]
;;                    (pp/pprint {:sensor-name sensor-name :sensor-names sensor-names :sensor-name-sensor-samples (sensor-name sensor-samples)})))))))
                    (output-csv path (str data-name "_" sensor-name) (sensor-name sensor-samples))))))))

        (do
          (output-csv path data-name samples)))))))
