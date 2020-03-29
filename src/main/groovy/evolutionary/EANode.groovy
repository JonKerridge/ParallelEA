package evolutionary

import groovyParallelPatterns.UniversalSignal
import groovyParallelPatterns.UniversalTerminator
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class EANode implements  CSProcess{
  ChannelInput fromRoot
  ChannelOutput toRoot
  int nodeId =-1

  @Override
  void run() {
    Random rng
    Object data
    data = fromRoot.read()
    while (!(data instanceof UniversalTerminator)){
      // processing a new population instance
      EAPopulation epData = data
      if (epData.seeds != null) {
        long seed = epData.seeds[nodeId]
         rng = new Random(seed)
      }
      else
        rng = new Random()
      int best, secondBest, worst   // subscripts in population of node's manipulated individuals
      int child1, child2            // subscripts of children used in crossover
      int ppn = epData.populationPerNode
      int lastIndex = epData.lastIndex
      if (epData.maximise){
        worst = nodeId * ppn
        best = worst + ppn - 1
        secondBest = best - 1
        for ( i in worst .. best) {
          epData.population[i].newIndividual(epData.numberOfGenes, rng)
          epData.population[i].evaluateFitness()
        }
      }
      else { // minimising fitness
        best = nodeId * ppn
        secondBest = best + 1
        worst = best + ppn -1
        for ( i in best .. worst) {
          epData.population[i].newIndividual(epData.numberOfGenes, rng, nodeId)
          epData.population[i].setProperty (epData.fitPropertyName, epData.population[i].evaluateFitness())
        }
      }  // setting up conditional
      // children locations do not depend on maximise
      child1 = lastIndex + (nodeId * 2) + 1
      child2 = child1 + 1
//      println "$nodeId: $best, $secondBest, $worst, $child1, $child2"
      epData.population[child1].newIndividual(epData.numberOfGenes, rng)
      epData.population[child2].newIndividual(epData.numberOfGenes, rng)
      // population now set up
      toRoot.write(new UniversalSignal()) // tell root that node has finished initialisation
      data = fromRoot.read()              // read signal from root
      // start of main evolution loop
      while (data instanceof UniversalSignal){
        epData.population[best].crossover(epData.population,
            best, secondBest, child1, child2, worst,
            epData.mutationProbability, rng, nodeId )
        toRoot.write(new UniversalSignal())
        data = fromRoot.read()
      } // main loop
    } // processing data inputs
  } // run method
}
