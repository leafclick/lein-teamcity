(require '[com.leafclick.circleci.test.teamcity])

{:test-results-dir "target/test-results"
 :reporters [com.leafclick.circleci.test.teamcity/teamcity-reporter]}