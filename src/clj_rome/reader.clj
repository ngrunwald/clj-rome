(ns clj-rome.reader
  (:import (java.net URL)
           (com.sun.syndication.io SyndFeedInput XmlReader)
           (java.io StringReader FileReader))
  (:require [clj-time.coerce :as coerce]
            [clj-time.core :as time]
            [clj-time.format :as fmt]
            [clojure.contrib.string :as str]))

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

(defn build-feed
  "builds a SyndFeedImpl from an url, a filepath or a XML string"
  [arg]
  (let
      [reader (make-reader arg)
       feed (.build (SyndFeedInput.) reader)]
    feed))

(defn get-entry-contents
  [entry]
  (into [] (map (fn [c] {:value (.getValue c), :type (.getType c)}) (.getContents entry))))

(defn get-entry-full-content
  [entry]
  (str/join "\n" (get-entry-contents entry)))

(defn get-entry-authors
  [entry]
  (into [] (.getAuthors entry)))

(defn get-entry-title
  [entry]
  (.getTitle entry))

(defn get-entry-link
  [entry]
  (.getLink entry))

(defn get-entry-uri
  [entry]
  (.getUri entry))

(defn get-entry-authors
  [entry]
  (into [] (.getAuthors entry)))

(defn get-entry-description
  [entry]
  (if-let [desc (.getDescription entry)] {:value (.getValue desc) :type (.getType desc)} {:value nil :type nil}))

(defn get-entry-categories
  [entry]
  (into [] (map (memfn getName) (.getCategories entry))))

(defn get-entry-updated
  [entry]
  (if-let [date (.getUpdatedDate entry)] (coerce/from-date date) nil))

(defn get-entry-published
  [entry]
  (if-let [date (.getPublishedDate entry)] (coerce/from-date date) nil ))

(defn entry2map
  "transforms a SyndEntryImpl into a Clojure map"
  [entry]
  {:contents (get-entry-contents entry)
   :authors (get-entry-authors entry)
   :title (get-entry-title entry)
   :link (get-entry-link entry)
   :uri (get-entry-uri entry)
   :description (get-entry-description entry)
   :categories (get-entry-categories entry)
   :updated-date (get-entry-updated entry)
   :published-date (get-entry-published entry)})

(defn get-entries
  "returns a vector of SyndEntryImpl from a SyndFeedImpl"
  [feed]
  (into [] (.getEntries feed)))
