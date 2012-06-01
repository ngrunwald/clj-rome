(ns clj-rome.reader
  (:import (java.net URL)
           (com.sun.syndication.io SyndFeedInput XmlReader)
           (java.io StringReader FileReader))
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
  (make-reader
    [reader]
    reader))

(gav/register-converters
 ["com.sun.syndication.feed.synd.SyndFeedImpl" :exclude [:class] :add {:original identity}]
 ["com.sun.syndication.feed.synd.SyndContentImpl" :exclude [:class]]
 ["com.sun.syndication.feed.synd.SyndImageImpl" :exclude [:class]]
 ["com.sun.syndication.feed.module.DCModuleImpl" :exclude [:class]]
 ["com.sun.syndication.feed.module.DCSubjectImpl" :exclude [:class]]
 ["com.sun.syndication.feed.module.SyModuleImpl" :exclude [:class]]
 ["com.sun.syndication.feed.synd.SyndEntryImpl" :exclude [:class :source]]
 ["com.sun.syndication.feed.synd.SyndLinkImpl" :exclude [:class]]
 ["com.sun.syndication.feed.synd.SyndPersonImpl" :exclude [:class]]
 ["com.sun.syndication.feed.synd.SyndEnclosureImpl" :exclude [:class]]
 ["com.sun.syndication.feed.synd.SyndCategoryImpl" :exclude [:class]])

(defn build-feed
  "builds a SyndFeedImpl from an url, a filepath or a XML string"
  [arg]
  (let
      [reader (make-reader arg)
       feed (.build (SyndFeedInput.) reader)]
    (gav/with-translator-ns 'clj-rome.reader
      (gav/translate feed))))
