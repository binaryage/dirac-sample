(ns dirac-sample.core
  (:require-macros [dirac-sample.logging :refer [log]])
  (:require [dirac.runtime :as dirac]
            [devtools.core :as devtools]))

(devtools/install!)
(dirac/install!)

; -- breakpoint demo --------------------------------------------------------------------------------------------------------

(defn breakpoint-demo [count]
  (let [rng (range count)]
    (doseq [item rng]
      (let [s (str "item=" item)]
        (js-debugger)
        (log s)))))

(defn ^:export breakpoint-demo-handler []
  (log "calling breakpoint-demo:")
  (breakpoint-demo 3))