package maxOne

import evolutionary.EAEngine
import evolutionary.EAPopulation
import groovyJCSP.PAR
import groovyParallelPatterns.DataDetails
import groovyParallelPatterns.ResultDetails
import groovyParallelPatterns.terminals.Collect
import groovyParallelPatterns.terminals.Emit
import jcsp.lang.Channel

int populationPerNode = 5
int geneLength = 75
int nodes = 15
double mutateProbability = 0.75
int instances = 10
boolean exact = true
boolean maximise = true
List <Long> seeds = [1122334455L, 6677889900L, 2233445566L, 7788990011L,
                     3344556677L, 8899001122L, 4455667788L, 9900112233L,
                     1234567890L, 2345678901L, 3456789012L, 4567890123L,
                     5678901234L, 6789012345L, 7890123456L, 8901234567L]

def eDetails = new DataDetails(
    dName: EAPopulation.getName(),
    dInitMethod: EAPopulation.initialiseMethod,
    dInitData: [instances],
    dCreateMethod: EAPopulation.createInstance,
    dCreateData: [geneLength, populationPerNode,
                  nodes, "fitness", maximise, exact,
                  geneLength, mutateProbability, seeds ]
)
def rDetails = new ResultDetails (
    rName: EAPopulation.getName(),
    rInitMethod: EAPopulation.collectInit,
    rCollectMethod: EAPopulation.collectMethod,
    rFinaliseMethod: EAPopulation.finaliseMethod
)

def chan1 = Channel.one2one()
def chan2 = Channel.one2one()

def emitter = new Emit( output: chan1.out(),
    eDetails: eDetails )

def eaEngine = new EAEngine(
    input: chan1.in(),
    output: chan2.out(),
    nodes: nodes
)

def collector = new Collect( input: chan2.in(),
    rDetails: rDetails)
println " MaxOnes: nodes: $nodes, " +
    "populationPerNode: $populationPerNode, " +
    " Genes: $geneLength, " +
    "mutateProbability: $mutateProbability, " +
    "instances: $instances"
new PAR([emitter, eaEngine, collector]).run()

