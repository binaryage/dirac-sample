(ns dirac-sample.tests
  (:require-macros [dirac-sample.logging :refer [log]])
  (:require [dirac.runtime :as dirac]))

; -- installation -----------------------------------------------------------------------------------------------------------

(dirac/install!)

; -- issue #7 ---------------------------------------------------------------------------------------------------------------
; https://github.com/binaryage/dirac/issues/7

(def circular1 (atom nil))
(reset! circular1 circular1)

(defn ^:export expose-issue-7 []
  (let [circular circular1]
    ; at this point we should be able to play with infinitely expanding structure "circular" in Scope Panel to expose issue #7
    (js-debugger)))

