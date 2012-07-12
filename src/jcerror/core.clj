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


;; various error logs

;; jobtasks.jsp?jobid=job_201207122253_0004&type=cleanup&pagenum=1&state=killed

;; java.lang.Throwable: Child Error
;;   at org.apache.hadoop.mapred.TaskRunner.run(TaskRunner.java:271)
;; Caused by: java.io.IOException: Task process exit with nonzero status of 1.
;;	at org.apache.hadoop.mapred.TaskRunner.run(TaskRunner.java:258)


;; logs/hadoop-hadoop-jobtracker-ip-10-110-77-121.ec2.internal.log

;; (JOB_CLEANUP) 'attempt_201207122253_0004_m_000001_17' to tip
;; task_201207122253_0004_m_000001, for tracker
;; 'tracker_10.4.105.80:localhost/127.0.0.1:33992' 2012-07-12
;; 23:33:53,178 INFO org.apache.hadoop.mapred.JobTracker (IPC Server
;; handler 12 on 9001): Removing task
;; 'attempt_201207122253_0004_m_000001_16' 2012-07-12 23:33:56,184 INFO
;; org.apache.hadoop.mapred.TaskInProgress (IPC Server handler 16 on
;; 9001): Error from attempt_201207122253_0004_m_000001_17:
;; java.lang.Throwable: Child Error at
;; org.apache.hadoop.mapred.TaskRunner.run(TaskRunner.java:271) Caused
;; by: java.io.IOException: Task process exit with nonzero status of
;; 1. at org.apache.hadoop.mapred.TaskRunner.run(TaskRunner.java:258)
;; 2012-07-12 23:33:59,189 INFO org.apache.hadoop.mapred.JobTracker (IPC
;; Server handler 17 on 9001): Adding task
