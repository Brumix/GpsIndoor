package bruno.p.pereira.gpsindoorf.models

import bruno.p.pereira.gpsindoorf.enums.LocationEnum

open class Location {

    private var place: LocationEnum = LocationEnum.UNKNOWN
    private var label: String = ""
    private var long: String = ""
    private var lat: String = ""

    fun getPLace(): LocationEnum {
        return this.place
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

    fun setPLace(local: Int) {
        if (LocationEnum.CLOUD.ordinal == local)
            this.place = LocationEnum.CLOUD
        if (LocationEnum.LOCAl.ordinal == local)
            this.place = LocationEnum.LOCAl
        else
            this.place = LocationEnum.UNKNOWN
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