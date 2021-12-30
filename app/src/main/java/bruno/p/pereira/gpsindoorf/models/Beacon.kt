package bruno.p.pereira.gpsindoorf.models


data class Beacon(
    val id: Int,
    val name: String,
    val mac: String,
    val rssi : Int
)