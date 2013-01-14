(ns clj-rome.test.reader
  (:use [clj-rome.reader] :reload)
  (:use [clojure.test]))

(let [feed-path "test/clj_rome/test/feeds/lacuisinededoria.xml"
      feed (build-feed feed-path)]
 (deftest options
   (is (instance? lazymap.core.LazyPersistentMap feed))
   (let [f2 (build-feed feed-path {:lazy? false})]
     (is (instance? clojure.lang.PersistentHashMap f2)))
   (let [raw-obj (build-feed feed-path {:raw? true})]
     (is (not (map? raw-obj)))))

 (deftest test-feed-from-path
   (is (= "rss_2.0" (:feed-type feed))))

 (deftest test-feed-from-string
   (is (= "rss_2.0" (:feed-type (build-feed (slurp feed-path))))))

 (def entry (first (:entries feed)))

 (deftest test-entry
   (is "Canard" (-> entry (:categories) (first) (:name)))))

