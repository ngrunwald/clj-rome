(ns clj-rome.fetcher
  (:import (com.sun.syndication.fetcher.impl HttpURLFeedFetcher)
           (com.sun.syndication.fetcher FetcherListener)
           (java.net URL))
  (:require [clojure.contrib.string]))

(def ^:dynamic *fetcher*)


(defn build-url-fetcher
  "builds an HttpURLFeedFetcher with or without caching."
  (
   []
     (HttpURLFeedFetcher.))
  (
   [cache]
     (HttpURLFeedFetcher. cache)
     ))

(defn retrieve-feed
  "retrieves a feed from a fetcher or the default one.
   Returns a SyndFeedImpl object"
  ( 
   [fetcher url]
     (.retrieveFeed fetcher (URL. (str url))))
  (
   [url]
     (retrieve-feed *fetcher* url)
     ))


(defn add-listener
  "adds a listener to the given fetcher object.
   Returns a FetcherListener object. Use it with
   remove-listener."
  ( [fetcher cb]
      (let [listener (proxy [FetcherListener] []
                       (fetcherEvent [event] (cb event)))]
        (.addFetcherEventListener fetcher listener)
        listener))
  ( [cb]
      (add-listener *fetcher* cb)
      ))

(defn remove-listener
  "removes a listener from the given fetcher"
  (
   [fetcher listener]
     (.removeFetcherEventListener fetcher listener)
     )
  (
   [listener]
     (remove-listener *fetcher* listener)
     ))

(defmacro with-fetcher
  [fetcher & body]
  `(binding [clj-rome.fetcher/*fetcher* fetcher]
     ~@body))

