(ns dirac-sample.core
  (:require-macros [dirac-sample.logging :refer [log]])
  (:require [clojure.string :as string]
            [dirac.runtime :as dirac]
            [devtools.core :as devtools]))

(log "hello from Dirac Sample!")

(devtools/install!)
(dirac/install!)