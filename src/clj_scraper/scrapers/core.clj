(ns clj-scraper.scrapers.core)

(use 'clj-common.clojure)

(require '[clj-common.logging :as logging])

(defn scrape [configuration]
  (let [id (:id configuration)]
    (if-let [scraper-fn (resolve (:scraper-fn configuration))]
      (scraper-fn configuration)
      (logging/report {
                        :status :fail
                        :message "scraper not set"
                        :id id}))))


(defn monitor [configuration result]
  (let [id (:id configuration)
        monitor-fn-seq (map resolve (:monitor-fn-seq configuration))]
    (reduce
      (fn [result monitor-fn]
        (monitor-fn configuration result))
      result
      monitor-fn-seq)))

(defn view [configuration result]
  (let [id (:id configuration)
        view-fn (resolve (:view-fn configuration))]
    (view-fn configuration result)))

(defn process-all [configurations]
  (todo-warn "require all namespaces needed before calling scrapers")
  (doall
    (map
      (fn [configuration]
        (let [result (monitor
                       configuration
                       (scrape configuration))]
          (view configuration result)))
      configurations)))

(comment
  (process-all (clj-common.edn/read-object
                     (clj-common.jvm/resource-as-stream ["configuration.edn"])))

)
