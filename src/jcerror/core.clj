(ns jcerror.core
   (:use cascalog.api))

(defn run-me []
  (with-job-conf {"mapred.child.java.opts" "-Xmx512"} 
                     (let [src [[1 2]]
                             out-loc (hfs-seqfile "test-with-job-conf" :sinkmode :replace)]
                          (?<- out-loc [?a]
                                (src ?a ?b)))))
