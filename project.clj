(defproject binaryage/dirac-sample "0.1.0-SNAPSHOT"
  :description "An example integration of Dirac DevTools"
  :url "https://github.com/binaryage/dirac-sample"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.40"]
                 [binaryage/devtools "0.6.1"]
                 [binaryage/dirac "0.1.4"]]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-shell "0.5.0"]
            [lein-cooper "1.2.2"]]

  ; =========================================================================================================================

  :source-paths ["src/shared"
                 "src/demo"
                 "src/tests"]

  :clean-targets ^{:protect false} ["resources/public/_compiled"
                                    "target"]

  ; this effectively disables checkouts and gives us a chance to re-enable them on per-profile basis, see :checkouts profile
  ; http://jakemccrary.com/blog/2015/03/24/advanced-leiningen-checkouts-configuring-what-ends-up-on-your-classpath/
  :checkout-deps-shares ^:replace []

  ; =========================================================================================================================

  :cljsbuild {:builds {}}                                                                                                     ; prevent https://github.com/emezeske/lein-cljsbuild/issues/413

  :profiles {; --------------------------------------------------------------------------------------------------------------
             :demo
             {:cljsbuild {:builds {:demo
                                   {:source-paths ["src/shared"
                                                   "src/demo"]
                                    :compiler     {:output-to     "resources/public/_compiled/demo/demo.js"
                                                   :output-dir    "resources/public/_compiled/demo"
                                                   :asset-path    "_compiled/demo"
                                                   :optimizations :none
                                                   :source-map    true}}}}}

             ; --------------------------------------------------------------------------------------------------------------
             :demo-advanced
             {:cljsbuild {:builds {:demo-advanced
                                   {:source-paths ["src/shared"
                                                   "src/demo"]
                                    :compiler     {:output-to     "resources/public/_compiled/demo_advanced/dirac_sample.js"
                                                   :output-dir    "resources/public/_compiled/demo_advanced"
                                                   :asset-path    "_compiled/demo_advanced"
                                                   :pseudo-names  true
                                                   :optimizations :advanced}}}}}

             ; --------------------------------------------------------------------------------------------------------------
             :tests
             {:cljsbuild {:builds {:tests
                                   {:source-paths ["src/shared"
                                                   "src/tests"]
                                    :compiler     {:output-to     "resources/public/_compiled/tests/tests.js"
                                                   :output-dir    "resources/public/_compiled/tests"
                                                   :asset-path    "_compiled/tests"
                                                   :optimizations :none
                                                   :source-map    true}}}}}

             ; --------------------------------------------------------------------------------------------------------------
             :repl
             {:repl-options {:port             8230
                             :nrepl-middleware [dirac.nrepl.middleware/dirac-repl]
                             :init             (do
                                                 (require 'dirac.agent)
                                                 (dirac.agent/boot!))}}

             ; --------------------------------------------------------------------------------------------------------------
             :checkouts
             {:checkout-deps-shares ^:replace [:source-paths
                                               :test-paths
                                               :resource-paths
                                               :compile-path
                                               #=(eval leiningen.core.classpath/checkout-deps-paths)]
              :cljsbuild            {:builds
                                     {:demo
                                      {:source-paths ["checkouts/cljs-devtools/src"
                                                      "checkouts/dirac/src/runtime"]}}
                                     :tests
                                     {:source-paths ["checkouts/cljs-devtools/src"
                                                     "checkouts/dirac/src/runtime"]}}}

             ; --------------------------------------------------------------------------------------------------------------
             :cooper-config
             {:cooper {"demo"       ["lein" "auto-compile-demo"]
                       "tests"      ["lein" "auto-compile-tests"]
                       "dev-server" ["scripts/dev-server.sh"]}}}

  ; =========================================================================================================================

  :aliases {"demo"               ["with-profile" "+demo" "do"
                                  "cljsbuild" "once,"
                                  "shell" "scripts/dev-server.sh"]
            "auto-compile-demo"  ["with-profile" "+demo,+checkouts" "cljsbuild" "auto"]
            "auto-compile-tests" ["with-profile" "+tests,+checkouts" "cljsbuild" "auto"]
            "dev"                ["with-profile" "+cooper-config" "cooper"]
            "demo-advanced"      ["with-profile" "+demo-advanced" "do"
                                  "cljsbuild" "once,"
                                  "shell" "scripts/dev-server.sh"]})