package bruno.p.pereira.gpsindoorf.graph.data.graph

import bruno.p.pereira.gpsindoorf.graph.data.Edge
import bruno.p.pereira.gpsindoorf.graph.drawable.DrawableNode
import java.util.*


class DrawableGraph :
    Graph<DrawableNode> {

    private val drawableNodes: LinkedList<DrawableNode> = LinkedList()
    var maxId: Int = 1


    override fun getNodes(): LinkedList<DrawableNode> {
        return drawableNodes
    }

    fun getNode(id: String): DrawableNode? {
        for (node in drawableNodes) {
            if (node.id == id)
                return node
        }
        return null
    }

    fun addNode(node: DrawableNode) {
        drawableNodes.add(node)
        maxId++
    }

    fun removeNode(node: DrawableNode) {
        node.disconnectAll()
        drawableNodes.remove(node)
    }

    fun readdNode(node: DrawableNode) {
        drawableNodes.add(node)
    }

    fun reconnectNodes(node: DrawableNode, edges: List<Edge?>) {
        node.reconnectNodes(edges)
    }

}