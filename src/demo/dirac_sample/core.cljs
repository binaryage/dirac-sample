(ns dirac-sample.core
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