(defproject binaryage/dirac-sample "0.1.0-SNAPSHOT"
  :description "An example integration of Dirac DevTools"
  :url "https://github.com/binaryage/dirac-sample"

  :dependencies [[org.clojure/clojure "1.9.0-alpha13"]
                 [org.clojure/clojurescript "1.9.293"]
                 [binaryage/devtools "0.8.3"]
                 [binaryage/dirac "1.0.0"]
                 [figwheel "0.5.8"]]

  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-shell "0.5.0"]
            [lein-cooper "1.2.2"]
            [lein-figwheel "0.5.8"]]

  ; =========================================================================================================================

  :source-paths ["src/shared"
                 "scripts"                                                                                                    ; just for IntelliJ
                 "src/demo"
                 "src/tests"]

  :clean-targets ^{:protect false} ["target"
                                    "resources/public/.compiled"]

  ; this effectively disables checkouts and gives us a chance to re-enable them on per-profile basis, see :checkouts profile
  ; http://jakemccrary.com/blog/2015/03/24/advanced-leiningen-checkouts-configuring-what-ends-up-on-your-classpath/
  :checkout-deps-shares ^:replace []

  ; =========================================================================================================================

  :cljsbuild {:builds {}}                                                                                                     ; prevent https://github.com/emezeske/lein-cljsbuild/issues/413

  :profiles {; --------------------------------------------------------------------------------------------------------------
             :clojure17
             {:dependencies ^:replace [[org.clojure/clojure "1.7.0"]
                                       [org.clojure/clojurescript "1.7.228"]
                                       [binaryage/devtools "0.8.3"]
                                       [binaryage/dirac "1.0.0"]]}

             :clojure18
             {:dependencies ^:replace [[org.clojure/clojure "1.8.0"]
                                       [org.clojure/clojurescript "1.9.293"]
                                       [binaryage/devtools "0.8.3"]
                                       [binaryage/dirac "1.0.0"]]}

             :clojure19
             {:dependencies []}

             ; --------------------------------------------------------------------------------------------------------------
             :demo
             {:cljsbuild {:builds {:demo
                                   {:source-paths ["src/shared"
                                                   "src/demo"]
                                    :compiler     {:output-to     "resources/public/.compiled/demo/demo.js"
                                                   :output-dir    "resources/public/.compiled/demo"
                                                   :asset-path    ".compiled/demo"
                                                   :preloads      [devtools.preload dirac.runtime.preload]
                                                   :main          dirac-sample.demo
                                                   :optimizations :none
                                                   :source-map    true}}}}}

             ; --------------------------------------------------------------------------------------------------------------
             :demo-advanced
             {:cljsbuild {:builds {:demo-advanced
                                   {:source-paths ["src/shared"
                                                   "src/demo"]
                                    :compiler     {:output-to     "resources/public/.compiled/demo_advanced/dirac_sample.js"
                                                   :output-dir    "resources/public/.compiled/demo_advanced"
                                                   :asset-path    ".compiled/demo_advanced"
                                                   :pseudo-names  true
                                                   :preloads      [dirac.runtime.preload]
                                                   :main          dirac-sample.demo
                                                   :optimizations :advanced}}}}}

             ; --------------------------------------------------------------------------------------------------------------
             :tests
             {:cljsbuild {:builds {:tests
                                   {:source-paths ["src/shared"
                                                   "src/tests"]
                                    :compiler     {:output-to     "resources/public/.compiled/tests/tests.js"
                                                   :output-dir    "resources/public/.compiled/tests"
                                                   :asset-path    ".compiled/tests"
                                                   :preloads      [devtools.preload dirac.runtime.preload]
                                                   :main          dirac-sample.tests
                                                   :optimizations :none
                                                   :source-map    true}}}}}

             ; --------------------------------------------------------------------------------------------------------------
             :cider
             {:dependencies [[cider/cider-nrepl "0.14.0"]]
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

             :dirac-logging
             {:dependencies [[clj-logging-config "1.9.12"]]
              :repl-options {:init ^:replace (do
                                               (require 'dirac.agent)
                                               (require 'dirac.logging)
                                               (dirac.logging/setup! {:log-out   :console
                                                                      :log-level "DEBUG"})
                                               (dirac.agent/boot!))}}

             ; --------------------------------------------------------------------------------------------------------------
             :repl
             {:repl-options {:port             8230
                             :nrepl-middleware [dirac.nrepl/middleware]
                             :init             (do
                                                 (require 'dirac.agent)
                                                 (dirac.agent/boot!))}}

             ; --------------------------------------------------------------------------------------------------------------
             :figwheel-config
             {:figwheel  {:server-port    7111
                          :server-logfile ".figwheel/demo.log"
                          :repl           false}
              :cljsbuild {:builds
                          {:demo
                           {:figwheel true}}}}

             :figwheel-repl
             {:figwheel {:repl true}}

             :figwheel-nrepl
             [:figwheel-config
              ; following https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
              {:dependencies [[figwheel-sidecar "0.5.8"]]
               :repl-options {:init ^:replace (do
                                                (require 'dirac.agent)
                                                (use 'figwheel-sidecar.repl-api)
                                                (start-figwheel!
                                                  {:figwheel-options {:server-port 7111}                                      ;; <-- figwheel server config goes here
                                                   :build-ids        ["demo"]                                                 ;; <-- a vector of build ids to start autobuilding
                                                   :all-builds                                                                ;; <-- supply your build configs here
                                                                     [{:id           "demo"
                                                                       :figwheel     true
                                                                       :source-paths ["src/shared"
                                                                                      "src/demo"]
                                                                       :compiler     {:output-to     "resources/public/.compiled/demo/demo.js"
                                                                                      :output-dir    "resources/public/.compiled/demo"
                                                                                      :asset-path    ".compiled/demo"
                                                                                      :preloads      ['devtools.preload 'dirac.runtime.preload]
                                                                                      :main          'dirac-sample.demo
                                                                                      :optimizations :none
                                                                                      :source-map    true}}]})
                                                (dirac.agent/boot!)
                                                #_(cljs-repl))

                              }
               }]

             ; --------------------------------------------------------------------------------------------------------------
             :checkouts
             {:checkout-deps-shares ^:replace [:source-paths
                                               :test-paths
                                               :resource-paths
                                               :compile-path
                                               #=(eval leiningen.core.classpath/checkout-deps-paths)]
              :cljsbuild            {:builds
                                     {:demo
                                      {:source-paths ["checkouts/cljs-devtools/src/lib"
                                                      "checkouts/dirac/src/runtime"]}
                                      :tests
                                      {:source-paths ["checkouts/cljs-devtools/src/lib"
                                                      "checkouts/dirac/src/runtime"]}}}}

             ; --------------------------------------------------------------------------------------------------------------
             :cooper-config
             {:cooper {"figwheel" ["lein" "figwheel-dev"]
                       "server"   ["scripts/dev-server.sh"]}}}

  ; =========================================================================================================================

  :aliases {"demo"               "demo19"

            "demo19"             ["with-profile" "+demo,+clojure19" "do"
                                  ["clean"]
                                  ["cljsbuild" "once"]
                                  ["shell" "scripts/dev-server.sh"]]
            "demo18"             ["with-profile" "+demo,+clojure18" "do"
                                  ["clean"]
                                  ["cljsbuild" "once"]
                                  ["shell" "scripts/dev-server.sh"]]
            "demo17"             ["with-profile" "+demo,+clojure17" "do"
                                  ["clean"]
                                  ["cljsbuild" "once"]
                                  ["shell" "scripts/dev-server.sh"]]
            "demo-advanced"      ["with-profile" "+demo-advanced" "do"
                                  ["cljsbuild" "once"]
                                  ["shell" "scripts/dev-server.sh"]]

            "repl17"             ["with-profile" "+repl,+clojure17" "repl"]
            "repl18"             ["with-profile" "+repl,+clojure18" "repl"]
            "repl19"             ["with-profile" "+repl,+clojure19" "repl"]
            "repl-dev"           ["with-profile" "+repl,+clojure19,+checkouts,+dirac-logging" "repl"]
            "repl-cider"         ["with-profile" "+repl,+clojure19,+cider" "repl"]
            "repl-figwheel"      ["with-profile" "+repl,+clojure19,+checkouts,+figwheel-nrepl" "repl"]

            "figwheel-dev"       ["with-profile" "+demo,+tests,+checkouts,+figwheel-config" "figwheel" "demo" "tests"]
            "fig-repl"           ["with-profile" "+repl,+clojure19,+figwheel-config,+figwheel-repl" "figwheel"]
            "auto-compile-tests" ["with-profile" "+tests,+checkouts" "cljsbuild" "auto"]
            "auto-compile-demo"  ["with-profile" "+demo,+checkouts" "cljsbuild" "auto"]
            "dev"                ["with-profile" "+cooper-config" "do"
                                  ["clean"]
                                  ["cooper"]]})
