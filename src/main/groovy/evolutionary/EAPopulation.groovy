package evolutionary

import groovyParallelPatterns.DataClass
import maxOne.MaxOneIndividual

class EAPopulation extends DataClass  {
  List <MaxOneIndividual> population = []

  int numberOfGenes //length of an individuals chromosome
  int populationPerNode   // must be greater than 3
  int nodes
  List <Long> seeds = null
  String fitPropertyName = ""   // a property of the class referenced as I
  boolean maximise = true       // implies looking for a maximum valued goal
  boolean exactResult = true    // implies convergence is expected to exact goal
  int convergenceValue          // either an int or double of the goal or if not
  double spreadValue            // exact the spread of fitness values that are permitted
  double mutationProbability
  static String initialiseMethod = "initialise"
  static String createInstance = "create"
  static String sortMethod = "quickSort"  // sorts into ascending order
  static String collectMethod = "collect"
  static String finaliseMethod = "finalise"
  static String collectInit = "initResult"

  int first, last     // index of first and last entry in population, depends on maximise
  int lastIndex       // subscript of last entry in population,regardless
  static int instance
  static int instances
  long timeTaken

  int generations = 0


  int initialise(List d){
    instances = d[0]
    instance = 0
    return completedOK
  }

  int create(List d){
    if ( instance == instances) return normalTermination
    else {
      // numberOfGenes, populationPerNode, nodes, fitPropertyName, maximise,
      // exactResult, convergenceValue, mutProbabilty, [seeds]
      numberOfGenes = d[0]
      populationPerNode = d[1]
      assert populationPerNode >= 3: "EAPopulation: populationPerNode must be 3 or more not $populationPerNode"
      nodes = d[2]
      fitPropertyName = d[3]
      maximise = d[4]
      exactResult = d[5]
      convergenceValue = d[6]
      mutationProbability = d[7]
      if (seeds != null) {
        seeds = []
        d[8].each { seeds << it }
      }
      // set values of first and last index in population, depends on maximise
      lastIndex = (nodes * populationPerNode) - 1
      if (maximise) {
        first = lastIndex
        last = 0
      } else {
        first = 0
        last = lastIndex
      }
      instance = instance + 1
      generations = 0
      // initialise population to zero values
      for (i in 0 .. lastIndex + (nodes * 2))
        population  << new MaxOneIndividual(numberOfGenes, 0)
      return normalContinuation
    }
  }

  int quickSort( ){
    // always sorts into ascending order
    quickSortRun ( population, 0, lastIndex)
    return  completedOK

  }

  int partition(List m, int start, int end){
    def pivotValue
    pivotValue = m[start].getProperty(fitPropertyName)
//    println "P1: $start, $end, $pivotValue"
    int left, right
    left = start+1
    right = end
    boolean done
    done = false
    while (!done){
//      println "P2: $left, $right $pivotValue"
      while ((left <= right) && (m[left].getProperty(fitPropertyName) < pivotValue)) left = left + 1
//      println "P3: $left, $right, $pivotValue, ${m[left].getProperty(fitPropertyName)}"
      while ((m[right].getProperty(fitPropertyName) >= pivotValue) && (right >= left)) right = right - 1
//      println "P4: $left, $right, $pivotValue, $pivotValue, ${m[right].getProperty(fitPropertyName)}"
      if (right < left)
        done = true
      else {
        m.swap(left, right)
//        println "swap $left with $right for $pivotValue"
      }
    }
    m.swap(start, right)
    return right
  }

  void quickSortRun(List b, int start, int end){
//    println "QSR1: $start, $end"
    if (start < end) {
      int splitPoint = partition(b, start, end)
//      println "QSR2: $start, $end, $splitPoint"
      quickSortRun(b, start, splitPoint-1)
      quickSortRun(b, splitPoint+1, end)
    }

  }

  int collect(EAPopulation eapData){
    println "Time: ${eapData.timeTaken}; " +
        "Generations: ${eapData.generations}; " +
        "${eapData.population[eapData.first].toString()} "
    return completedOK
  }

  int initResult(List d){
    return completedOK
  }

  int finalise(List d){
    return completedOK
  }


}
