package bruno.p.pereira.gpsindoorf.models

data class DtoLocation(var mac : String, val place:String, val division:String, val longitude:String, val latitude:String, val loc_time: String? = null)