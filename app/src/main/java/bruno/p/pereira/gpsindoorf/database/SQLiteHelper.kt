package bruno.p.pereira.gpsindoorf.database


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import bruno.p.pereira.gpsindoorf.TAG
import bruno.p.pereira.gpsindoorf.enums.LocationEnum
import bruno.p.pereira.gpsindoorf.models.Beacon
import bruno.p.pereira.gpsindoorf.models.DtoLocation
import java.lang.Exception
//comentario
class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Gpsindoor.db"
        private const val TBL_BEACONS = "beacons"
        private const val TBL_LOCATION = "location"
        private const val ID = "id"
        private const val NAME = "name"
        private const val MAC = "mac"
        private const val RSSI = "rssi"

        private const val PLACE = "place"
        private const val DIVISION = "division"
        private const val LONGITUDE = "longitude"
        private const val LATITUDE = "latitude"


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
                + LATITUDE + " TEXT )")




        db?.execSQL(createTblBeacons)
        db?.execSQL(createTblLocation)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_BEACONS")
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_LOCATION")
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

        val success = db.insert(TBL_LOCATION, null, contentValues)

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

        if (cursor.moveToFirst()) {


            macB = cursor.getString(cursor.getColumnIndex(MAC))
            placeB = cursor.getString(cursor.getColumnIndex(PLACE))
            divisonB = cursor.getString(cursor.getColumnIndex(DIVISION))
            longuitudeB = cursor.getString(cursor.getColumnIndex(LONGITUDE))
            latitudeB = cursor.getString(cursor.getColumnIndex(LATITUDE))

            return DtoLocation(macB, placeB, divisonB, longuitudeB, latitudeB)
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
        val mac = dto.mac
        val success = db.update(TBL_BEACONS, contentValues, "$MAC = '$mac' ", null)
        db.close()
        return success
    }

    fun deleteBeaconsById(id: Int): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, id)

        val success = db.delete(TBL_BEACONS, "id=$id", null)
        db.close()
        return success
    }

    fun deleteLocationByMac(mac: String): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(MAC, mac)

        val success = db.delete(TBL_BEACONS, "$MAC= '$mac'", null)
        db.close()
        return success
    }
}