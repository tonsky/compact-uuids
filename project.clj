(defproject compact-uuids "0.2.1"
  :description  "Compact 26-char URL-safe representation of UUIDs"
  :license      { :name "Eclipse"
                  :url  "http://www.eclipse.org/legal/epl-v10.html" }
  :url "https://github.com/tonsky/compact-uuids"

  :dependencies [
    [org.clojure/clojure       "1.9.0-RC1"]
    [org.clojure/clojurescript "1.9.946" :scope "provided"]
  ]

  :plugins [
    [lein-cljsbuild "1.1.7"]
  ]

  :profiles {
    :dev {
      :dependencies [[criterium "0.4.4"]]
    }
  }

  :aliases {"test-all" ["do" ["clean"] ["test"] ["cljsbuild" "test"]]
            "bench" [["run" "-m" "compact-uuids.core.bench"]]}

  :cljsbuild {
    :test-commands
      {"test" ["node" "test_node.js"]}

    :builds [
      { :id "advanced"
        :source-paths ["src" "test"]
        :compiler {
          :main           compact_uuids.core.test
          :output-to      "target/compact_uuids.js"
          :output-dir     "target/advanced"
          :optimizations  :advanced
          :parallel-build true
        } }
      { :id "none"
        :source-paths ["src" "test"]
        :compiler {
          :main           compact_uuids.core.test
          :output-to      "target/compact_uuids.js"
          :output-dir     "target/none"
          :optimizations  :none
          :parallel-build true
        } }
  ]}
  :mirrors {
    "central" {:name "central" :url "https://repo.maven.apache.org/maven2/"}
  }
)
