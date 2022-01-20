package bruno.p.pereira.gpsindoorf.graph.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.TAG
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.graph.algorithm.Djikstra
import bruno.p.pereira.gpsindoorf.graph.algorithm.PathFindingAlgorithm
import bruno.p.pereira.gpsindoorf.graph.data.Edge
import bruno.p.pereira.gpsindoorf.graph.data.Node
import bruno.p.pereira.gpsindoorf.graph.data.graph.DrawableGraph
import bruno.p.pereira.gpsindoorf.graph.manager.ActionsManager
import bruno.p.pereira.gpsindoorf.graph.manager.HistoryAction
import bruno.p.pereira.gpsindoorf.graph.manager.actions.*
import bruno.p.pereira.gpsindoorf.models.DtoLocation
import bruno.p.pereira.gpsindoorf.models.EdgeModel
import bruno.p.pereira.gpsindoorf.services.HttpRequest
import bruno.p.pereira.gpsindoorf.ui.sync.SyncViewModel
import java.util.*
import kotlin.collections.ArrayList


class DrawableGraphView : View {

    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)

    data class CloseData(val mac: String, val rssi: Int)

    private val db: SQLiteHelper by lazy {
        SQLiteHelper(this.context)
    }

    private val touchableSpace: Float = 10f
    private lateinit var actionsManager: ActionsManager
    private lateinit var viewModel: SyncViewModel
    private lateinit var tvDivision: TextView
    private lateinit var tvMac: TextView

    private val weighBoxes: ArrayList<WeighBox> = ArrayList()
    private var startPoint: DrawableNode? = null
    private var endPoint: DrawableNode? = null
    private var readyToAddEdges: Boolean = false
    private var readyToAddStartAndEndNodes: Boolean = false
    private var readyToRunAgain: Boolean = false
    private var isInsit: Boolean = true
    private var selectedNode: DrawableNode? = null
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
                    if (selectedNode != null)
                        drawText(selectedNode!!.id, selectedNode!!.text)

                } else {
                    val nodeB = getDrawableNodeAtPoint(x, y)

                    if (nodeB == null) { //user clicks on an empty area after choose first node
                        deselectNode()
                        return true
                    }

                    if (selectedNode != nodeB) { //user connect nodes
                        addDrawableEdge(selectedNode!!, nodeB)
                        storeEdgeInfo(selectedNode!!, nodeB)
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
                        incresasseWeightInfo(edge)
                    } else {
                       // addDrawableNode(x, y)
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
                    saveNewLocation(selectedNode!!.id, x, y)
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


        var closest: CloseData? = null
        for (i in viewModel.getBeacons()) {
            if (closest == null)
                closest = CloseData(i.mac, i.rssi)
            else {
                if (i.rssi > closest.rssi)
                    closest = CloseData(i.mac, i.rssi)
            }
        }

        Log.v(TAG, "[DRAWABLEGRAPHVIEW] Closest Data = $closest")
        val seenNodes = mutableMapOf<String, DtoLocation>()
        for (node in this.db.getAllLocations()) {
            if (seenNodes.containsKey(node.mac) || node.latitude == "-1") continue
            val draw = DrawableNode(
                node.mac,
                node.longitude.toFloat(),
                node.latitude.toFloat(),
                node.division
            )
            if (closest != null && draw.id == closest.mac)
                closestNode = draw
            addDrawableNode(draw)

            seenNodes[node.mac] = node
        }

        for (edge in this.db.getAllEdges()) {
            val nodeA = getDrawableNode(edge.nodeA)
            val nodeB = getDrawableNode(edge.nodeB)
            val weigh = edge.weight.toDouble()
            addDrawableEdge(nodeA, nodeB, weight = weigh)
        }
    }

    private fun saveNewLocation(mac: String, x: Float, y: Float) {
        val dto = this.db.getFirstLocationbyMac(mac) ?: return
        dto.latitude = y.toString()
        dto.longitude = x.toString()
        this.db.updateLocation(dto)
        HttpRequest.startActionPOSTLoc(context, dto)
        Log.v(TAG, "[DRAWABLEGRAPHVIEW] DTO updated ${dto.mac}")
    }

    private fun deselectNode() {
        selectedNode = null
        resetText()
    }

    fun runAlgorithm() {
        if (!isReadyToRun()) return

        pathNodesOrder.clear()

        val nodes = graph.getNodes() as LinkedList<Node>
        val nodeA =
            if (startPoint == null && closestNode != null) closestNode as Node else startPoint as Node
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
        return (startPoint != null && endPoint != null || closestNode != null && endPoint != null)
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
            if (selectedNode != null)
                drawText(selectedNode!!.id, selectedNode!!.text)
            invalidate()
        }

    }

    private fun getDrawableNode(id: String): DrawableNode {
        return graph.getNode(id)!!
    }

    private fun addDrawableNode(drawableNode: DrawableNode) {
        graph.addNode(drawableNode)
        invalidate()
    }

    fun removeSelectedNode() {
        val selected = selectedNode ?: return
        HttpRequest.startActionDELETEEdge(this.context, selected.id)
        HttpRequest.startActionDELETELoc(this.context, selected.id)
        this.db.deleteEdge(selected.id)
        this.db.deleteLocationByMac(selected.id)
        removeNodeInfo(selected.id)
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
        history: Boolean = true,
        weight: Double = Edge.DEFAULT_WEIGHT,
    ) {
        if (nodeA.connectedTo.size < graph.getNodes().size - 1) {

            weighBoxes.add(WeighBox(weighBoxes.size + 1, nodeA, nodeB))

            weighBoxes.last().connectTo(nodeB, paint)
            if (weight != Edge.DEFAULT_WEIGHT)
                nodeA.increaseEdgeWeight(nodeB, weight - 1)
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

        if (startPoint == null && closestNode == null || startPoint == null && endPoint != null) {
            if (closestNode != null && node == closestNode) return
            selectStartPoint(node)
            actionsManager.addHistory(
                ActionStartPoint(
                    node
                )
            )
        } else if (endPoint == null) {
            if (closestNode != null && node == closestNode) return
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
        if (startPoint != null || closestNode != null) {

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
        deselectNode()
        weighBoxes.clear()
        pathNodesOrder.clear()
        actionsManager.clearHistory()
        invalidate()

    }

    fun setResources(
        actionsManager: ActionsManager,
        viewModel: SyncViewModel,
        tvDivision: TextView,
        tvmac: TextView
    ) {
        this.actionsManager = actionsManager
        this.viewModel = viewModel
        this.tvDivision = tvDivision
        this.tvMac = tvmac
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

    private fun removeNodeInfo(mac: String) {

        this.db.deleteLocationByMac(mac)
        HttpRequest.startActionDELETELoc(this.context, mac)
        this.db.deleteEdge(mac)
        HttpRequest.startActionDELETEEdge(this.context, mac)
    }

    private fun storeEdgeInfo(nodeA: DrawableNode, nodeB: DrawableNode) {
        val edge = EdgeModel(nodeA.id, nodeB.id)
        this.db.insertEdges(edge)
        HttpRequest.startActionPOSTEdge(this.context, edge)
    }

    private fun incresasseWeightInfo(edge: WeighBox) {
        val edgeM = EdgeModel(edge.nodeA.id, edge.nodeB.id, edge.getWeight().toString())
        this.db.updateEdge(edgeM)
        HttpRequest.startActionPOSTEdge(this.context, edgeM)
    }

    private fun drawText(mac: String, division: String) {
        this.tvDivision.text = division
        this.tvMac.text = mac
    }

    private fun resetText() {
        this.tvDivision.text = "DIVISION"
        this.tvMac.text = "MAC"
    }


    companion object {
        var closestNode: DrawableNode? = null


    }
}