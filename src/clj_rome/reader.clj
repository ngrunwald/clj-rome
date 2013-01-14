(ns clj-rome.reader
  (:import (java.net URL)
           (com.sun.syndication.io SyndFeedInput XmlReader)
           (java.io StringReader FileReader Reader))
  (:require [clojure.string :as str]
            [gavagai.core :as gav]))

(defprotocol Feedable
  (make-reader [arg]))

(extend-type java.net.URL
  Feedable
  (make-reader [url] (XmlReader. url)))

(extend-type java.lang.String
  Feedable
  (make-reader
    [str]
    (cond
      (re-find #"^https?://" str) (XmlReader. (URL. str))
      (re-find #"^\s*<" str) (StringReader. str)
      :else (FileReader. str))))

(extend-type java.io.Reader
  Feedable
  (make-reader [reader] reader))

(def feed-translator
  (gav/register-converters
   {:exclude [:class]}
   [["com.sun.syndication.feed.synd.SyndFeedImpl"]
    ["com.sun.syndication.feed.synd.SyndContentImpl"]
    ["com.sun.syndication.feed.synd.SyndImageImpl"]
    ["com.sun.syndication.feed.module.DCModuleImpl"]
    ["com.sun.syndication.feed.module.DCSubjectImpl"]
    ["com.sun.syndication.feed.module.SyModuleImpl"]
    ["com.sun.syndication.feed.synd.SyndEntryImpl"]
    ["com.sun.syndication.feed.synd.SyndLinkImpl"]
    ["com.sun.syndication.feed.synd.SyndPersonImpl"]
    ["com.sun.syndication.feed.synd.SyndEnclosureImpl"]
    ["com.sun.syndication.feed.synd.SyndCategoryImpl"]]))

(def convert-feed
  "converts an object from ROME to its Clojure immutable translation"
  (partial gav/translate feed-translator))

(defn build-feed
  "builds a SyndFeedImpl from an Url, a filepath, Reader or a XML string
   It takes 2 options
     :lazy? (set to false to get a realized map)
     :raw?  (set to true to get the raw java SyndFeed)"
  ([arg {:keys [lazy? raw?] :as opts}]
     (let
         [^Reader reader (make-reader arg)
          feed (.build (SyndFeedInput.) reader)]
       (if raw?
         feed
         (convert-feed feed opts))))
  ([arg]
     (build-feed arg {})))
