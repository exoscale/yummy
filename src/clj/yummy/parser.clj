(ns yummy.parser
  (:require [yummy.tag :as tag])
  (:import org.yaml.snakeyaml.Yaml
           org.yaml.snakeyaml.representer.Representer
           org.yaml.snakeyaml.DumperOptions
           yummy.YummyConstructor))

(def ^:dynamic *tags* "registered parsers" [:envdir :envvar :keyword :envfmt])

(defprotocol Decoder
  (decode [data] "Post unmarshalling step to coerce to idiomatic clojure"))

(defn ^Yaml make-yaml
  "Create a YAML parser instance"
  []
  (org.yaml.snakeyaml.Yaml.
   (let [ctor (yummy.YummyConstructor.)]
     (doseq [tag *tags*]
       (.registerTag ctor (name tag)))
     ctor)
   (org.yaml.snakeyaml.representer.Representer.)
   (org.yaml.snakeyaml.DumperOptions.)))

(defn augment-bean
  "Transform a yummy TagHolder to a good data representation"
  [{:keys [map list scalar] :as bean}]
  (let [type (keyword (:type bean))]
    {:type type
     :tag  (keyword (:shortName bean))
     :args (case type
             :map (decode map)
             :list list
             scalar)}))

(defn parse-string
  "Load a string into a Clojure datastructure. In addition to standard YAML,
   A few additional tags are supported: !envdir, !keyword, and !envvar"
  [input]
  (-> (make-yaml)
      (.load (str input))
      (decode)))

(extend-protocol Decoder
  java.util.Map
  (decode [data]
    (reduce merge {} (for [[k v] data] [(or (keyword k) k) (decode v)])))
  java.util.LinkedHashSet
  (decode [data]
    (reduce conj #{} (map decode data)))
  java.util.ArrayList
  (decode [data]
    (into [] (map decode data)))
  yummy.TagHolder
  (decode [data]
    (tag/decode (augment-bean (bean data))))
  Object
  (decode [data]
    data)
  nil
  (decode [data]
    data))
