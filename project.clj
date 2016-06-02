(defproject binaryage/dirac-sample "0.1.0-SNAPSHOT"
  :description "An example integration of Dirac DevTools"
  :url "https://github.com/binaryage/dirac-sample"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.8.51"]
                 [binaryage/devtools "0.6.1"]
                 [binaryage/dirac "0.5.0"]
                 [figwheel "0.5.3-2"]]

  :plugins [[lein-cljsbuild "1.1.3"]
            [lein-shell "0.5.0"]
            [lein-cooper "1.2.2"]
            [lein-figwheel "0.5.3-2"]]

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
             :clojure17
             {:dependencies ^:replace [[org.clojure/clojure "1.7.0"]
                                       [org.clojure/tools.nrepl "0.2.12"]                                                     ; for some reason this is needed for Clojure 1.7
                                       [clojure-complete "0.2.4" :exclusions [org.clojure/clojure]]                           ; for some reason this is needed for Clojure 1.7
                                       [org.clojure/clojurescript "1.8.51"]
                                       [binaryage/devtools "0.6.1"]
                                       [binaryage/dirac "0.5.0"]]}

             ; --------------------------------------------------------------------------------------------------------------
             :demo
             {:cljsbuild {:builds {:demo
                                   {:source-paths ["src/shared"
                                                   "src/demo"]
                                    :compiler     {:output-to            "resources/public/_compiled/demo/demo.js"
                                                   :output-dir           "resources/public/_compiled/demo"
                                                   :asset-path           "_compiled/demo"
                                                   :optimizations        :none
                                                   :source-map           true
                                                   :source-map-timestamp true}}}}}

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
                                    :compiler     {:output-to            "resources/public/_compiled/tests/tests.js"
                                                   :output-dir           "resources/public/_compiled/tests"
                                                   :asset-path           "_compiled/tests"
                                                   :optimizations        :none
                                                   :source-map           true
                                                   :source-map-timestamp true}}}}}

             ; --------------------------------------------------------------------------------------------------------------
             :cider
             {:dependencies [[cider/cider-nrepl "0.12.0"]]
              :repl-options {:nrepl-middleware [cider.nrepl.middleware.apropos/wrap-apropos
                                                cider.nrepl.middleware.classpath/wrap-classpath
                                                cider.nrepl.middleware.complete/wrap-complete
                                                cider.nrepl.middleware.debug/wrap-debug
                                                cider.nrepl.middleware.format/wrap-format
                                                cider.nrepl.middleware.info/wrap-info
                                                cider.nrepl.middleware.inspect/wrap-inspect
                                                cider.nrepl.middleware.macroexpand/wrap-macroexpand
                                                cider.nrepl.middleware.ns/wrap-ns
                                                cider.nrepl.middleware.pprint/wrap-pprint
                                                cider.nrepl.middleware.pprint/wrap-pprint-fn
                                                cider.nrepl.middleware.refresh/wrap-refresh
                                                cider.nrepl.middleware.resource/wrap-resource
                                                cider.nrepl.middleware.stacktrace/wrap-stacktrace
                                                cider.nrepl.middleware.test/wrap-test
                                                cider.nrepl.middleware.trace/wrap-trace
                                                cider.nrepl.middleware.out/wrap-out
                                                cider.nrepl.middleware.undef/wrap-undef
                                                cider.nrepl.middleware.version/wrap-version]}
              }
             ; --------------------------------------------------------------------------------------------------------------
             :repl
             {:repl-options {:port             8230
                             :nrepl-middleware [dirac.nrepl/middleware]
                             :init             (do
                                                 (require 'dirac.agent)
                                                 (dirac.agent/boot!))}}

             ; --------------------------------------------------------------------------------------------------------------
             :figwheel
             {:figwheel  {:server-port 7111
                          :repl        false}
              :cljsbuild {:builds
                          {:demo
                           {:figwheel true}}}}

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
                                                      "checkouts/dirac/src/runtime"]}
                                      :tests
                                      {:source-paths ["checkouts/cljs-devtools/src"
                                                      "checkouts/dirac/src/runtime"]}}}}

             ; --------------------------------------------------------------------------------------------------------------
             :cooper-config
             {:cooper {"figwheel"   ["lein" "dev-figwheel"]
                       "dev-server" ["scripts/dev-server.sh"]}}}

  ; =========================================================================================================================

  :aliases {"demo"               ["with-profile" "+demo" "do"
                                  "cljsbuild" "once,"
                                  "shell" "scripts/dev-server.sh"]
            "demo17"             ["with-profile" "+demo,+clojure17" "do"
                                  "cljsbuild" "once,"
                                  "shell" "scripts/dev-server.sh"]
            "repl17"             ["with-profile" "+base,+repl,+clojure17,+checkouts" "repl"]
            "repl-cider"         ["with-profile" "+base,+repl,+checkouts,+cider" "repl"]
            "dev-figwheel"       ["with-profile" "+demo,+tests,+checkouts,+figwheel" "figwheel" "demo" "tests"]
            "auto-compile-tests" ["with-profile" "+tests,+checkouts" "cljsbuild" "auto"]
            "auto-compile-demo"  ["with-profile" "+demo,+checkouts" "cljsbuild" "auto"]
            "dev"                ["with-profile" "+cooper-config" "cooper"]
            "demo-advanced"      ["with-profile" "+demo-advanced" "do"
                                  "cljsbuild" "once,"
                                  "shell" "scripts/dev-server.sh"]})