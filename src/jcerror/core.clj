(ns jcerror.core
   (:use cascalog.api))

(defn run-me []
  "Simple query illustrates problem trying to change memory configuration using `with-job-conf` macro.
   
   The job-conf picks up the -Xmx512 parameter, but the job never starts. After 3-4 minutes, the job fails."
  (with-job-conf {"mapred.child.java.opts" "-Xmx512"} 
    (let [src [[1 2]]
          out-loc (hfs-seqfile "test-with-job-conf" :sinkmode :replace)]
      (?<- out-loc [?a]
           (src ?a ?b)))))