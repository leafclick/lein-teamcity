(ns lein-teamcity.plugin
  (:require [robert.hooke]
            [leiningen.test]
            [leiningen.jar]
            [leiningen.uberjar]
            [clojure.test]
            [leiningen.core.main]
            [leiningen.core.eval]
            [leiningen.core.project :as project]
            [clojure.string :as str]))

(defn escape
  [s]
  (when s
    (str/replace s #"['|\n\r\[\]]"
                 (fn [x]
                   (cond (= x "\n") "|n"
                         (= x "\r") "|r"
                         :else (str "|" x))))))

(defn tc-msg-attrs
  [attrs]
  (if (seq (rest attrs))
    (->> attrs
         (partition 2)
         (map (fn [[n v]] (str (name n) "='" (escape v) "'")))
         (str/join " "))
    (str "'" (first attrs) "'")))

(defn tc-msg
  [message & attrs]
  (str "##teamcity[" (name message) " " (tc-msg-attrs attrs) "]"))

(defn add-teamcity-jar-artifact-reporting [f & [_ out-file :as args]]
  (apply f args)
  (println (tc-msg :publishArtifacts out-file)))

(defn add-teamcity-uberjar-artifact-reporting [f & args]
  (let [artifact (apply f args)]
    (println (tc-msg :publishArtifacts artifact))))

(defn add-teamcity-task-reporting [f & [name :as args]]
  (println (tc-msg :blockOpened :name name))
  (apply f args)
  (println (tc-msg :blockClosed :name name)))

(defn hooks []
  (do
    (robert.hooke/add-hook #'leiningen.jar/write-jar
                           add-teamcity-jar-artifact-reporting)
    (robert.hooke/add-hook #'leiningen.uberjar/uberjar
                           add-teamcity-uberjar-artifact-reporting)
    (robert.hooke/add-hook #'leiningen.core.main/apply-task
                           add-teamcity-task-reporting)))
