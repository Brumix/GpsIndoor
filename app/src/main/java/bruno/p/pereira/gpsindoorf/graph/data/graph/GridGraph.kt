package bruno.p.pereira.gpsindoorf.graph.data.graph


import bruno.p.pereira.gpsindoorf.graph.data.Node
import java.util.*
import kotlin.collections.HashMap


class GridGraph(private val rows: Int, private val columns: Int) :
    Graph<Node> {

    val table = Array(rows) { arrayOfNulls<Node>(columns) }
    private val removedNodes = HashMap<String, Node>()

    init {
        populate()
        connectNodes()
    }

    override fun getNodes() : LinkedList<Node> {
        val nodes = LinkedList<Node>()
        table.forEach { row ->
            row.forEach { node ->
                node?.let { nodes.add(it) }
            }
        }
        return nodes
    }

    fun getNode(row: Int, column: Int): Node? {
        return table[row][column]
    }

    fun getNode(point: Pair<Int,Int>): Node? {
        return table[point.first][point.second]
    }

    fun removeNode(nodePosition: Pair<Int,Int>) {
        val node = getNode(nodePosition) ?: return
        node.disconnectAll()
        removedNodes[node.name] = node
    }

    fun readdNode(nodePosition: Pair<Int,Int>) {
        val nodeName = "${nodePosition.first},${nodePosition.second}"
        val node = removedNodes[nodeName] ?: return
        removedNodes.remove(nodeName)
        node.edges.values.forEach {
            val opposite = it.getOpposite(node)
            if (!removedNodes.containsKey(opposite.name)) {
                node.reconnect(opposite)
            }
        }
    }

    private fun addNode(row: Int, col: Int) {
        if ((row >= rows) || (col >= columns)) return
        val node = Node("${row},${col}")
        node.position = Pair(row,col)
        table[row][col] = node
    }

    /**
     * Creates all the nodes of the matrix
     */
    private fun populate() {
        table.forEachIndexed { i, row ->
            row.forEachIndexed { j, _ -> addNode(i,j)}
        }
    }

    /**
     * Connects adjacent nodes with weight 1
     */
    private fun connectNodes() {
        table.forEachIndexed { i, row ->
            row.forEachIndexed { j, node ->
                if(node != null) {
                    if (i <= columns - 2) {
                        getNode(i + 1, j)?.connect(node) //connect to right node
                    }

                    if (j <= rows - 2) {
                        getNode(i, j + 1)?.connect(node)  //connect to bottom node
                    }
                }
            }
        }
    }

}