(ns clj-rome.fetcher
  (:import (com.sun.syndication.fetcher.impl HttpURLFeedFetcher DiskFeedInfoCache
                                             LinkedHashMapFeedInfoCache HashMapFeedInfoCache)
           (com.sun.syndication.fetcher FetcherListener)
           (java.net URL))
  (:use [clojure.java.io :as io]
        [gavagai.core :as gav]))

(def ^{:dynamic true} *fetcher*)

(defn create-dir
  [path]
  (let [f (io/file path)]
    (.mkdirs f)
    (str f)))

(defn build-feed-cache
  "builds a cache instance based on type:
     - :hash-map
     - :linked-hash-map
     - :disk path"
  [type & args]
  (case type
    :hash-map (HashMapFeedInfoCache.)
    :linked-hash-map (LinkedHashMapFeedInfoCache.)
    :disk (DiskFeedInfoCache. (create-dir (first args)))))

(defn build-url-fetcher
  "builds an HttpURLFeedFetcher. Can take a cache type param
  (see build-feed-cache)"
  ([]
     (HttpURLFeedFetcher.))
  ([& args]
     (HttpURLFeedFetcher. (apply build-feed-cache args))))

(defn retrieve-feed
  "retrieves a feed from a fetcher or the default one.
   Returns a SyndFeedImpl object"
  ([fetcher url]
     (gav/translate (.retrieveFeed fetcher (URL. (str url)))))
  ([url]
     (retrieve-feed *fetcher* url)))

(defn add-listener
  "adds a listener to the given fetcher object.
   Returns a FetcherListener object. Use it with
   remove-listener."
  ([fetcher cb]
     (let [listener (proxy [FetcherListener] []
                      (fetcherEvent [event] (cb event)))]
       (.addFetcherEventListener fetcher listener)
       listener))
  ([cb]
     (add-listener *fetcher* cb)))

(defn remove-listener
  "removes a listener from the given fetcher"
  ([fetcher listener]
     (.removeFetcherEventListener fetcher listener))
  ([listener]
     (remove-listener *fetcher* listener)))

(defmacro with-fetcher
  [fetcher & body]
  `(binding [clj-rome.fetcher/*fetcher* ~fetcher]
     ~@body))
