package bruno.p.pereira.gpsindoorf.graph.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.graph.algorithm.Djikstra
import bruno.p.pereira.gpsindoorf.graph.algorithm.PathFindingAlgorithm
import bruno.p.pereira.gpsindoorf.graph.data.Edge
import bruno.p.pereira.gpsindoorf.graph.data.Node
import bruno.p.pereira.gpsindoorf.graph.data.graph.DrawableGraph
import bruno.p.pereira.gpsindoorf.graph.manager.ActionsManager
import bruno.p.pereira.gpsindoorf.graph.manager.HistoryAction
import bruno.p.pereira.gpsindoorf.graph.manager.actions.*
import java.util.*
import kotlin.collections.ArrayList


class DrawableGraphView : View {

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    private val touchableSpace: Float = 10f
    private lateinit var actionsManager: ActionsManager

    private val weighBoxes: ArrayList<WeighBox> = ArrayList()
    private var startPoint: DrawableNode? = null
    private var endPoint: DrawableNode? = null
    private var selectedNode: DrawableNode? = null
    private var readyToAddEdges: Boolean = false
    private var readyToAddStartAndEndNodes: Boolean = false
    private var readyToRunAgain: Boolean = false
    private var isInsit: Boolean = true

    private var movingNode: Boolean = false
    private var initalMovePosition = Pair(-1f, -1f)

    private var graph: DrawableGraph =
        DrawableGraph()
    private lateinit var algorithm: PathFindingAlgorithm
    private val pathNodesOrder: Stack<Node> = Stack()

    private val paint = Paint()
    private val paintManager = DrawableGraphViewPaint(context, paint)


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isInsit)
            setup()


        paintManager.drawBoundaries(width, height, canvas)
        paintManager.drawEdges(weighBoxes, canvas)
        paintManager.drawNodes(graph.getNodes(), canvas)
        paintManager.drawWeights(weighBoxes, canvas)

        if (pathNodesOrder.isNotEmpty()) {
            paintManager.drawPathNodes(graph, pathNodesOrder, canvas)
            paintManager.drawPathEdges(pathNodesOrder, this, canvas)
            paintManager.drawPathWeights(pathNodesOrder, this, canvas)

        }
        paintManager.drawStartAndEndPoints(startPoint, endPoint, canvas)
        paintManager.drawTextNodes(graph.getNodes(), canvas)
        paintManager.drawSelectedNode(selectedNode, canvas)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = if (measuredWidth > measuredHeight) measuredHeight else measuredWidth
        setMeasuredDimension(size, size)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false

        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (selectedNode == null) {
                    selectedNode = getDrawableNodeAtPoint(x, y)
                } else {
                    val nodeB = getDrawableNodeAtPoint(x, y)

                    if (nodeB == null) { //user clicks on an empty area after choose first node
                        deselectNode()
                        return true
                    }

                    if (selectedNode != nodeB) { //user connect nodes
                        addDrawableEdge(selectedNode!!, nodeB)
                        deselectNode()
                        return true
                    }

                    if (selectedNode == nodeB) { //user clicks on the same selected node
                        selectInitialFinalNode(x, y)
                        deselectNode()
                        return true
                    }
                }

                if (selectedNode == null) { //user increase weight
                    val edge = getEdgeBoxAtPoint(x, y)
                    if (edge != null) {
                        increaseEdgeWeight(edge)
                    } else {
                        addDrawableNode(x, y)
                        readyToAddEdges = false
                    }
                } else {
                    readyToAddEdges = true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!movingNode) {
                    initalMovePosition = Pair(x, y)
                    movingNode = true
                }
                if (y > 0 && y < this.height) {
                    val node = selectedNode ?: return false
                    moveNode(node, x, y)
                    readyToAddEdges = false
                }
            }
            MotionEvent.ACTION_UP -> {
                if (movingNode && selectedNode != null) {
                    actionsManager.addHistory(
                        ActionMove(
                            selectedNode as DrawableNode,
                            Pair(initalMovePosition.first, initalMovePosition.second),
                            Pair(x, y)
                        )
                    )
                    initalMovePosition = Pair(-1f, -1f)
                    movingNode = false
                }
                if (!readyToAddEdges && !readyToAddStartAndEndNodes) {
                    deselectNode()
                }
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
            }
        }
        return true
    }

    private fun setup() {
        isInsit = false
        val a = DrawableNode("1", width / 2f, height / 2f)
        val b = DrawableNode("2", width / 3f, height / 3f)
        addDrawableNode(a)
        addDrawableNode(b)
        a.increaseEdgeWeight(b,12.0)
        addDrawableEdge(a, b)
    }

    private fun deselectNode() {
        selectedNode = null
    }

    fun runAlgorithm() {
        if (!isReadyToRun()) return

        pathNodesOrder.clear()

        val nodes = graph.getNodes() as LinkedList<Node>
        val nodeA = startPoint as Node
        val nodeB = endPoint as Node
        algorithm = Djikstra(nodes, nodeA, nodeB)


        algorithm.run()
        readyToRunAgain = true

        val path = algorithm.getPath()
        while (path.isNotEmpty())
            pathNodesOrder.add(path.pop())

        invalidate()
    }

    fun isReadyToRun(): Boolean {
        return (startPoint != null && endPoint != null)
    }

    private fun increaseEdgeWeight(weighBox: WeighBox, history: Boolean = true) {
        val weight = Edge.DEFAULT_WEIGHT
        weighBox.increaseWeight(weight)
        if (history) {
            actionsManager.addHistory(
                ActionWeigh(
                    weighBox,
                    weight
                )
            )
        }
        invalidate()
        if (readyToRunAgain) {
            runAlgorithm()
        }
    }

    private fun decreaseEdgeWeight(weighBox: WeighBox) {
        weighBox.decreaseWeight(Edge.DEFAULT_WEIGHT)
        invalidate()
        if (readyToRunAgain) {
            runAlgorithm()
        }
    }

    private fun getMaxIdNodes(): String {
        if (graph.getNodes().isNotEmpty()) {
           /* var max = graph.getNodes()[0].id
            for (i in graph.getNodes()) {
                if (max.toInt() < i.id.toInt()) {
                    max = i.id
                }
            }
            return max.toInt().plus(1).toString()*/
            return graph.maxId.toString()
        }
        return "1"
    }

    private fun addDrawableNode(x: Float, y: Float) {
        val id = getMaxIdNodes()
        val node = DrawableNode(id, x, y)
        if (!hasCollision(node)) {
            graph.addNode(node)
            actionsManager.addHistory(ActionAdd(node))
            selectedNode = node
            invalidate()
        }

    }

    private fun addDrawableNode(drawableNode: DrawableNode) {
        graph.addNode(drawableNode)
        invalidate()
    }

    fun removeSelectedNode() {
        val selected = selectedNode ?: return
        removeNode(selected)
    }

    private fun removeNode(drawableNode: DrawableNode, history: Boolean = true) {
        val edgesToRemove =
            weighBoxes.filter { edge -> edge.nodeA == selectedNode || edge.nodeB == selectedNode }
        val edgesConnections =
            edgesToRemove.map { edge -> edge.edge?.connected ?: false }
        if (history) {
            actionsManager.addHistory(
                ActionRemove(
                    drawableNode,
                    edgesToRemove,
                    edgesConnections
                )
            )
        }
        graph.removeNode(drawableNode)
        weighBoxes.removeAll(edgesToRemove)
        deselectNode()
        invalidate()
    }

    private fun addDrawableEdge(
        nodeA: DrawableNode,
        nodeB: DrawableNode,
        history: Boolean = true
    ) {
        if (nodeA.connectedTo.size < graph.getNodes().size - 1) {
            weighBoxes.add(WeighBox(weighBoxes.size + 1, nodeA, nodeB))
            weighBoxes.last().connectTo(nodeB, paint)
            if (history) {
                actionsManager.addHistory(
                    ActionConnect(
                        nodeA,
                        nodeB
                    )
                )
            }
            invalidate()
            if (readyToRunAgain) {
                runAlgorithm()
            }
        }
    }

    private fun reconnect(nodeA: DrawableNode, nodeB: DrawableNode) {
        nodeA.reconnect(nodeB)
        invalidate()
        if (readyToRunAgain) {
            runAlgorithm()
        }
    }

    private fun selectInitialFinalNode(x: Float, y: Float) {
        val node = getDrawableNodeAtPoint(x, y) ?: return
        if (startPoint == node) {
            deselectStartPoint()
            actionsManager.addHistory(
                ActionStartPoint(
                    node
                )
            )
            return
        } else if (endPoint == node) {
            deselectEndPoint()
            actionsManager.addHistory(
                ActionEndPoint(
                    node
                )
            )
            return
        }

        if (startPoint == null) {
            selectStartPoint(node)
            actionsManager.addHistory(
                ActionStartPoint(
                    node
                )
            )
        } else if (endPoint == null) {
            selectEndPoint(node)
            actionsManager.addHistory(
                ActionEndPoint(
                    node
                )
            )
        }
    }

    private fun selectStartPoint(node: DrawableNode) {
        startPoint = node

        invalidate()
    }

    private fun deselectStartPoint() {
        startPoint = null
        if (endPoint != null) {

            pathNodesOrder.clear()
        }
        invalidate()
    }

    private fun selectEndPoint(node: DrawableNode) {
        endPoint = node

        invalidate()
    }

    private fun deselectEndPoint() {
        endPoint = null
        if (startPoint != null) {

            pathNodesOrder.clear()
        }
        invalidate()
    }

    private fun moveNode(selectedNode: DrawableNode, x: Float, y: Float) {
        val previousX = selectedNode.centerX
        val previousY = selectedNode.centerY
        selectedNode.updatePosition(x, y)
        if (hasCollision(selectedNode)) {
            selectedNode.updatePosition(previousX, previousY)
        } else {
            for (drawableEdge in selectedNode.connectedByEdge.values) {
                val edge = drawableEdge.edge ?: continue
                if (edge.connected)
                    drawableEdge.updateWeightBox(paint)
            }
            invalidate()
        }
    }

    private fun hasCollision(node: DrawableNode): Boolean {
        for (n in graph.getNodes()) {
            if (n == node) continue
            if (node.rect.intersect(n.rect))
                return true
        }
        return false
    }

    private fun getDrawableNodeAtPoint(x: Float, y: Float): DrawableNode? {
        val touchedPoint = RectF(
            x - touchableSpace, y - touchableSpace,
            x + touchableSpace, y + touchableSpace
        )
        for (n in graph.getNodes()) {
            if (touchedPoint.intersect(n.rect))
                return n
        }
        return null
    }

    private fun getEdgeBoxAtPoint(x: Float, y: Float): WeighBox? {
        val touchedPoint = RectF(
            x - touchableSpace, y - touchableSpace,
            x + touchableSpace, y + touchableSpace
        )
        for (e in weighBoxes) {
            if (touchedPoint.intersect(e.touchableArea))
                return e
        }
        return null
    }

    fun printablePath(): String {
        if (pathNodesOrder.isEmpty()) return ""

        val stringPath = StringBuffer(pathNodesOrder[0].name)
        for (i in 1 until pathNodesOrder.size) {
            stringPath.append(" -> ${pathNodesOrder[i].name}")
        }

        return stringPath.toString()
    }

    fun printableVisitedOrder(): SpannableStringBuilder {
        if (algorithm.getVisitedOrder().isEmpty()) return SpannableStringBuilder("")

        val stringPath = SpannableStringBuilder(algorithm.getVisitedOrder()[0].name)
        val color = ForegroundColorSpan(ContextCompat.getColor(context, R.color.colorAccent))
        stringPath.setSpan(color, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        for (i in 1 until algorithm.getVisitedOrder().size) {
            stringPath.append(" -> ${algorithm.getVisitedOrder()[i].name}")
        }

        return stringPath
    }

    fun graphDescription(): String {
        val total = context.getString(R.string.graph_information_total_of)
        val isNotConnected = context.getString(R.string.graph_information_is_not_connected)
        val isConnectedTo = context.getString(R.string.graph_information_is_connected_to)
        val nodes = context.getString(R.string.nodes)
        val node = context.getString(R.string.node)

        val stringPath = StringBuffer(
            "$total: ${graph.getNodes().size} ${
                if (graph.getNodes().size > 1) nodes else node
            }"
        )

        for (drawableNode in graph.getNodes()) {
            if (drawableNode.edges.keys.isEmpty()) {
                stringPath.append("\n${drawableNode.name} $isNotConnected")
            } else {
                stringPath.append("\n${drawableNode.name} $isConnectedTo ${drawableNode.edges.keys}")
            }
        }

        return stringPath.toString()
    }


    fun reset() {
        graph = DrawableGraph()
        startPoint = null
        endPoint = null
        selectedNode = null
        weighBoxes.clear()
        pathNodesOrder.clear()
        actionsManager.clearHistory()
        invalidate()

    }

    fun setActionsManager(actionsManager: ActionsManager) {
        this.actionsManager = actionsManager
    }

    // ------------------ Undo / Redo ------------------

    fun undo() {
        val action = this.actionsManager.undo() ?: return
        when (action.getType()) {
            HistoryAction.ADD -> undoAdd(action as ActionAdd)
            HistoryAction.REMOVE -> undoRemove(action as ActionRemove)
            HistoryAction.CONNECT -> undoConnect(action as ActionConnect)
            HistoryAction.MOVE -> undoMove(action as ActionMove)
            HistoryAction.WEIGH -> undoWeigh(action as ActionWeigh)
            HistoryAction.START_POINT -> undoStartPoint()
            HistoryAction.END_POINT -> undoEndPoint()
        }
        if (readyToRunAgain) runAlgorithm()
        deselectNode()
        invalidate()
    }

    fun redo() {
        val action = this.actionsManager.redo() ?: return
        when (action.getType()) {
            HistoryAction.ADD -> redoAdd(action as ActionAdd)
            HistoryAction.REMOVE -> redoRemove(action as ActionRemove)
            HistoryAction.CONNECT -> redoConnect(action as ActionConnect)
            HistoryAction.MOVE -> redoMove(action as ActionMove)
            HistoryAction.WEIGH -> redoWeigh(action as ActionWeigh)
            HistoryAction.START_POINT -> redoStartPoint(action as ActionStartPoint)
            HistoryAction.END_POINT -> redoEndPoint(action as ActionEndPoint)
        }
        if (readyToRunAgain) runAlgorithm()
        invalidate()
    }

    private fun undoAdd(action: ActionAdd) {
        graph.removeNode(action.drawableNode)
        weighBoxes.removeAll(weighBoxes.filter { edge -> edge.nodeA == selectedNode || edge.nodeB == selectedNode })
    }

    private fun redoAdd(action: ActionAdd) {
        addDrawableNode(action.drawableNode)
    }

    private fun undoRemove(action: ActionRemove) {
        graph.readdNode(action.getNode())
        val edgesToReconnect = ArrayList<Edge?>()
        for (i in action.getConnections().indices) {
            if (action.getConnections()[i]) {
                edgesToReconnect.add(action.getEdges()[i])
            }
        }
        graph.reconnectNodes(action.getNode(), edgesToReconnect)
        this.weighBoxes.addAll(action.getDrawableEdges())
    }

    private fun redoRemove(action: ActionRemove) {
        removeNode(action.getNode(), false)
    }

    private fun undoConnect(action: ActionConnect) {
        action.nodeA.disconnect(action.nodeB)
    }

    private fun redoConnect(action: ActionConnect) {
        reconnect(action.nodeA, action.nodeB)
    }

    private fun undoMove(action: ActionMove) {
        action.node.updatePosition(action.initialPosition.first, action.initialPosition.second)
        action.node.connectedByEdge.values.forEach { edge ->
            edge.updateWeightBox(paint)
        }
    }

    private fun redoMove(action: ActionMove) {
        action.node.updatePosition(action.finalPosition.first, action.finalPosition.second)
        action.node.connectedByEdge.values.forEach { edge ->
            edge.updateWeightBox(paint)
        }
    }

    private fun undoWeigh(action: ActionWeigh) {
        decreaseEdgeWeight(action.weighBox)
    }

    private fun redoWeigh(action: ActionWeigh) {
        increaseEdgeWeight(action.weighBox, false)
    }

    private fun undoStartPoint() {
        deselectStartPoint()
    }

    private fun redoStartPoint(action: ActionStartPoint) {
        selectStartPoint(action.node)
    }

    private fun undoEndPoint() {
        deselectEndPoint()
    }

    private fun redoEndPoint(action: ActionEndPoint) {
        selectEndPoint(action.node)
    }

}