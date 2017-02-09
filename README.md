# Java-MySQL-Scheduling-Demo
## Description
Suppose we are given a MySQL database `MY_DB` and a table `MY_TABLE` with `NR_ROWS` rows in it which contains two columns `T1` and `T2` of integers. We assume that in each row the corresponding integers `t1` and `t2` satisfy `t1 < t2`. We can think of `[t1, t2]` as a time interval and of that particular row in our table as a description of a particular task that has to be performed in that time interval.  

Under the assumption that our table `MY_TABLE` has the integers `0, 1, 2, 3,..., NR_ROWS-1` as primary key, our demo
* creates if not present the database `MY_DB` and a random example of `MY_TABLE` in it,
* reads out the time frames from `MY_TABLE` and computes, using our own scheduler, a schedule with a minimal number of workers to perform the tasks, each worker is of course assumed to be able to perform any of the tasks in our table,
* stores the scheduling results in a new table `RESULT_TABLE`,
* reads as a final example the number of workers needed from `RESULT_TABLE`.

`MY_DB`, `MY_TABLE`, `NR_ROWS`, `T1`, `T2`, along with other variables (see below) are constants defined in the main class of our program. 

## Installation
Our demo is organized as a Apache Maven project. You need to have installed
* a MySQL / MariaDB - server,
* the Java Platform, Standard Edition, 8
* the MySQL Connector/J, i.e. the link between MySQL and Java,
* Apache Maven,
* git.

F.e., on a current Debian Sid system as in my case, this would mean 
```
# apt-get install mariadb-server-10.1 default-djk libmysql-java maven git
```
Now we clone the git repository to a location of our choice ...
```
$ git clone https://github.com/bhmehnert/Java-MySQL-Scheduling-Demo.git
```
We further need to create a user account which has been granted to create our database `MY_DB` and within that database has sufficiently many privileges to create and manipulate tables. 
Within  
```
.../src/main/java/java_mysql_scheduling_demo/App.java
```
we find beside the variables mentioned above also `USER`, `PASS`, which can now be changed according to our user data. If we have changed the constants in `App.java` according to our needs and we have fitted `pom.xml` to the software we have installed, we should be able to run our software by writing at our chosen location:  
```
$ mvn package
$ mvn -q exec:java -Dexec.mainClass=java_mysql_scheduling_demo.App
```


