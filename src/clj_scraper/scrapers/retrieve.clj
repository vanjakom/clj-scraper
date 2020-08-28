(ns clj-scraper.scrapers.retrieve
  (:require [clj-common.http :as http])
  (:require [clj-common.time :as time])
  (:require [clj-common.io :as io])
  (:require [clj-common.base64 :as base64])
  (:require [clj-common.hash :as hash])
  (:require [clj-common.json :as json])
  (:require [clj-common.logging :as logging])
  (:require [clj-common.cache :as cache])
  (:require [clj-scraper.env :as env]))

(defn fs-serialize [entry]
  (json/serialize
    (assoc
      entry
      :data (base64/bytes->base64 (:data entry)))))

(defn fs-deserialize [bytes]
  (let [entry (json/deserialize bytes)]
    (assoc
      entry
      :data
      (base64/base64->bytes (:data entry)))))

;(def cache-fn (cache/create-in-memory-cache))
(def cache-fn (cache/create-local-fs-cache
             {
               :cache-path env/*cache-path*
               :key-fn hash/string->md5
               :value-serialize-fn fs-serialize
               :value-deserialize-fn fs-deserialize}))

;; deprecated
(def default-configuration
  {
   :keep-for (* 24 60 60)
   :cache-fn cache-fn})

(def ^:dynamic *configuration* default-configuration)

;; todo
;; fix must be added in each scraper to support dynamic configuration
;; extract url as only parameter to retrieve and always use dynamic configuration

(defn retrieve
  "Should either retrieve InputStream to page or download it
  Note: currently returning only InputStream but headers and status are kept
  also in cache"
  ([]
   (retrieve *configuration*))
  ([configuration]
   (let [url (:url configuration)
         timestamp (time/timestamp-second)
         keep-for (or (:keep-for configuration) 0)
         ;; safety for raw configurations which do not have cache-fn
         cache-fn (or (:cache-fn configuration) cache-fn)]
     (let [result-from-cache (cache-fn url)]
       (if
           (or
            (nil? result-from-cache)
            (and
             (not (= keep-for -1))
             (> timestamp (+ (get result-from-cache :timestamp 0) keep-for))))
         (let [result (http/get-raw-as-stream url)]
           (if (and
                (= (:status result) 200)
                (some? (:body result)))
             (let [result-to-cache {
                                    :url url
                                    :timestamp timestamp
                                    :data (io/input-stream->bytes (:body result))}]
               (logging/report {
                                :url url
                                :timestamp timestamp
                                :retireve :download-ok})
               (cache-fn url result-to-cache)
               (io/bytes->input-stream (:data result-to-cache)))
             (do
               (logging/report {
                                :url url
                                :timestamp timestamp
                                :retireve :download-fail})
               nil)))
         (do
           (logging/report {
                            :url url
                            :timestamp timestamp
                            :retireve :cache})
           (io/bytes->input-stream (:data result-from-cache))))))))
