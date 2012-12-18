# clj-rome

 A simple Clojure wrapper for the [ROME](http://rometools.org/) feed parsing and manipulation library. Right now only the wrapper for feed parsing and fetching are implemented.

## Installation

`clj-rome` is available as a Maven artifact from
[Clojars](http://clojars.org/clj-rome):
```clojure
[clj-rome "0.3.1"]
```

## Usage

```clojure
    ;; build-feed will automaticaly dispatch on an xml string, a filepath or an url
    (use 'clj-rome.reader)
    (def feed (build-feed "test/clj_rome/test/feeds/lacuisinededoria.xml"))
```

 The return value of `build-feed` is a lazy structure (see [lazymap](https://github.com/ngrunwald/lazymap)) recursively translated from Java to Clojure with [gavagai](https://github.com/ngrunwald/gavagai). It has keys corresponding to the Java getter methods. If you prefer plain greedy maps, you can use build-feed with an option map:
```clojure
    (def feed (build-feed "test/clj_rome/test/feeds/lacuisinededoria.xml" {:lazy? false}))
```

Here are some exemples to give you an idea of what is in the feed and each entry:
```clojure
(keys feed)
=> (:foreign-markup :published-date :entries :preserving-wire-feed?
    :copyright :link :contributors :author :supported-feed-types
    :feed-type :image :language :title :uri :categories :original :modules
    :interface :links :encoding :authors :title-ex :description
    :description-ex)

(first (:entries feed))
=> {:enclosures [],
    :foreign-markup [],
    :wire-entry nil,
    :published-date #inst "2011-03-16T06:00:00.000-00:00",
    :link "http://lacuisinededoria.over-blog.com/article-magret-laque-au-miel-epice-quenelles-de-navets-jaunes-et-polenta-69083687.html",
    :contributors [],
    :author "",
    :title "Magret laqué au miel épicé, quenelles de navets jaunes et polenta",
    :uri "ae447456b7dda12824d4e2dbf7e13279",
    :updated-date nil,
    :categories [{:name "Canard", :taxonomy-uri nil}],
    :modules [{:subject nil,
               :creator nil,
               :date #inst "2011-03-16T06:00:00.000-00:00",
               :identifier nil,
               :formats [],
               :subjects [],
               :creators [],
               :types [],
               :contributors [],
               :coverages [],
               :contributor nil,
               :language nil,
               :title nil,
               :uri "http://purl.org/dc/elements/1.1/",
               :identifiers [],
               :rights nil,
               :rights-list [],
               :coverage nil,
               :type nil,
               :interface com.sun.syndication.feed.module.DCModule,
               :sources [],
               :dates [#inst "2011-03-16T06:00:00.000-00:00"],
               :format nil,
               :publishers [],
               :relations [],
               :descriptions [],
               :languages [],
               :source nil,
               :relation nil,
               :titles [],
               :publisher nil,
               :description nil}],
    :interface com.sun.syndication.feed.synd.SyndEntry,
    :links [],
    :authors [],
    :title-ex {:type nil,
               :interface com.sun.syndication.feed.synd.SyndContent,
               :mode nil,
               :value "Magret laqué au miel épicé, quenelles de navets jaunes et polenta"},
    :description {:type "text/html",
                  :interface com.sun.syndication.feed.synd.SyndContent,
                  :mode nil,
                  :value "<p style=\"text-align: center;\"> <span
                  style=\"font-family: comic sans ms,sans-serif;
                  font-size: 12pt;\">Une recette réalisée par mon mari
                  qui aime de plus en plus la cuisine et me \"voler\"
                  la place derrière les fourneaux !</span></p>"},
    :contents []}
```

 You can use the ROME fetcher to retrieve a feed from the web with caching and conditional GET handled for you:
```clojure
    ;; creating a caching fetcher
    ;; type can be any of :hash-map, :linked-hash-map, :disk
    (use 'clj-rome.fetcher)
    (def fetcher (build-url-fetcher :disk "/tmp/cache"))

    ;; you can attach a listener to your fetcher
    (add-listener fetcher (fn [event] (println event)))

    ;; using the fetcher with a cache to fetch a feed
    (def feed
      (with-fetcher fetcher
        (retrieve-feed "http://www.atomenabled.org/atom.xml")))
```

 For more documentation on ROME, see the [ROME javadocs](http://www.jarvana.com/jarvana/view/net/java/dev/rome/rome/1.0.0/rome-1.0.0-javadoc.jar!/index.html).

## License

Copyright (C) 2012 Nils Grunwald

Distributed under the Eclipse Public License, the same as Clojure.
