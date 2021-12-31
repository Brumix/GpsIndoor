package bruno.p.pereira.gpsindoorf.services

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.nfc.Tag
import android.util.Log
import android.widget.Toast
import bruno.p.pereira.gpsindoorf.TAG
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.models.Beacon
import com.android.volley.toolbox.StringRequest
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject


private const val URL = "https://94b4-188-250-33-145.ngrok.io"

private const val ACTION_GET = "bruno.p.pereira.gpsindoorf.services.action.GET"
private const val ACTION_POST = "bruno.p.pereira.gpsindoorf.services.action.POST"

// TODO: Rename parameters
private const val IDBEACON = "bruno.p.pereira.gpsindoorf.services.extra.IDBEACON"
private const val NAMEBEACON = "bruno.p.pereira.gpsindoorf.services.extra.NAMEBEACON"
private const val MACBEACON = "bruno.p.pereira.gpsindoorf.services.extra.MACEBEACON"
private const val RSSIBEACON = "bruno.p.pereira.gpsindoorf.services.extra.RSSI"


class HttpRequest : IntentService("HttpRequest") {

    private val db: SQLiteHelper by lazy {
        SQLiteHelper(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_GET -> {
                val param1 = intent.getStringExtra(MACBEACON)!!
                handleActionGET(param1)
            }
            ACTION_POST -> {
                Log.v(TAG, "cheguei")
                val id = intent.getIntExtra(IDBEACON, -1)
                val name = intent.getStringExtra(NAMEBEACON)!!
                val mac = intent.getStringExtra(MACBEACON)!!
                val rssi = intent.getIntExtra(RSSIBEACON, -1)

                handleActionPOST(Beacon(id, name, mac, rssi))
            }
        }
    }


    private fun handleActionGET(param1: String) {

        var url = "$URL/beacon"
        if (param1 != "") {
            url = "$url/$param1"
        }

        Log.v(TAG, "[URL] $url")
        val queue = SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue
        // Create Request with Listeners:
        // GET METHOD
        val request = StringRequest(
            Request.Method.GET, url,
            { //Handle Response
                    response ->
                getResponseGEt(response, param1)
            },
            { //Handle Error
                    error ->
                Toast.makeText(this, "ERROR : ENDPOINT NOT FOUND", Toast.LENGTH_LONG).show()
                Log.e(TAG, "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error")
            }
        )
        queue.add(request)
    }

    private fun handleActionPOST(beacon: Beacon) {

        if (beacon.id == -1 && beacon.rssi == -1) {
            return
        }
        val url = "$URL/beacon"
        Log.v(TAG, "[URL] $url")

        val queue = SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue
        val body = JSONObject()
        body.accumulate("id", beacon.id)
        body.accumulate("name", beacon.name)
        body.accumulate("mac", beacon.mac)
        body.accumulate("rssi", beacon.rssi)
        // Create Request with Listeners:
        //  1 listener to handle Response from the provided URL;
        //  1 listener for error handling.
        val request = JsonObjectRequest(
            Request.Method.POST, url, body,
            { //Handle Response
                    response ->
                Log.v(TAG, "Beacon added with sucess to the cloud!!")

            },
            { //Handle Error
                    error ->
                Log.e(
                    TAG,
                    "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error"
                )
            })

        queue.add(request)
    }

    private fun getResponseGEt(resp: String, mac: String) {

        val gson = Gson()
        val listBeacons: MutableList<Beacon> = this.db.getAllBeacons()
        val mapDB = mutableMapOf<String, Beacon>()
        for (i in listBeacons)
            mapDB[i.mac] = i


        if (mac != "") {
            val info: Beacon = gson.fromJson(resp, Beacon::class.java)
            if (mapDB.containsKey(info.mac))
                return
            info.id = db.getAllBeacons().size + 1
            db.insertStudent(info)
            Log.v(TAG, "[HTTPREQUEST] BEACON ADDED $info")

        } else {
            val info: Array<Beacon> =
                gson.fromJson(resp, object : TypeToken<Array<Beacon>>() {}.type)

            for (i in info) {
                if (!mapDB.containsKey(i.mac)) {
                    i.id = db.getAllBeacons().size + 1
                    db.insertStudent(i)
                    Log.v(TAG, "[HTTPREQUEST] BEACON ADDED $i")
                }
            }
        }

    }


    companion object {

        @JvmStatic
        fun startActionGET(context: Context, idBeacon: String = "") {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_GET
                putExtra(MACBEACON, idBeacon)
            }
            context.startService(intent)
        }


        @JvmStatic
        fun startActionPOST(context: Context, beacon: Beacon) {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_POST
                putExtra(IDBEACON, beacon.id)
                putExtra(NAMEBEACON, beacon.name)
                putExtra(MACBEACON, beacon.mac)
                putExtra(RSSIBEACON, beacon.rssi)
            }
            context.startService(intent)
        }
    }
}