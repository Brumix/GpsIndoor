package bruno.p.pereira.gpsindoorf.graph.algorithm


import bruno.p.pereira.gpsindoorf.graph.data.Node
import bruno.p.pereira.gpsindoorf.graph.data.graph.GraphTypes
import java.util.*


interface PathFindingAlgorithm {
    fun run(graphType: GraphTypes = GraphTypes.TRADITIONAL)
    fun getPath(): Stack<Node>
    fun getVisitedOrder(): LinkedList<Node>
}