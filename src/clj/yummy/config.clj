(ns yummy.config
  ""
  (:require [clojure.spec.alpha :as spec]
            [clojure.string     :as string]
            [yummy.parser       :as parser]
            [expound.alpha      :as expound]))

(def ^:dynamic *die-fn* nil)

(spec/check-asserts true)

(defn die!
  "Exit early"
  [e msg]
  (let [estr (cond-> msg
               (some? e) (str ": " (.getMessage ^Exception e)))]
    (binding [*out* *err*] (println estr))
    (System/exit 1)))

(defn configuration-property
  "Yield a property name which can be used to point to a configuration
   file path"
  [program-name]
  (-> program-name (name) (str ".configuration")))

(defn configuration-environment-variable
  "Yield an environment variable name which can be used to point to a
  configuration file path"
  [program-name]
  (-> program-name name string/upper-case (str "_CONFIGURATION")))

(defn configuration-path
  "Try to find a configuration path candidate by looking at provided paths,
   JVM properties, and the environment"
  [program-name path]
  (or path
      (System/getProperty (configuration-property program-name))
      (System/getenv (configuration-environment-variable program-name))))

(def spec-printer
  "A custom spec printer with more helpful messages"
  (expound/custom-printer {:print-specs? false}))

(defn safe-slurp
  "Slurp a file or die with a helpful message"
  [path]
  (try
    (slurp path)
    (catch Exception e
      (*die-fn* e "cannot read configuration file"))))

(defn safe-parse
  "Parse YAML data or die with a helpful message"
  [data]
  (try
    (parser/parse-string data)
    (catch Exception e
      (*die-fn* e "cannot parse YAML configuration file"))))

(defn validate
  "Validate configuration against aspec or die with a helpful message"
  [v spec]
  (try
    (binding [spec/*explain-out* spec-printer]
      (spec/assert spec v))
    (catch Exception e
      (*die-fn* e "validation"))))

(defn load-config-string
  "Load configuration from a string, optionaly ensuring that the supplied
   spec is honored. An optional `die-fn` can be provided to report errors"
  ([input {:keys [spec die-fn]}]
   (binding [*die-fn* (or die-fn *die-fn* die!)]
     (cond-> (safe-parse input) (some? spec) (validate spec))))
  ([input]
   (load-config-string input {})))

(defn load-config
  "Load config from the filesystem. The path of the config file
   is resolved with `configuration-path`"
  [{:keys [spec program-name path die-fn] :as opts}]
  (binding [*die-fn* (or die-fn *die-fn* die!)]
    (-> (configuration-path program-name path)
        (safe-slurp)
        (load-config-string opts))))
