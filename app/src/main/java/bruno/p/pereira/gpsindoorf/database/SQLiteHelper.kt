package bruno.p.pereira.gpsindoorf.database


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import bruno.p.pereira.gpsindoorf.models.Beacon
import bruno.p.pereira.gpsindoorf.models.DtoLocation
import bruno.p.pereira.gpsindoorf.models.EdgeModel


class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Gpsindoor.db"

        //Table Beacons
        private const val TBL_BEACONS = "beacons"
        private const val ID = "id"
        private const val NAME = "name"
        private const val MAC = "mac"
        private const val RSSI = "rssi"
        private const val PLACE = "place"


        //Table Location
        private const val TBL_LOCATION = "location"
        private const val DIVISION = "division"
        private const val LONGITUDE = "longitude"
        private const val LATITUDE = "latitude"
        private const val LOC_TIME = "loc_time"

        //Table Location
        private const val TBL_EDGES = "edges"
        private const val NODEA = "nodeA"
        private const val NODEB = "nodeB"
        private const val WEIGHT = "weight"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblBeacons = ("CREATE TABLE " + TBL_BEACONS + "("
                + ID + " INTEGER, "
                + NAME + " TEXT, "
                + MAC + " TEXT PRIMARY KEY, "
                + RSSI + " INTEGER )")

        val createTblLocation = ("CREATE TABLE " + TBL_LOCATION + "("
                + MAC + " TEXT, "
                + PLACE + " TEXT, "
                + DIVISION + " TEXT, "
                + LONGITUDE + " TEXT, "
                + LATITUDE + " TEXT, "
                + LOC_TIME + " TEXT )")

        val createTblEdges = ("CREATE TABLE " + TBL_EDGES + "("
                + NODEA + " TEXT, "
                + NODEB + " TEXT, "
                + WEIGHT + " TEXT )")



        db?.execSQL(createTblBeacons)
        db?.execSQL(createTblLocation)
        db?.execSQL(createTblEdges)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_BEACONS")
        db.execSQL("DROP TABLE IF EXISTS $TBL_LOCATION")
        onCreate(db)
    }

    fun insertBeacon(ble: Beacon): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, ble.id)
        contentValues.put(NAME, ble.name)
        contentValues.put(MAC, ble.mac)
        contentValues.put(RSSI, ble.rssi)

        val success = db.insert(TBL_BEACONS, null, contentValues)

        db.close()

        return success
    }

    fun insertLocation(dto: DtoLocation): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(MAC, dto.mac)
        contentValues.put(PLACE, dto.place)
        contentValues.put(DIVISION, dto.division)
        contentValues.put(LONGITUDE, dto.longitude)
        contentValues.put(LATITUDE, dto.latitude)
        contentValues.put(LOC_TIME, dto.loc_time ?: "-1")


        val success = db.insert(TBL_LOCATION, null, contentValues)

        db.close()

        return success
    }

    fun insertEdges(edge: EdgeModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(NODEA, edge.nodeA)
        contentValues.put(NODEB, edge.nodeB)
        contentValues.put(WEIGHT, edge.weight)

        val success = db.insert(TBL_EDGES, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllBeacons(): ArrayList<Beacon> {
        val bleList: ArrayList<Beacon> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_BEACONS"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var mac: String
        var rssi: Int

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(ID))
                name = cursor.getString(cursor.getColumnIndex(NAME))
                mac = cursor.getString(cursor.getColumnIndex(MAC))
                rssi = cursor.getString(cursor.getColumnIndex(RSSI)).toInt()

                val ble = Beacon(id = id, name = name, mac = mac, rssi = rssi)
                bleList.add(ble)
            } while (cursor.moveToNext())
        }
        return bleList
    }

    @SuppressLint("Range")
    fun getFirstBeaconbyMac(mac: String): Beacon? {
        val selectQuery = "SELECT * FROM $TBL_BEACONS WHERE mac = \'$mac\'"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return null
        }

        val idB: Int
        val nameB: String
        val macB: String
        val rssiB: Int

        if (cursor.moveToFirst()) {

            idB = cursor.getInt(cursor.getColumnIndex(ID))
            nameB = cursor.getString(cursor.getColumnIndex(NAME))
            macB = cursor.getString(cursor.getColumnIndex(MAC))
            rssiB = cursor.getString(cursor.getColumnIndex(RSSI)).toInt()

            return Beacon(id = idB, name = nameB, mac = macB, rssi = rssiB)

        }
        return null
    }

    @SuppressLint("Range")
    fun getAllLocations(): ArrayList<DtoLocation> {
        val locList: ArrayList<DtoLocation> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_LOCATION"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var macB: String
        var placeB: String
        var divisonB: String
        var longuitudeB: String
        var latitudeB: String
        var loc_timeB: String

        if (cursor.moveToFirst()) {
            do {
                macB = cursor.getString(cursor.getColumnIndex(MAC))
                placeB = cursor.getString(cursor.getColumnIndex(PLACE))
                divisonB = cursor.getString(cursor.getColumnIndex(DIVISION))
                longuitudeB = cursor.getString(cursor.getColumnIndex(LONGITUDE))
                latitudeB = cursor.getString(cursor.getColumnIndex(LATITUDE))
                loc_timeB = cursor.getString(cursor.getColumnIndex(LOC_TIME))

                val currentDto =
                    DtoLocation(macB, placeB, divisonB, longuitudeB, latitudeB, loc_timeB)
                locList.add(currentDto)
            } while (cursor.moveToNext())
        }
        return locList
    }


    @SuppressLint("Range")
    fun getAllEdges(): ArrayList<EdgeModel> {
        val edgeList: ArrayList<EdgeModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_EDGES"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var nodeA: String
        var nodeB: String
        var weight: String

        if (cursor.moveToFirst()) {
            do {
                nodeA = cursor.getString(cursor.getColumnIndex(NODEA))
                nodeB = cursor.getString(cursor.getColumnIndex(NODEB))
                weight = cursor.getString(cursor.getColumnIndex(WEIGHT))

                val currentEdge = EdgeModel(nodeA, nodeB, weight)
                edgeList.add(currentEdge)
            } while (cursor.moveToNext())
        }
        return edgeList
    }

    @SuppressLint("Range")
    fun getAllLocationbyMac(mac: String): ArrayList<DtoLocation> {
        val locList: ArrayList<DtoLocation> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_LOCATION WHERE mac = \'$mac\'"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var macB: String
        var placeB: String
        var divisonB: String
        var longuitudeB: String
        var latitudeB: String
        var locTimeB: String

        if (cursor.moveToFirst()) {
            do {
                macB = cursor.getString(cursor.getColumnIndex(MAC))
                placeB = cursor.getString(cursor.getColumnIndex(PLACE))
                divisonB = cursor.getString(cursor.getColumnIndex(DIVISION))
                longuitudeB = cursor.getString(cursor.getColumnIndex(LONGITUDE))
                latitudeB = cursor.getString(cursor.getColumnIndex(LATITUDE))
                locTimeB = cursor.getString(cursor.getColumnIndex(LOC_TIME))

                val currentDto =
                    DtoLocation(macB, placeB, divisonB, longuitudeB, latitudeB, locTimeB)
                locList.add(currentDto)
            } while (cursor.moveToNext())
        }
        return locList
    }

    @SuppressLint("Range")
    fun getFirstLocationbyMac(mac: String): DtoLocation? {
        val selectQuery = "SELECT * FROM $TBL_LOCATION WHERE mac = \'$mac\'"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return null
        }

        val macB: String
        val placeB: String
        val divisonB: String
        val longuitudeB: String
        val latitudeB: String
        val locTimeB: String

        if (cursor.moveToFirst()) {


            macB = cursor.getString(cursor.getColumnIndex(MAC))
            placeB = cursor.getString(cursor.getColumnIndex(PLACE))
            divisonB = cursor.getString(cursor.getColumnIndex(DIVISION))
            longuitudeB = cursor.getString(cursor.getColumnIndex(LONGITUDE))
            latitudeB = cursor.getString(cursor.getColumnIndex(LATITUDE))
            locTimeB = cursor.getString(cursor.getColumnIndex(LOC_TIME))

            return DtoLocation(macB, placeB, divisonB, longuitudeB, latitudeB, locTimeB)
        }
        return null
    }


    fun updateBeacons(ble: Beacon): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, ble.id)
        contentValues.put(NAME, ble.name)
        contentValues.put(MAC, ble.mac)
        contentValues.put(RSSI, ble.rssi)

        val success = db.update(TBL_BEACONS, contentValues, "id=" + ble.id, null)
        db.close()
        return success
    }

    fun updateLocation(dto: DtoLocation): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(MAC, dto.mac)
        contentValues.put(PLACE, dto.place)
        contentValues.put(DIVISION, dto.division)
        contentValues.put(LONGITUDE, dto.longitude)
        contentValues.put(LATITUDE, dto.latitude)
        contentValues.put(LOC_TIME, dto.loc_time ?: "-1")
        val mac = dto.mac
        val success = db.update(TBL_LOCATION, contentValues, "$MAC = '$mac' ", null)
        db.close()
        return success
    }

    fun updateEdge(edge: EdgeModel): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(NODEA, edge.nodeA)
        contentValues.put(NODEB, edge.nodeB)
        contentValues.put(WEIGHT, edge.weight)
        val success = db.update(TBL_EDGES, contentValues, "$NODEA = '${edge.nodeA}' ", null)
        db.close()
        return success
    }

    fun deleteBeaconsByMac(mac: String): Int {
        val db = this.writableDatabase

        val success = db.delete(TBL_BEACONS, "$MAC= '$mac'", null)
        db.close()
        return success
    }

    fun deleteLocationByMac(mac: String): Int {
        val db = this.writableDatabase

        val success = db.delete(TBL_LOCATION, "$MAC= '$mac'", null)
        db.close()
        return success
    }

    fun deleteEdge(nodeA: String): Int {
        val db = this.writableDatabase

        val success = db.delete(TBL_EDGES, "$NODEA= '$nodeA'", null)
        db.close()
        return success
    }

    fun deleteALLLocation(): Int {
        val db = this.writableDatabase

        val success = db.delete(TBL_LOCATION, " 0=0", null)
        db.close()
        return success
    }

}