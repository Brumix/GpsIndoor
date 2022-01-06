package bruno.p.pereira.gpsindoorf.models

import bruno.p.pereira.gpsindoorf.enums.LocationEnum

open class Location {

    private var whereIs: LocationEnum = LocationEnum.UNKNOWN
    private var label: String = ""
    private var place: String = " "
    private var division: String = " "
    private var long: String = ""
    private var lat: String = ""

    fun getPWhereIs(): LocationEnum {
        return this.whereIs
    }

    fun getPlace(): String {
        return this.place
    }

    fun getDivision(): String {
        return this.division
    }

    fun getLabel(): String {
        return this.label
    }

    fun getLong(): String {
        return this.long
    }

    fun getLat(): String {
        return this.lat
    }

    fun setWhereIs(local: LocationEnum) {
   this.whereIs = local
    }

    fun setPlace(place: String) {
        this.place = place
    }

    fun setDivision(division: String) {
        this.division = division
    }

    fun setLabel(label: String) {
        this.label = label
    }

    fun setLong(long: String) {
        this.long = long
    }

    fun setLat(lat: String) {
        this.lat = lat
    }

}