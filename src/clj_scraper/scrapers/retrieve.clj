(ns clj-scraper.scrapers.retrieve)

(require '[clj-common.http :as http])
(require '[clj-common.time :as time])
(require '[clj-common.io :as io])
(require '[clj-common.logging :as logging])

(defonce cache (atom {}))

(defn retrieve
  "Should either retrieve InputStream to page or download it
  Note: currently returning only InputStream but headers and status are kept
  also in cache"
  [configuration]
  (let [url (:url configuration)
        timestamp (time/timestamp-second)
        keep-for (or (:keep-for configuration) 0)]
    (let [result-from-cache (get @cache url)]
      (if
        (or
          (nil? result-from-cache)
          (> timestamp (+ (get result-from-cache :timestamp 0) keep-for)))
        (let [result (http/get-raw-as-stream url)]
          (if (and
                (= (:status result) 200)
                (some? (:body result)))
            (let [result-to-cache (dissoc
                                    (assoc
                                      result
                                      :timestamp
                                      timestamp
                                      :body-generate-fn
                                      (io/cache-input-stream (:body result)))
                                    :body)]
              (logging/report {
                                :url url
                                :timestamp timestamp
                                :retireve :download-ok})
              (swap!
                cache
                (fn [cache]
                  (assoc cache url result-to-cache)))
              ((:body-generate-fn result-to-cache)))
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
          ((:body-generate-fn result-from-cache)))))))
