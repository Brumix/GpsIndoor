package bruno.p.pereira.gpsindoorf.database


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import bruno.p.pereira.gpsindoorf.models.Beacon
import java.lang.Exception
//comentario
class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Gpsindoor.db"
        private const val TBL_BEACONS = "beacons"
        private const val ID = "id"
        private const val NAME = "name"
        private const val MAC = "mac"
        private const val RSSI = "rssi"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblStudent = ("CREATE TABLE " + TBL_BEACONS + "("
                + ID + " INTEGER PRIMARY KEY, "
                + NAME + " TEXT, "
                + MAC + " TEXT, "
                + RSSI + " INTEGER )")
        db?.execSQL(createTblStudent)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_BEACONS")
        onCreate(db)
    }

    fun insertStudent(ble: Beacon): Long {
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