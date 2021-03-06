package bruno.p.pereira.gpsindoorf.graph.drawable

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.graph.data.Node
import bruno.p.pereira.gpsindoorf.graph.data.graph.DrawableGraph

class DrawableGraphViewPaint(val context: Context, private val paint: Paint) {

    init {
        configurePaint()
    }

    // --------- colors ---------
    private val colorStartNode: Int = ContextCompat.getColor(context, R.color.colorStartPoint)
    private val colorEndNode: Int = ContextCompat.getColor(context, R.color.colorEndPoint)
    private val colorNode: Int = ContextCompat.getColor(context, R.color.colorNode)
    private val colorDrawablePath: Int = ContextCompat.getColor(context, R.color.colorDrawablePath)
    private val colorNodeText: Int = ContextCompat.getColor(context, R.color.colorNodeText)
    private val colorEdge: Int = ContextCompat.getColor(context, R.color.colorEdge)
    private val colorTextWeight: Int = ContextCompat.getColor(context, R.color.colorTextWeight)
    private val colorBoxWeight: Int = ContextCompat.getColor(context, R.color.colorBoxWeight)
    private val colorSelectedNode: Int = ContextCompat.getColor(context, R.color.colorSelectedNode)
    private val colorBoundaries: Int = ContextCompat.getColor(context, R.color.colorBoundaries)
    private val colorClosestNode: Int = ContextCompat.getColor(context, R.color.colorClosestNode)
    // --------------------------

    fun drawBoundaries(width: Int, height: Int, canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.color = colorBoundaries

        canvas.drawRect(1f, 1f, width - 1f, height - 1f, paint)
    }

    fun drawNodes(nodes: List<DrawableNode>, canvas: Canvas) {
        paint.style = Paint.Style.FILL
        paint.color = colorNode

        for (node in nodes)
            drawNode(node, canvas)
    }

    private fun drawNode(node: DrawableNode, canvas: Canvas) {
        if (DrawableGraphView.closestNode != null && node.id == DrawableGraphView.closestNode!!.id)
            paint.color = colorClosestNode
        canvas.drawCircle(node.centerX, node.centerY, DrawableNode.RADIUS, paint)
        paint.color = colorNode
    }

    fun drawSelectedNode(selectedNode: DrawableNode?, canvas: Canvas) {
        if (selectedNode == null) return
        paint.style = Paint.Style.STROKE
        paint.color = colorSelectedNode
        drawNode(selectedNode, canvas)
    }

    fun drawPathNodes(graph: DrawableGraph, pathNodesOrder: List<Node>, canvas: Canvas) {
        paint.style = Paint.Style.FILL
        paint.color = colorDrawablePath

        for (node in pathNodesOrder) {
            val drawableNode = graph.getNode(node.name)
            if (drawableNode != null)
                drawNode(drawableNode, canvas)
        }
    }

    fun drawStartAndEndPoints(startNode: DrawableNode?, endNode: DrawableNode?, canvas: Canvas) {
        if (startNode != null) {
            paint.style = Paint.Style.FILL
            paint.color = colorStartNode
            canvas.drawCircle(startNode.centerX, startNode.centerY, DrawableNode.RADIUS, paint)
        }

        if (endNode == null) return
        paint.color = colorEndNode
        canvas.drawCircle(endNode.centerX, endNode.centerY, DrawableNode.RADIUS, paint)
    }

    fun drawTextNodes(nodes: List<DrawableNode>, canvas: Canvas) {
        paint.color = colorNodeText
        for (node in nodes) {
            drawTextNode(node, canvas)
        }
    }

    private fun drawTextNode(node: DrawableNode, canvas: Canvas) {
        canvas.drawText(
            node.text,
            node.centerX - paint.measureText(node.text) / 2,
            node.centerY - ((paint.descent() + paint.ascent()) / 2), paint
        )
    }

    fun drawEdges(weighBoxes: List<WeighBox>, canvas: Canvas) {
        paint.style = Paint.Style.STROKE
        paint.color = colorEdge
        paint.strokeWidth = context.resources.displayMetrics.density * 2

        for (weighBox in weighBoxes) {
            val edge = weighBox.edge ?: return
            if (edge.connected)
                drawEdge(weighBox.nodeA, weighBox.nodeB, canvas)
        }
        paint.strokeWidth = context.resources.displayMetrics.density
    }

    private fun drawEdge(nodeA: DrawableNode, nodeB: DrawableNode, canvas: Canvas) {
        canvas.drawLine(nodeA.centerX, nodeA.centerY, nodeB.centerX, nodeB.centerY, paint)
    }

    fun drawPathEdges(pathNodesOrder: List<Node>, view: View, canvas: Canvas) {
        var currentNode = pathNodesOrder.get(index = 0) as DrawableNode
        paint.color = colorDrawablePath
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = context.resources.displayMetrics.density * 2
        for (i in 1 until pathNodesOrder.size) {
            val nodeB = pathNodesOrder.get(index = i) as DrawableNode
            drawEdge(currentNode, nodeB, canvas)
            currentNode = pathNodesOrder.get(index = i) as DrawableNode
        }
        view.invalidate()
    }

    fun drawPathWeights(pathNodesOrder: List<Node>, view: View, canvas: Canvas) {
        var currentNode = pathNodesOrder.get(index = 0) as DrawableNode
        paint.textSize /= 1.5f
        paint.style = Paint.Style.FILL
        paint.strokeWidth = context.resources.displayMetrics.density
        for (i in 1 until pathNodesOrder.size) {
            val nodeB = pathNodesOrder.get(index = i) as DrawableNode
            drawWeight(
                currentNode, nodeB, currentNode.edges[nodeB.id]!!.weight.toInt().toString(),
                colorDrawablePath, canvas
            )
            currentNode = pathNodesOrder.get(index = i) as DrawableNode
        }
        paint.textSize *= 1.5f
        view.invalidate()
    }

    fun drawWeights(weighBoxes: List<WeighBox>, canvas: Canvas) {
        paint.style = Paint.Style.FILL

        paint.textSize /= 1.5f
        for (weighBox in weighBoxes) {
            val edge = weighBox.edge ?: continue
            if (!edge.connected) continue
            val nodeA = weighBox.nodeA
            val nodeB = weighBox.nodeB
            drawWeight(
                nodeA, nodeB, edge.weight.toInt().toString(),
                colorBoxWeight, canvas
            )
        }
        paint.textSize *= 1.5f
    }

    fun drawWeight(
        nodeA: DrawableNode, nodeB: DrawableNode, weight: String,
        boxColor: Int, canvas: Canvas
    ) {
        val edge = nodeA.connectedByEdge[nodeB.id] ?: return
        val textCenterX = (nodeA.centerX + nodeB.centerX) / 2
        val textCenterY = (nodeA.centerY + nodeB.centerY) / 2

        paint.color = boxColor
        canvas.drawRoundRect(edge.boundaries, 15f, 15f, paint)

        paint.color = colorTextWeight
        canvas.drawText(
            weight, textCenterX - (paint.measureText(weight) / 2),
            textCenterY - ((paint.descent() + paint.ascent()) / 2), paint
        )
    }

    private fun configurePaint() {
        paint.isAntiAlias = true
        paint.strokeWidth = context.resources.displayMetrics.density
        paint.textSize = 48f
    }

}