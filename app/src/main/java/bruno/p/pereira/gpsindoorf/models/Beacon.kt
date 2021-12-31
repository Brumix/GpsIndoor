package bruno.p.pereira.gpsindoorf.models


data class Beacon(
    var id: Int,
    val name: String,
    val mac: String,
    val rssi : Int
)