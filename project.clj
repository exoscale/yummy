(defproject spootnik/yummy "0.2.1"
  :description       "YAML configuration for Clojure application"
  :url               "https://github.com/exoscale/yummy"
  :license           {:name "MIT License"
                      :url  "https://github.com/exoscale/yummy/tree/master/LICENSE"}
  :plugins           [[lein-codox   "0.10.2"]
                      [lein-ancient "0.6.15"]]
  :codox             {:source-uri  "https://github.com/exoscale/yummy/blob/{version}/{filepath}#L{line}"
                      :output-path "target/docs"
                      :namespaces  [#"^net"]
                      :metadata    {:doc/format :markdown}}
  :source-paths      ["src/clj"]
  :java-source-paths ["src/java"]
  :dependencies      [[org.clojure/clojure "1.9.0"]
                      [org.yaml/snakeyaml  "1.21"]
                      [expound             "0.5.0"]]
  :profiles          {:dev {:resource-paths ["test/resources"]}}
  :global-vars       {*warn-on-reflection* true})
