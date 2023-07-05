(defproject exoscale/yummy "0.2.13-SNAPSHOT"
  :description       "YAML configuration for Clojure application"
  :url               "https://github.com/exoscale/yummy"
  :license           {:name "MIT License"
                      :url  "https://github.com/exoscale/yummy/tree/master/LICENSE"}
  :plugins           [[lein-codox   "0.10.8"]
                      [lein-ancient "0.7.0"]]
  :codox             {:source-uri  "https://github.com/exoscale/yummy/blob/{version}/{filepath}#L{line}"
                      :output-path "target/docs"
                      :metadata    {:doc/format :markdown}}
  :javac-options     ["-target" "1.8" "-source" "1.8"]
  :source-paths      ["src/clj"]
  :java-source-paths ["src/java"]
  :dependencies      [[org.clojure/clojure "1.11.1"]
                      [org.yaml/snakeyaml  "2.0"]
                      [exoscale/cloak "0.1.10"]
                      [expound             "0.9.0"]]
  :profiles          {:dev {:resource-paths ["test/resources"]}}
  :deploy-repositories [["snapshots" :clojars] ["releases" :clojars]]
  :global-vars       {*warn-on-reflection* true}
  :pedantic? :warn)
