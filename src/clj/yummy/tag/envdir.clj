(ns yummy.tag.envdir
  "Decoder for the envdir tag which creates a map of the file names and
  file contents found in a directory.

  This can be convenient when loading configuration provided by Kubernetes"
  (:require [clojure.string  :as string]
            [clojure.java.io :as io])
  (:import java.io.File))

(defn file-tuple
  "Given a file, return a tuple of its name as a keyword and
   its content as a string."
  [^File fd]
  [(keyword (.getName fd)) (string/trim (slurp fd))])

(defn get-envdir
  "Given a path, yield a map of file name to file contents"
  [path]
  (let [dir (io/file path)]
    (->> (.listFiles dir)
         (filter #(.isFile ^File %))
         (map file-tuple)
         (reduce merge {}))))
