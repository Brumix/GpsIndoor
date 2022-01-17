package bruno.p.pereira.gpsindoorf.graph.drawable

import android.graphics.RectF
import bruno.p.pereira.gpsindoorf.graph.data.Edge
import bruno.p.pereira.gpsindoorf.graph.data.Node
import java.time.temporal.TemporalAmount


class DrawableNode (val id: String, var centerX: Float, var centerY: Float, val text:String = "SALA -1"):
    Node(id) {

    var rect: RectF
    var connectedTo: HashMap<String, DrawableNode> = HashMap()
    var connectedByEdge: HashMap<String, WeighBox> = HashMap()

    companion object {
        private const val DIAMETER = 100f
        const val RADIUS = DIAMETER / 2
    }

    init {
        rect = RectF(
            centerX - RADIUS,
            centerY - RADIUS,
            centerX + RADIUS,
            centerY + RADIUS)
        position = Pair(centerX.toInt(), centerY.toInt())
    }

    fun updatePosition(x: Float, y: Float) {
        centerX = x;
        centerY = y
        rect.left = centerX - RADIUS
        rect.top = centerY - RADIUS
        rect.right = centerX + RADIUS
        rect.bottom = centerY + RADIUS
    }

    fun connectByEdge(nodeToConnect: DrawableNode, weighBox: WeighBox) {
        weighBox.edge = super.connect(nodeToConnect, Edge.DEFAULT_WEIGHT)

        connectedTo[nodeToConnect.id] = nodeToConnect
        connectedByEdge[nodeToConnect.id] = weighBox

        nodeToConnect.connectedTo[id] = this
        nodeToConnect.connectedByEdge[id] = weighBox
    }

    fun increaseEdgeWeight(node : DrawableNode, amount: Double){
        connectedByEdge[node.id]?.increaseWeight(amount)
    }


}