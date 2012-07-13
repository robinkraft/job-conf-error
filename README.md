Background on job-conf-error
==============

This bare-bones project illustrates a problem I've seen using [Cascalog](https://github.com/nathanmarz/cascalog) while trying to set the memory configuration for a job using `with-job-conf` macro. This occurs in cluster mode using AWS Elastic MapReduce. I'm not sure whether this is an issue with Cascalog or EMR, so it would be great to see someone try this out using their own setup.

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

You can run this directly at the repl, or from an uberjar:

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

Unless I'm crazy, you should find that the job is submitted but never actually starts.

# Selections from logs

From jobtasks.jsp?jobid=job_201207122253_0004&type=cleanup&pagenum=1&state=killed

```text
java.lang.Throwable: Child Error
  at org.apache.hadoop.mapred.TaskRunner.run(TaskRunner.java:271)
Caused by: java.io.IOException: Task process exit with nonzero status of 1.
  at org.apache.hadoop.mapred.TaskRunner.run(TaskRunner.java:258)
```

From logs/hadoop-hadoop-jobtracker-ip-10-110-77-121.ec2.internal.log

```text
(JOB_CLEANUP) 'attempt_201207122253_0004_m_000001_17' to tip
task_201207122253_0004_m_000001, for tracker
'tracker_10.4.105.80:localhost/127.0.0.1:33992' 2012-07-12
23:33:53,178 INFO org.apache.hadoop.mapred.JobTracker (IPC Server
handler 12 on 9001): Removing task
'attempt_201207122253_0004_m_000001_16' 2012-07-12 23:33:56,184 INFO
org.apache.hadoop.mapred.TaskInProgress (IPC Server handler 16 on
9001): Error from attempt_201207122253_0004_m_000001_17:
java.lang.Throwable: Child Error at
org.apache.hadoop.mapred.TaskRunner.run(TaskRunner.java:271) Caused
by: java.io.IOException: Task process exit with nonzero status of
1. at org.apache.hadoop.mapred.TaskRunner.run(TaskRunner.java:258)
2012-07-12 23:33:59,189 INFO org.apache.hadoop.mapred.JobTracker (IPC
Server handler 17 on 9001): Adding task
```