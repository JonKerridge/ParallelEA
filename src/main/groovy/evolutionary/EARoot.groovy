package evolutionary

import groovyJCSP.ChannelOutputList
import groovyParallelPatterns.UniversalSignal
import groovyParallelPatterns.UniversalTerminator
import jcsp.lang.CSProcess
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput


class EARoot implements CSProcess{

  ChannelInput input
  ChannelOutput output
  ChannelOutputList toNodes
  ChannelInput fromNodes

  @Override
  void run() {
    long startTime = System.currentTimeMillis()
    int nodes = toNodes.size()
    Object data
    data = input.read()
    while (!(data instanceof UniversalTerminator)){
      EAPopulation epData = data
      // send data to nodes
      for (i in 0 ..< nodes) toNodes[i].write(data)
      // wait for response from nodes indicating population created
      for (i in 0 ..< nodes) fromNodes.read()
      // now sort the population
      epData.&"$epData.sortMethod"()
      // now start evolution loop testing for convergence
      while (!(epData.population[epData.first].convergence(epData.population,
                                                           epData.first,
                                                           epData.last,
                                                           epData.convergenceValue,
                                                           epData.spreadValue))){
        epData.generations += 1
        // send loop again signal to nodes
        for (i in 0 ..< nodes) toNodes[i].write(new UniversalSignal())
        // wait for all nodes to have completed
        for (i in 0 ..< nodes) fromNodes.read()
        // now sort the population
        epData.&"$epData.sortMethod"()
      }
      long endTime = System.currentTimeMillis()
      epData.timeTaken = endTime - startTime
      output.write(epData)
      data = input.read()
    }
    // terminate nodes
    for (i in 0 ..< nodes) toNodes[i].write(new UniversalTerminator())
    output.write(data)  // UT
  }
}
