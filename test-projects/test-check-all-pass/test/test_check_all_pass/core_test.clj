(ns test-check-all-pass.core-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [clojure.test.check.properties :as prop]
            [test-check-all-pass.core :refer :all]))

(defmacro is-ok?
          [result]
          `(if (true? ~result)
             (is ~result)
             (is (= {} (ex-data ~result)))))


(deftest crypto-should-roundtrip
         (testing "d(e(x,k),k) = x"
                  (is (:result
                        (tc/quick-check 10
                                        (prop/for-all [message (gen/string)
                                                       user-key (gen/string)]
                                                      (= message (decrypt (encrypt message user-key) user-key))))))))
