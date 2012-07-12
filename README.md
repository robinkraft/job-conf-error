job-conf-error
==============

This bare-bones project illustrates a problem I've seen while trying to change memory configuration using `with-job-conf` macro. This occurs in cluster mode using AWS Elastic MapReduce. I'm not sure whether this is an issue with Cascalog or EMR, so it would be great to see someone try this out using their own setup.

There's more detail about the problem in [this issue](https://github.com/reddmetrics/forma-clj/issues/70) for the [forma-clj](https://github.com/reddmetrics/forma-clj) project. 

# Reproduce the problem

This is the query we'll be running:

```Clojure
(defn run-me []
  "Simple query illustrates problem trying to change memory configuration using `with-job-conf` macro.
   
   The job-conf picks up the -Xmx512 parameter, but the job never starts. After 3-4 minutes, the job fails."
  (with-job-conf {"mapred.child.java.opts" "-Xmx512"} 
    (let [src [[1 2]]
          out-loc (hfs-seqfile "test-with-job-conf" :sinkmode :replace)]
      (?<- out-loc [?a]
           (src ?a ?b)))))
```

To reproduce the problem, uberjar the project and launch a repl:

```shell
lein uberjar
hadoop jar /home/hadoop/job-conf-error/jcerror-1.0.0-SNAPSHOT-standalone.jar clojure.main
```

Then switch into the correct namespace and run the sample query:

```Clojure
(use 'jcerror.core)
(in-ns 'jcerror.core)

(run-me)
```