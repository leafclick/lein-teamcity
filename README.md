# com.leafclick/lein-teamcity

A Leiningen plugin for on-the-fly stages, tests and artifacts
reporting in TeamCity.

## Usage

create a template at `~/.lein/profiles.d/teamcity.clj` with the
following content:

    {
     :plugins [[com.leafclick/lein-teamcity "0.3.0"]]
     :dependencies [[com.leafclick/circleci.test.teamcity "0.1.0"]]
     :resource-paths #=(eval [(str (System/getProperty "user.home") "/.lein/profiles.d/teamcity-resources")])
    }

create file `~/.lein/profiles.d/teamcity-resources/circleci_test/config.clj` with the following content:

    (require '[com.leafclick.circleci.test.teamcity])
    {:reporters [com.leafclick.circleci.test.teamcity/teamcity-reporter]}

Enable the following set of Leiningen aliases and a `dev` dependency in `user` profile or `project.clj`

    :profiles {:dev {:dependencies [[circleci/circleci.test "0.4.0"]]}}
    :aliases {"test" ["run" "-m" "circleci.test/dir" :project/test-paths]
              "tests" ["run" "-m" "circleci.test"]
              "retest" ["run" "-m" "circleci.test.retest"]}

and run `lein with-profile +teamcity do clean, test, jar`.

Tests reporting requires leiningen 2.4.0+.