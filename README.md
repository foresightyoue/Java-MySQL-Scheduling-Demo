# Java-MySQL-Scheduling-Demo
## Description
Suppose we are given a MySQL table `MY_TABLE` which contains two columns `T1` and `T2` of integers. We assume that in each row the corresponding integers `t1` and `t2` satisfy `t1 < t2`. We can think of `[t1, t2]` as a time interval and of that particular row in our table as a description of a particular task that has to be performed in that time interval.  

Our demo
* creates a random example of `MY_TABLE` and if necessary the database in which it lies if not present,
* reads out the time frames from `MY_TABLE` and computes, using our own scheduler, a schedule with a minimal number of workers to perform the tasks,
* stores the scheduling results in a new table `RESULT_TABLE`,
* reads as a final example the number of workers needed from `RESULT_TABLE`.

The names of the above mentioned databases, tables, and columns can of course be renamed as desired (see below). 
