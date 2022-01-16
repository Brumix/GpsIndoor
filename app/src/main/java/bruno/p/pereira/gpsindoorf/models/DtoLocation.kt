package bruno.p.pereira.gpsindoorf.models

data class DtoLocation(var mac : String, val place:String, val division:String, var longitude:String, var latitude:String, val loc_time: String? = null)