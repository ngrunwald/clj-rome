(ns clj-rome.reader
  (:import (java.net URL)
           (com.sun.syndication.io SyndFeedInput XmlReader)
           (java.io StringReader FileReader))
  (:require [clj-time.coerce :as coerce]
            [clj-time.core :as time]
            [clj-time.format :as fmt]))

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

(defn build-feed
  [arg]
  (let
      [reader (make-reader arg)
       feed (.build (SyndFeedInput.) reader)]
    feed))

(defn entry2map
  [entry]
  {:contents (into [] (map (fn [c] {:value (.getValue c), :type (.getType c)}) (.getContents entry)))
   :authors (into [] (.getAuthors entry))
   :title (.getTitle entry)
   :link (.getLink entry)
   :description (if-let [desc (.getDescription entry)] {:value (.getValue desc) :type (.getType desc)} {:value nil :type nil})
   :categories (into [] (map (memfn getName) (.getCategories entry)))
   :updated-date (if-let [date (.getUpdatedDate entry)] (coerce/from-date date) nil)
   :published-date (if-let [date (.getPublishedDate entry)] (coerce/from-date date) nil )})

(defn list-entries
  [feed]
  (map entry2map (.getEntries feed)))
