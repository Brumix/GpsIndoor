package bruno.p.pereira.gpsindoorf.database


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import bruno.p.pereira.gpsindoorf.enums.LocationEnum
import bruno.p.pereira.gpsindoorf.models.Beacon
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
        private const val LONG = "long"
        private const val LAT = "lat"
        private const val PLACE = "place"
        private const val LABEl = "label"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblBeacons = ("CREATE TABLE " + TBL_BEACONS + "("
                + ID + " INTEGER, "
                + NAME + " TEXT, "
                + MAC + " TEXT PRIMARY KEY, "
                + RSSI + " INTEGER )")

        val createTblLocation = ("CREATE TABLE " + TBL_LOCATION + "("
                + MAC + " TEXT, "
                + LABEl + " TEXT, "
                + LONG + " TEXT, "
                + LAT + " TEXT, "
                + PLACE + " INTEGER )")

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

    fun insertLocation(ble: Beacon): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(MAC, ble.mac)
        contentValues.put(LABEl, ble.getLabel())
        contentValues.put(LONG, ble.getLong())
        contentValues.put(LAT, ble.getLat())
        contentValues.put(PLACE, ble.getPLace().ordinal)


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
    fun getFirstBeaconbyId(mac: String): Beacon? {
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

        var idB: Int
        var nameB: String
        var macB: String
        var rssiB: Int

        if (cursor.moveToFirst()) {

            idB = cursor.getInt(cursor.getColumnIndex(ID))
            nameB = cursor.getString(cursor.getColumnIndex(NAME))
            macB = cursor.getString(cursor.getColumnIndex(MAC))
            rssiB = cursor.getString(cursor.getColumnIndex(RSSI)).toInt()

            return Beacon(id = idB, name = nameB, mac = macB, rssi = rssiB)

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

    fun deleteBeaconsById(id: Int): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID, id)

        val success = db.delete(TBL_BEACONS, "id=$id", null)
        db.close()
        return success
    }
}