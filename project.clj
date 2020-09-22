(defproject exoscale/yummy "0.2.10"
  :description       "YAML configuration for Clojure application"
  :url               "https://github.com/exoscale/yummy"
  :license           {:name "MIT License"
                      :url  "https://github.com/exoscale/yummy/tree/master/LICENSE"}
  :plugins           [[lein-codox   "0.10.2"]
                      [lein-ancient "0.6.15"]]
  :codox             {:source-uri  "https://github.com/exoscale/yummy/blob/{version}/{filepath}#L{line}"
                      :output-path "target/docs"
                      :metadata    {:doc/format :markdown}}
  :source-paths      ["src/clj"]
  :java-source-paths ["src/java"]
  :dependencies      [[org.clojure/clojure "1.10.1"]
                      [org.yaml/snakeyaml  "1.25"]
                      [exoscale/cloak "0.1.1"]
                      [expound             "0.7.2"]]
  :profiles          {:dev {:resource-paths ["test/resources"]}}
  :deploy-repositories [["snapshots" :clojars] ["releases" :clojars]]
  :global-vars       {*warn-on-reflection* true}
  :pedantic? :warn)
