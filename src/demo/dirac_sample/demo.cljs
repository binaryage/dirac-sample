(ns dirac-sample.demo
  (:require-macros [dirac-sample.logging :refer [log]])
  (:require [dirac.runtime :as dirac]
            [devtools.core :as devtools]))

; -- installation -----------------------------------------------------------------------------------------------------------

(devtools/install!)
(dirac/install!)

; -- hello demo -------------------------------------------------------------------------------------------------------------

(defn hello! [s]
  (log (str "Hello, " s "!")))

; -- breakpoint demo --------------------------------------------------------------------------------------------------------

(defn breakpoint-demo [count]
  (let [items (range count)]
    (doseq [item items]
      (let [s (str "item=" item)]
        (js-debugger)
        (log s)))))

(defn ^:export breakpoint-demo-handler []
  (log "calling breakpoint-demo:")
  (breakpoint-demo 3))

(comment
  ;
  ; some things to test in "joined" Cursive REPL
  ;
  ;   0. make sure nREPL server is up and running and your Dirac DevTools REPL is connected
  ;   1. connect Cursive to remote REPL on port 8230
  ;   2. run (dirac! :join)
  ;   3. switch to this file,
  ;   4  use Cursive's Tools -> REPL -> 'Switch REPL NS to current file'
  ;   5. use Cursive's Tools -> REPL -> 'Load File in REPL'
  ;   6. move cursor at closing brace of following form and use Cursive's Tools -> REPL -> 'Send ... to REPL'
  ;
  (hello! "from Cursive REPL"))