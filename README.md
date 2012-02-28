# clj-rome

A simple Clojure wrapper for the ROME feed parsing and manipulation library. Right now only the wrapper for feed parsing and fetching are implemented.

## Usage

```clojure
    ;; build-feed will automaticaly dispatch on an xml string, a filepath or an url
    ;; returns a SyndFeedImpl object
    (use 'clj-rome.reader)
    (def feed (build-feed "test/clj_rome/test/feeds/lacuisinededoria.xml"))

    ;; creating a caching fetcher
    (use 'clj-rome.fetcher)
    (def fetcher (build-url-fetcher :disk "/tmp/cache"))

    ;; using the fetcher with a cache to fetch a feed
    (def fetched
      (with-fetcher fetcher
        (retrieve-feed "http://www.atomenabled.org/atom.xml")))

    ;; get-entries returns a vector of SyndEntryImpl
    (def title (get-entry-title (first (get-entries feed))))

    ;; entry2map turns SyndEntryImpl into a map with the most
    ;; generally useful fields
    (def same-title (:title (entry2map (first (get-entries feed)))))
    ;; contains :contents :authors :title :link :links :description :categories :updated-date :published-date
    ;; the dates are in clj-time format
```

For more documentation on ROME, see the [ROME javadocs](http://www.jarvana.com/jarvana/view/net/java/dev/rome/rome/1.0.0/rome-1.0.0-javadoc.jar!/index.html).

## License

Copyright (C) 2012 Nils Grunwald

Distributed under the Eclipse Public License, the same as Clojure.
