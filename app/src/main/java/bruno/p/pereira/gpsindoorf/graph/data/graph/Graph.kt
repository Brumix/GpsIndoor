package bruno.p.pereira.gpsindoorf.graph.data.graph

import java.util.*

interface Graph<T> {
    fun getNodes() : LinkedList<T>
}