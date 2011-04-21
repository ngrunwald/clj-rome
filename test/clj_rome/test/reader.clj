(ns clj-rome.test.reader
  (:use [clj-rome.reader] :reload)
  (:use [clojure.test]))

(def feed (build-feed "test/clj_rome/test/feeds/lacuisinededoria.xml"))

(deftest test-feed-from-path
  (is (= "rss_2.0" (.getFeedType feed))))

(deftest test-feed-from-string
  (is (= "rss_2.0" (.getFeedType (build-feed (slurp "test/clj_rome/test/feeds/lacuisinededoria.xml"))))))

(def entry (first (get-entries feed)))

(deftest test-entry
  (is "Canard" (first (get-entry-categories entry))))

(deftest test-map-entry
  (is "Canard" (first (:categories (entry2map entry)))))
