(ns clj-rome.test.reader
  (:use [clj-rome.reader] :reload)
  (:use [clojure.test]))

(def feed (build-feed "test/clj_rome/test/feeds/lacuisinededoria.xml"))

(deftest lazyness
  (is (instance? lazymap.core.LazyPersistentMap feed))
  (let [f2 (build-feed "test/clj_rome/test/feeds/lacuisinededoria.xml" {:lazy? false})]
    (is (instance? clojure.lang.PersistentHashMap f2))))

(deftest test-feed-from-path
  (is (= "rss_2.0" (:feed-type feed))))

(deftest test-feed-from-string
  (is (= "rss_2.0" (:feed-type (build-feed (slurp "test/clj_rome/test/feeds/lacuisinededoria.xml"))))))

(def entry (first (:entries feed)))

(deftest test-entry
  (is "Canard" (-> entry (:categories) (first) (:name))))

