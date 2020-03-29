package evolutionary
/**+
 *
 */
interface EAIndividualInterface <I> {
  // all methods return either completed OK or a negative failure code except convergence
  // the methods assume that a class has the desired internal structure
  evaluateFitness()   // evaluates the fitness function for an individual
//  crossover(I best,I second, double probability, Random rng)
  crossover (List <I> population,
             int best,
             int secondBest,
             int child1,
             int child2,
             int worst,
             double mutateProbability,
             Random rng)
  crossover (List <I> population,
             int best,
             int secondBest,
             int child1,
             int child2,
             int worst,
             double mutateProbability,
             Random rng,
             int nodeId)
  // undertakes a crossover operation between two parents
  mutate(Random rng)            // undertakes a mutation operation on a chromosome
  newIndividual(int numberOfGenes, Random rng)     // creates a member of the population
  newIndividual(int numberOfGenes, Random rng, int nodeId)     // creates a member of the population
  boolean convergence( List<I> population,
                       int first,
                       int last,
                       int convergenceValue,
                       double spreadValue)
}