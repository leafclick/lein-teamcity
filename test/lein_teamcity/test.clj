(ns lein-teamcity.test
  (:require [clojure.test :refer :all]
            [clojure.java.shell :refer [sh]]
            [lein-teamcity.plugin :refer :all]
            [clojure.string :as str])
    (:import [java.io File]))

(deftest escaping
  (is (= (tc-msg :msg :name "a'b")  "##teamcity[msg name='a|'b']"))
  (is (= (tc-msg :msg :name "a\nb") "##teamcity[msg name='a|nb']"))
  (is (= (tc-msg :msg :name "a\rb") "##teamcity[msg name='a|rb']"))
  (is (= (tc-msg :msg :name "a|b")  "##teamcity[msg name='a||b']"))
  (is (= (tc-msg :msg :name "a[b")  "##teamcity[msg name='a|[b']"))
  (is (= (tc-msg :msg :name "a]b")  "##teamcity[msg name='a|]b']")))

(defn teamcity-test-messages
  [s]
  (re-seq #"##teamcity\[.*" s))

(defn lein-test
  [test-project-root]
  (spit (str test-project-root "/.lein-classpath") (.getCanonicalPath (File. "src")))
  #_(sh (System/getProperty "leiningen.script") "test" :dir test-project-root)
  (sh "lein" "test" :dir test-project-root))

(deftest report-all-passing
  (let [{:keys [exit out]} (lein-test "./test-projects/all-pass")]
    (is (= 0 exit))
    (is (= ["##teamcity[blockOpened name='|[\"run\" \"-m\" \"circleci.test/dir\" :project/test-paths|]']"
            "##teamcity[blockOpened name='javac']"
            "##teamcity[blockClosed name='javac']"
            "##teamcity[blockOpened name='compile']"
            "##teamcity[blockClosed name='compile']"
            "##teamcity[testSuiteStarted name='all-pass.core-test']"
            "##teamcity[testStarted name='all-pass.core-test/ (a-test) (:)' captureStandardOutput='true']"
            "##teamcity[testFinished name='all-pass.core-test/ (a-test) (:)']"
            "##teamcity[testSuiteFinished name='all-pass.core-test']"
            "##teamcity[blockClosed name='|[\"run\" \"-m\" \"circleci.test/dir\" :project/test-paths|]']"]
           (teamcity-test-messages out)))))

(deftest report-failure
  (let [{:keys [exit out]} (lein-test "./test-projects/one-failure")]
    (is (= 1 exit))
    (is (= ["##teamcity[blockOpened name='|[\"run\" \"-m\" \"circleci.test/dir\" :project/test-paths|]']"
            "##teamcity[blockOpened name='javac']"
            "##teamcity[blockClosed name='javac']"
            "##teamcity[blockOpened name='compile']"
            "##teamcity[blockClosed name='compile']"
            "##teamcity[testSuiteStarted name='one-failure.core-test']"
            "##teamcity[testStarted name='one-failure.core-test/ (a-failing-test) (:)' captureStandardOutput='true']"
            "##teamcity[testFailed name='one-failure.core-test/ (a-failing-test) (core_test.clj:7)' message='' details='|nI fail.' type='comparisonFailure' expected='(= 0 1)' actual='(not (= 0 1))']"
            "##teamcity[testFinished name='one-failure.core-test/ (a-failing-test) (:)']"
            "##teamcity[testSuiteFinished name='one-failure.core-test']"]
           (teamcity-test-messages out)))))

(deftest report-error
  (let [{:keys [exit out]} (lein-test "./test-projects/one-error")
        messages (teamcity-test-messages out)
        num-intro 7]
    (is (= 1 exit))
    (is (= 10 (count messages)))
    (is (= ["##teamcity[blockOpened name='|[\"run\" \"-m\" \"circleci.test/dir\" :project/test-paths|]']"
            "##teamcity[blockOpened name='javac']"
            "##teamcity[blockClosed name='javac']"
            "##teamcity[blockOpened name='compile']"
            "##teamcity[blockClosed name='compile']"
            "##teamcity[testSuiteStarted name='one-error.core-test']"
            "##teamcity[testStarted name='one-error.core-test/ (an-erroring-test) (:)' captureStandardOutput='true']"]
           (take num-intro messages)))
    (is (str/starts-with? (nth messages num-intro) "##teamcity[testFailed name='one-error.core-test/ (an-erroring-test) (core_test.clj:6)' message='Uncaught exception, not in assertion.' details='Uncaught exception, not in assertion.|nexpected=|'nil|'|nactual=|'java.lang.Exception: ERROR|n at one_error.core_test/fn (core_test.clj:6)|n"))
    (is (str/ends-with? (nth messages num-intro) "clojure.main.main (main.java:37)|n|'']"))
    (is (= "##teamcity[testFinished name='one-error.core-test/ (an-erroring-test) (:)']" (nth messages (inc num-intro))))
    (is (= "##teamcity[testSuiteFinished name='one-error.core-test']" (last messages)))))

(deftest report-test-check-all-passing
  (let [{:keys [exit out]} (lein-test "./test-projects/test-check-all-pass")]
    (is (= 0 exit))
    (is (= ["##teamcity[blockOpened name='|[\"run\" \"-m\" \"circleci.test/dir\" :project/test-paths|]']"
            "##teamcity[blockOpened name='javac']"
            "##teamcity[blockClosed name='javac']"
            "##teamcity[blockOpened name='compile']"
            "##teamcity[blockClosed name='compile']"
            "##teamcity[testSuiteStarted name='test-check-all-pass.core-test']"
            "##teamcity[testStarted name='test-check-all-pass.core-test/ (crypto-should-roundtrip) (:)' captureStandardOutput='true']"
            "##teamcity[progressMessage 'trial 0/10']"
            "##teamcity[progressMessage 'trial 1/10']"
            "##teamcity[progressMessage 'trial 2/10']"
            "##teamcity[progressMessage 'trial 3/10']"
            "##teamcity[progressMessage 'trial 4/10']"
            "##teamcity[progressMessage 'trial 5/10']"
            "##teamcity[progressMessage 'trial 6/10']"
            "##teamcity[progressMessage 'trial 7/10']"
            "##teamcity[progressMessage 'trial 8/10']"
            "##teamcity[progressMessage 'trial 9/10']"
            "##teamcity[progressMessage 'trial 10/10']"
            "##teamcity[testFinished name='test-check-all-pass.core-test/ (crypto-should-roundtrip) (:)']"
            "##teamcity[testSuiteFinished name='test-check-all-pass.core-test']"
            "##teamcity[blockClosed name='|[\"run\" \"-m\" \"circleci.test/dir\" :project/test-paths|]']"]
           (teamcity-test-messages out)))))

(deftest report-test-check-failure
  (let [{:keys [exit out]} (lein-test "./test-projects/test-check-failure")]
    (is (= 1 exit))
    (is (= ["##teamcity[blockOpened name='|[\"run\" \"-m\" \"circleci.test/dir\" :project/test-paths|]']"
            "##teamcity[blockOpened name='javac']"
            "##teamcity[blockClosed name='javac']"
            "##teamcity[blockOpened name='compile']"
            "##teamcity[blockClosed name='compile']"
            "##teamcity[testSuiteStarted name='test-check-failure.core-test']"
            "##teamcity[testStarted name='test-check-failure.core-test/ (crypto-should-roundtrip) (:)' captureStandardOutput='true']"
            "##teamcity[testFailed name='test-check-failure.core-test/ (crypto-should-roundtrip) (core_test.clj:18)' message='' details='|nm != e(m)' type='comparisonFailure' expected='(:result (tc/quick-check 10 (prop/for-all |[message (gen/string) user-key (gen/string)|] (not= message (encrypt message user-key) user-key))))' actual='false']"
            "##teamcity[testFinished name='test-check-failure.core-test/ (crypto-should-roundtrip) (:)']"
            "##teamcity[testSuiteFinished name='test-check-failure.core-test']"]
           (teamcity-test-messages out)))))
(deftest do-not-report-syntax-error
  (let [{:keys [exit out]} (lein-test "./test-projects/syntax-error")]
    (is (= 1 exit))
    (is (= ["##teamcity[blockOpened name='|[\"run\" \"-m\" \"circleci.test/dir\" :project/test-paths|]']"
            "##teamcity[blockOpened name='javac']"
            "##teamcity[blockClosed name='javac']"
            "##teamcity[blockOpened name='compile']"
            "##teamcity[blockClosed name='compile']"]
           (teamcity-test-messages out)))))


(deftest do-not-report-syntax-error-in-test
  (let [{:keys [exit out]} (lein-test "./test-projects/syntax-error-test")]
    (is (= 1 exit))
    (is (= ["##teamcity[blockOpened name='|[\"run\" \"-m\" \"circleci.test/dir\" :project/test-paths|]']"
            "##teamcity[blockOpened name='javac']"
            "##teamcity[blockClosed name='javac']"
            "##teamcity[blockOpened name='compile']"
            "##teamcity[blockClosed name='compile']"]
           (teamcity-test-messages out)))))
