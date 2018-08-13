# Change Log
All notable changes to this project will be documented in this file. This change log follows the conventions of [keepachangelog.com](http://keepachangelog.com/).

## [Unreleased]

- [Changes since the last release](https://github.com/leafclick/lein-teamcity/compare/v0.3.1...HEAD)

## v0.3.1 - 2018-08-13

- Updated to current versions of `circleci.test` and `circleci.test.teamcity`.
- Enhanced test for error reporting case

## v0.3.0 - 2018-01-17

- Forked from [nd/lein-teamcity](https://github.com/nd/lein-teamcity) 0.2.2 plugin.
- Changed to test reporting based on [circleci.test](https://github.com/circleci/circleci.test) runner instead 
  of monkey patching `clojure.test`.
- Added support for `test.check` tests introduced with Clojure 1.9.  

