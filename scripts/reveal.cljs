#!/usr/bin/env lumo

; this is an example fuzzy matcher to open devtools urls via nREPL

(ns fuzzy.finder
  (:require [lumo.core :refer [*command-line-args*]]
            [clojure.string :as string]))

; -- node requires ----------------------------------------------------------------------------------------------------------

(def url (js/require "url"))
(def path (js/require "path"))
(def fs (js/require "fs"))
(def child-process (js/require "child_process"))

; -- config -----------------------------------------------------------------------------------------------------------------

; TODO: make this configurable externally?
(def search-dirs ["src" "resources/public"])                                                                                  ; folders will be searched in this order

; -- utils ------------------------------------------------------------------------------------------------------------------

(defn coerce-buffer-to-string [buffer & [encoding]]
  (if (js/Buffer.isBuffer buffer)
    (.toString buffer (or encoding "utf-8"))
    (.toString buffer)))

(defn print-err [& args]
  (binding [*print-fn* *print-err-fn*]
    (apply println args)))

(defn list-dir [dir]
  (let [items (map #(str dir "/" %) (.readdirSync fs dir))
        * (fn [item]
            (let [stat (.lstatSync fs item)]
              (if (.isDirectory stat)
                (list-dir item)
                [item])))]
    (mapcat * items)))

(defn path-segments [path]
  (filter (complement empty?) (string/split path "/")))

(defn spawn! [cmd & [args opts]]
  (let [result (.apply (.-spawnSync child-process) child-process (into-array (keep identity [cmd (into-array args) opts])))
        stdout (.-stdout result)
        stderr (.-stderr result)
        status (.-status result)]
    (if stdout
      (println (coerce-buffer-to-string stdout)))
    (if stderr
      (print-err (coerce-buffer-to-string stderr)))
    status))

; -- fuzzy search for best match --------------------------------------------------------------------------------------------

(defn filter-files-by-filename [files filename]
  (let [postfix (str "/" filename)]
    (filter #(string/ends-with? % postfix) files)))

(defn compute-score [reversed-candidate reversed-ideal]
  (count (take-while true? (map = reversed-candidate reversed-ideal))))

(defn compute-file-score [reversed-ideal file]
  [file (compute-score (reverse (path-segments file)) reversed-ideal)])

(defn compute-score-table [files ideal]
  (let [reversed-ideal (reverse ideal)
        table (map (partial compute-file-score reversed-ideal) files)]
    (reverse (sort #(compare (second %1) (second %2)) table))))

(defn find-best-candidate [file-path dir]
  (let [file-segments (path-segments file-path)
        filename (last file-segments)
        all-files (list-dir dir)
        candidate-files (filter-files-by-filename all-files filename)
        score-table (compute-score-table candidate-files file-segments)
        best-candidate (first score-table)]
    best-candidate))

(defn select-matching-file-for-url [file-url]
  (let [parsed-url (.parse url file-url)
        file-path (.-pathname parsed-url)
        best-candidates (keep (partial find-best-candidate file-path) search-dirs)                                            ; for each search-dir find the best candidate
        strong-candidates (filter #(> (second %) 1) best-candidates)                                                          ; take only candidates with score > 1 (thanks to clojure's convention to have at least 2-segments namespaces)
        winner (first (first strong-candidates))]
    winner))

; -- opening files ----------------------------------------------------------------------------------------------------------

(defmulti open-file! (fn [method & _args] (keyword method)))

(defmethod open-file! :idea [_ path line _column]
  (let [file-line (str path ":" line)]
    (spawn! "idea" [file-line])))

; -- main work --------------------------------------------------------------------------------------------------------------

(defn work! [& [file-url line column]]
  (if-let [matching-file (select-matching-file-for-url file-url)]
    (do
      (println (str "Requested url: " file-url "\n"
                    "Matching file: '" matching-file "'"))
      (open-file! :idea matching-file line column))
    (do
      (print-err (str "No matching file found for url: " file-url))
      1)))

(defn main! [args]
  (try
    (let [res (apply work! args)]
      (if-not (= res 0)
        (js/process.exit res)))
    (catch :default e
      (print-err (str "Exception: " (.-message e) "\n" (.-stack e)))
      (js/process.exit 101))))

; -- entry point ------------------------------------------------------------------------------------------------------------

(main! *command-line-args*)
