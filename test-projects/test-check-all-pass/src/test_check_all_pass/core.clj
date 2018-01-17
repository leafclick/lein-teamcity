(ns test-check-all-pass.core
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.properties :as prop]))

(defn encrypt
      "I encrypt very hard."
      [x _k] x)

(defn decrypt
      "I decrypt very hard."
      [x _k] x)
