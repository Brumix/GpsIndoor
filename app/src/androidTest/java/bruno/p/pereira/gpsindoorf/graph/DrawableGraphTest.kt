package bruno.p.pereira.gpsindoorf.graph.data.graph

import bruno.p.pereira.gpsindoorf.graph.drawable.DrawableNode
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

@RunWith(JUnit4::class)
class DrawableGraphTest{
    private lateinit var teste:DrawableGraph
    @Test
    fun adicionarnovoNo(){
        teste= DrawableGraph()
       val testenode =DrawableNode("beacon10",50.0f,50.0f)
        teste.addNode(testenode)
       val no= teste.getNode("beacon10")
        assertEquals(no,testenode)
    }
    @Test
    fun tentarGetdeNoQueNaoExiste(){
        teste= DrawableGraph()
        val testenode =DrawableNode("beacon10",50.0f,50.0f)
        teste.addNode(testenode)
        val no= teste.getNode("beacon100")
        assertNotEquals(no,testenode)
    }

}