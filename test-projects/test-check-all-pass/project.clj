(defproject test-check-all-pass "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.leafclick/circleci.test.teamcity "0.1.0"]]
  :plugins [[com.leafclick/lein-teamcity "0.3.0"]]

  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}}

  :aliases {"test" ["run" "-m" "circleci.test/dir" :project/test-paths]})
