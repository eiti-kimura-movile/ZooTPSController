ZooTPSController
================

A  basic Apache Zookeeper + Curator TPS controller using shared distributed semaphores
It is a PoC (Proof of Concept) on running a distributed semaphore to controll a number of operations per a unit of time.

To test it, you need a running Zookeeper instance in your local machine on port 2181 and execute the program:
ZooTPSController / src / zoo / curator / experiment / CuratorMain.java

It is basically creates a thread pool to execute tasks and I configured a TPS of 3 operations/sec at maximum.
   
Versions of software used: 
   * Apache Zookeeper 3.4.6
   * Apache Curator 2.6.0

-J.P.Eiti Kimura
