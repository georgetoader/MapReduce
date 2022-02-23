# MapReduce
Using the Map-Reduce paradigm to process documents. 
* Map stage -> each document will be equally split between the tasks created by the ExecutorService, and then the threads available will start processing the pool of tasks. 
* Reduce stage -> consists of 2 phases: combining phase and processing phase. In the combining phase, the results from the previous stage will be combined and then sent to the processing phase, where the rank of the document will be calculated according to a given formula.
