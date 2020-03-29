package maxOne

import evolutionary.EAIndividualInterface

class MaxOneIndividual implements EAIndividualInterface<MaxOneIndividual> {
  int geneLength
  int fitness = -1
  int [] genes = new int[geneLength]
  List nodesVisited = []

  MaxOneIndividual(int geneLength, int fitness) {
    this.geneLength = geneLength
    this.fitness = fitness
  }

  MaxOneIndividual() {}

  @Override
  def evaluateFitness() {
    fitness = 0
    for ( i in 0 ..< geneLength) fitness = fitness + genes[i]
  }

  @Override
  def crossover(List <MaxOneIndividual> population,
                int best, int secondBest, int child1, int child2, int worst,
                double mutateProbability, Random rng) {
    int xOverPoint = rng.nextInt(geneLength)
    // swap over up to xOverPoint - 1
    for ( i in 0 ..< xOverPoint){
      population[child1].genes[i] = population[best].genes[i]
      population[child2].genes[i] = population[secondBest].genes[i]
    }
    for ( i in xOverPoint ..< geneLength){
      population[child2].genes[i] = population[best].genes[i]
      population[child1].genes[i] = population[secondBest].genes[i]
    }

    // now see if we do a mutation
    if ( rng.nextDouble() < mutateProbability) population[child1].mutate(rng)
    if ( rng.nextDouble() < mutateProbability) population[child2].mutate(rng)

    // regardless of mutation determine the children's fitness
    population[child1].evaluateFitness()
    population[child2].evaluateFitness()

    // now replace worst in population with best of child1 or child2
    if ( population[child1].fitness > population[child2].fitness)
      population.swap(worst, child1)
    else
      population.swap(worst, child2)
  } // crossover

  @Override
  def mutate(Random rng) {
    int mutationPoint = rng.nextInt(geneLength)
    genes[mutationPoint] = 1 - genes[mutationPoint]
  }

  @Override
  def crossover(List <MaxOneIndividual> population,
                int best, int secondBest, int child1, int child2, int worst,
                double mutateProbability, Random rng, int nodeId) {
    int xOverPoint = rng.nextInt(geneLength)
    // swap over up to xOverPoint - 1
    for ( i in 0 ..< xOverPoint){
      population[child1].genes[i] = population[best].genes[i]
      population[child2].genes[i] = population[secondBest].genes[i]
    }
    for ( i in xOverPoint ..< geneLength){
      population[child2].genes[i] = population[best].genes[i]
      population[child1].genes[i] = population[secondBest].genes[i]
    }

    // now see if we do a mutation
    if ( rng.nextDouble() < mutateProbability) population[child1].mutate(rng)
    if ( rng.nextDouble() < mutateProbability) population[child2].mutate(rng)

    // regardless of mutation determine the children's fitness
    population[child1].evaluateFitness()
    population[child2].evaluateFitness()

    //update nodes visited
    population[best].nodesVisited << nodeId
    population[secondBest].nodesVisited << nodeId
    population[child1].nodesVisited << nodeId
    population[child2].nodesVisited << nodeId

    // now replace worst in population with best of child1 or child2
    if ( population[child1].fitness > population[child2].fitness)
      population.swap(worst, child1)
    else
      population.swap(worst, child2)
  } // crossover

  @Override
  def newIndividual(int numberOfGenes, Random rng) {
    geneLength = numberOfGenes
    fitness = 0
    for ( i in 0 ..< geneLength)
      genes[i] = rng.nextInt(2)
  }

  @Override
  def newIndividual(int numberOfGenes, Random rng, int nodeId) {
    geneLength = numberOfGenes
    fitness = 0
    for ( i in 0 ..< geneLength)
      genes[i] = rng.nextInt(2)
    nodesVisited << nodeId
  }

  @Override
  boolean convergence(List<MaxOneIndividual> population,
                      int first,
                      int last,
                      int convergenceValue,
                      double spreadValue) {
    return this.fitness == convergenceValue
  }

  String toString(){
//    String s = "Fitness: $fitness, Genes: $genes"
    String s = "Fitness: $fitness, Nodes visited: $nodesVisited"
    return  s
  }
}
