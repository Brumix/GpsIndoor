package bruno.p.pereira.gpsindoorf.models

import bruno.p.pereira.gpsindoorf.graph.data.Edge

data class EdgeModel(val nodeA:String, val nodeB :String, val weight:String = Edge.DEFAULT_WEIGHT.toString())