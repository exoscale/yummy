(ns yummy.tag.envvar
  "Decoder for the envvar tag which pulls from the environment")

(defn get-envvar
  "A version of `System/getenv` with a possible default value"
  [{:keys [type args] :as opts}]
  (let [[v default] (case type
                      :map  [(:envVar args) (:defaultValue args)]
                      :list args
                      [args])]
    (or (System/getenv v) default)))

(defn get-envfmt
  "Formats according to environment variables"
  [[fmt & vs]]
  (apply format fmt (map #(System/getenv %) vs)))
