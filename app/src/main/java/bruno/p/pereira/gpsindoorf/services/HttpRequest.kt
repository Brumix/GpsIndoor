package bruno.p.pereira.gpsindoorf.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import bruno.p.pereira.gpsindoorf.TAG
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.models.Beacon
import bruno.p.pereira.gpsindoorf.models.DtoLocation
import bruno.p.pereira.gpsindoorf.models.EdgeModel
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject


private const val URL = "https://1a6c-188-250-33-145.ngrok.io"


private const val ACTION_GET_USER = "bruno.p.pereira.gpsindoorf.services.action.GET_USER"
private const val ACTION_GET_BEACONS = "bruno.p.pereira.gpsindoorf.services.action.GET_BEACONS"
private const val ACTION_GET_LOCATION = "bruno.p.pereira.gpsindoorf.services.action.GET_LOCATION"
private const val ACTION_POST_BEACONS = "bruno.p.pereira.gpsindoorf.services.action.POST_BEACONS"
private const val ACTION_POST_LOC = "bruno.p.pereira.gpsindoorf.services.action.POST_LOC"
private const val ACTION_DELETE_LOC = "bruno.p.pereira.gpsindoorf.services.action.DELETE_LOC"
private const val ACTION_GET_HIST = "bruno.p.pereira.gpsindoorf.services.action.GET_HIST"
private const val ACTION_GET_EDGE = "bruno.p.pereira.gpsindoorf.services.action.GET_EDGE"
private const val ACTION_POST_EDGE = "bruno.p.pereira.gpsindoorf.services.action.POST_EDGE"
private const val ACTION_DELETE_EDGE = "bruno.p.pereira.gpsindoorf.services.action.DELETE_EDGE"


// TODO: Rename parameters
private const val IDBEACON = "bruno.p.pereira.gpsindoorf.services.extra.IDBEACON"
private const val NAMEBEACON = "bruno.p.pereira.gpsindoorf.services.extra.NAMEBEACON"
private const val MACBEACON = "bruno.p.pereira.gpsindoorf.services.extra.MACEBEACON"
private const val RSSIBEACON = "bruno.p.pereira.gpsindoorf.services.extra.RSSI"
private const val PLACELOC = "bruno.p.pereira.gpsindoorf.services.extra.PLACELOC"
private const val DIVISIONLOC = "bruno.p.pereira.gpsindoorf.services.extra.DIVISIONLOC"
private const val LONGUITUDELOC = "bruno.p.pereira.gpsindoorf.services.extra.LONGUITUDELOC"
private const val LATITUDELOC = "bruno.p.pereira.gpsindoorf.services.extra.LATITUDELOC"
private const val NODEA = "bruno.p.pereira.gpsindoorf.services.extra.NODEA"
private const val NODEB = "bruno.p.pereira.gpsindoorf.services.extra.NODEB"
private const val WEIGHT = "bruno.p.pereira.gpsindoorf.services.extra.WEIGHT"

class HttpRequest : IntentService("HttpRequest") {

    private val db: SQLiteHelper by lazy {
        SQLiteHelper(this)
    }


    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_GET_USER -> handleActionGETUser()
            ACTION_GET_BEACONS -> {
                val param1 = intent.getStringExtra(MACBEACON)!!
                handleActionGETBeacon(param1)
            }
            ACTION_GET_LOCATION -> {
                val param1 = intent.getStringExtra(MACBEACON)!!
                handleActionGETLocation(param1)
            }
            ACTION_POST_BEACONS -> {
                val id = intent.getIntExtra(IDBEACON, -1)
                val name = intent.getStringExtra(NAMEBEACON)!!
                val mac = intent.getStringExtra(MACBEACON)!!
                val rssi = intent.getIntExtra(RSSIBEACON, -1)

                handleActionPOSTBeacon(Beacon(id, name, mac, rssi))
            }
            ACTION_POST_LOC -> {
                val mac = intent.getStringExtra(MACBEACON)!!
                val place = intent.getStringExtra(PLACELOC)!!
                val division = intent.getStringExtra(DIVISIONLOC)!!
                val longuitude = intent.getStringExtra(LONGUITUDELOC)!!
                val latitude = intent.getStringExtra(LATITUDELOC)!!
                handleActionPOSTLoc(DtoLocation(mac, place, division, longuitude, latitude))
            }
            ACTION_DELETE_LOC -> {
                val mac = intent.getStringExtra(MACBEACON)!!
                handleActionDELETELoc(mac)
            }
            ACTION_GET_HIST -> {
                val mac = intent.getStringExtra(MACBEACON)!!
                handleActionGETHist(mac)
            }
            ACTION_GET_EDGE -> handleActionGETEdge()
            ACTION_POST_EDGE -> {
                val nodeA = intent.getStringExtra(NODEA)!!
                val nodeB = intent.getStringExtra(NODEB)!!
                val weight = intent.getStringExtra(WEIGHT)!!
                handleActionPOSTEdge(EdgeModel(nodeA, nodeB, weight))
            }
            ACTION_DELETE_EDGE ->{
                val nodeA = intent.getStringExtra(NODEA)!!
                handleActionDELETEEdge(nodeA)
            }
        }
    }


    private fun handleActionGETUser() {
        val url = "$URL/beacon/user/${Build.ID}"

        Log.v(TAG, "[URL] $url")
        val queue = SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue
        // Create Request with Listeners:
        // GET METHOD
        val request = StringRequest(
            Request.Method.GET, url,
            { //Handle Response
                    _ ->
                Log.v(TAG, "[HTTPREQUEST] User Added to the system")
            },
            { //Handle Error
                    error ->
                Toast.makeText(this, "ERROR : ENDPOINT NOT FOUND", Toast.LENGTH_LONG).show()
                Log.e(TAG, "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error")
            }
        )
        queue.add(request)
    }

    private fun handleActionGETBeacon(param1: String) {

        var url = "$URL/beacon/${Build.ID}"
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
                getResponseGETBeacons(response, param1)
            },
            { //Handle Error
                    error ->
                Toast.makeText(this, "ERROR : ENDPOINT NOT FOUND", Toast.LENGTH_LONG).show()
                Log.e(TAG, "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error")
            }
        )
        queue.add(request)
    }

    private fun handleActionGETLocation(mac: String) {

        var url = "$URL/beacon/${Build.ID}/loc"
        if (mac != "") {
            url = "$url/$mac"
        }

        Log.v(TAG, "[URL] $url")
        val queue = SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue
        // Create Request with Listeners:
        // GET METHOD
        val request = StringRequest(
            Request.Method.GET, url,
            { //Handle Response
                    response ->
                getResponseGETLocation(response, mac)
            },
            { //Handle Error
                    error ->
                Toast.makeText(this, "ERROR : ENDPOINT NOT FOUND", Toast.LENGTH_LONG).show()
                Log.e(TAG, "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error")
            }
        )
        queue.add(request)
    }

    private fun handleActionGETHist(mac: String) {
        val url = "$URL/beacon/${Build.ID}/loc/hist/$mac"


        Log.v(TAG, "[URL] $url")
        val queue = SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue
        // Create Request with Listeners:
        // GET METHOD
        val request = StringRequest(
            Request.Method.GET, url,
            { //Handle Response
                    response ->
                getResponseGETHist(response, mac)
            },
            { //Handle Error
                    error ->
                Toast.makeText(this, "ERROR : ENDPOINT NOT FOUND", Toast.LENGTH_LONG).show()
                Log.e(TAG, "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error")
            }
        )
        queue.add(request)
    }

    private fun handleActionPOSTBeacon(beacon: Beacon) {

        if (beacon.id == -1 && beacon.rssi == -1) {
            return
        }
        val url = "$URL/beacon/${Build.ID}"
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
                    _ ->
                // val gson = Gson()
                // val info: DtoLocation = gson.fromJson(response.toString(), DtoLocation::class.java)
                Log.v(TAG, "[HTTPREQUEST]Beacon added with sucess to the cloud!!")

            },
            { //Handle Error
                    error ->
                Toast.makeText(this, "ERROR : ENDPOINT NOT FOUND", Toast.LENGTH_LONG).show()
                Log.e(TAG, "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error")
            })

        queue.add(request)
    }

    private fun handleActionPOSTLoc(loc: DtoLocation) {

        val url = "$URL/beacon/${Build.ID}/loc"
        Log.v(TAG, "[URL] $url")

        val queue = SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue
        val body = JSONObject()
        body.accumulate("mac", loc.mac)
        body.accumulate("place", loc.place)
        body.accumulate("division", loc.division)
        body.accumulate("longitude", loc.longitude)
        body.accumulate("latitude", loc.latitude)
        // Create Request with Listeners:
        //  1 listener to handle Response from the provided URL;
        //  1 listener for error handling.
        val request = JsonObjectRequest(
            Request.Method.POST, url, body,
            { //Handle Response
                    _ ->
                Log.v(TAG, "[HTTPREQUEST] Location added with sucess to the cloud!!")

            },
            { //Handle Error
                    error ->
                Toast.makeText(this, "ERROR : ENDPOINT NOT FOUND", Toast.LENGTH_LONG).show()
                Log.e(
                    TAG,
                    "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error"
                )
            })

        queue.add(request)
    }

    private fun handleActionDELETELoc(mac: String) {
        var url = "$URL/beacon/${Build.ID}/loc"

        if (mac != "")
            url += "/$mac"

        Log.v(TAG, "[URL] $url")
        val queue = SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue
        // Create Request with Listeners:
        // GET METHOD
        val request = StringRequest(
            Request.Method.DELETE, url,
            { //Handle Response
                    response ->
                Log.v(TAG, "[HTTPREQUEST] Loc deleted from cloud !!")
            },
            { //Handle Error
                    error ->
                Toast.makeText(this, "ERROR : ENDPOINT NOT FOUND", Toast.LENGTH_LONG).show()
                Log.e(TAG, "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error")
            }
        )
        queue.add(request)
    }

    private fun handleActionGETEdge() {
        val url = "$URL/beacon/${Build.ID}/edge"

        Log.v(TAG, "[URL] $url")
        val queue = SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue
        // Create Request with Listeners:
        // GET METHOD
        val request = StringRequest(
            Request.Method.GET, url,
            { //Handle Response
                    respose ->
                getResponseGETEdges(respose)
            },
            { //Handle Error
                    error ->
                Toast.makeText(this, "ERROR : ENDPOINT NOT FOUND", Toast.LENGTH_LONG).show()
                Log.e(TAG, "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error")
            }
        )
        queue.add(request)
    }

    private fun handleActionPOSTEdge(edge: EdgeModel) {

        val url = "$URL/beacon/${Build.ID}/edge"
        Log.v(TAG, "[URL] $url")

        val queue = SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue
        val body = JSONObject()
        body.accumulate("nodeA", edge.nodeA)
        body.accumulate("nodeB", edge.nodeB)
        body.accumulate("weight", edge.weight)
        // Create Request with Listeners:
        //  1 listener to handle Response from the provided URL;
        //  1 listener for error handling.
        val request = JsonObjectRequest(
            Request.Method.POST, url, body,
            { //Handle Response
                    _ ->
                Log.v(TAG, "[HTTPREQUEST] Edge added with sucess to the cloud!!")

            },
            { //Handle Error
                    error ->
                Toast.makeText(this, "ERROR : ENDPOINT NOT FOUND", Toast.LENGTH_LONG).show()
                Log.e(
                    TAG,
                    "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error"
                )
            })

        queue.add(request)
    }

    private fun handleActionDELETEEdge(nodeA: String) {
        var url = "$URL/beacon/${Build.ID}/edge"

        if (nodeA != "" )
            url += "/$nodeA"

        Log.v(TAG, "[URL] $url")
        val queue = SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue
        // Create Request with Listeners:
        // GET METHOD
        val request = StringRequest(
            Request.Method.DELETE, url,
            { //Handle Response
                    response ->
                Log.v(TAG, "[HTTPREQUEST] Loc deleted from cloud !!")
            },
            { //Handle Error
                    error ->
                Toast.makeText(this, "ERROR : ENDPOINT NOT FOUND", Toast.LENGTH_LONG).show()
                Log.e(TAG, "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error")
            }
        )
        queue.add(request)
    }

    private fun getResponseGETBeacons(resp: String, mac: String) {
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
            db.insertBeacon(info)
            Log.v(TAG, "[HTTPREQUEST] BEACON ADDED $info")

        } else {
            val info: Array<Beacon> =
                gson.fromJson(resp, object : TypeToken<Array<Beacon>>() {}.type)
            for (i in info) {
                if (!mapDB.containsKey(i.mac)) {
                    i.id = db.getAllBeacons().size + 1
                    db.insertBeacon(i)
                    Log.v(TAG, "[HTTPREQUEST] BEACON ADDED $i")
                }
            }
        }

    }

    private fun getResponseGETLocation(resp: String, mac: String) {
        val gson = Gson()
        val listLocations: MutableList<DtoLocation> = this.db.getAllLocations()
        val mapDB = mutableMapOf<String, DtoLocation>()
        for (i in listLocations)
            mapDB[i.mac] = i


        if (mac != "") {
            val info: DtoLocation = gson.fromJson(resp, DtoLocation::class.java)
            if (mapDB.containsKey(info.mac)) {
                db.updateLocation(info)
                Log.v(TAG, "[HTTPREQUEST] LOCATION UPDATED $info")
            } else {
                db.insertLocation(info)
                Log.v(TAG, "[HTTPREQUEST] LOCATION ADDED $info")
            }
        } else {
            val info: Array<DtoLocation> =
                gson.fromJson(resp, object : TypeToken<Array<DtoLocation>>() {}.type)

            for (i in info) {
                if (mapDB.containsKey(i.mac)) {
                    db.updateLocation(i)
                    Log.v(TAG, "[HTTPREQUEST] LOCATION UPDATED $i")
                } else {
                    db.insertLocation(i)
                    Log.v(TAG, "[HTTPREQUEST] LOCATION ADDED $i")
                }

            }
        }
    }

    private fun getResponseGETHist(resp: String, mac: String) {
        val gson = Gson()
        val info: Array<DtoLocation> =
            gson.fromJson(resp, object : TypeToken<Array<DtoLocation>>() {}.type)
        if (info.isEmpty())
            return
        db.deleteLocationByMac(mac)
        for (i in info) {
            i.mac = mac
            db.insertLocation(i)
        }
    }

    private fun getResponseGETEdges(resp: String) {


        val gson = Gson()
        val listEdges: MutableList<EdgeModel> = this.db.getAllEdges()
        val mapDB = mutableMapOf<String, EdgeModel>()
        for (i in listEdges)
            mapDB[i.nodeA + i.nodeB] = i


        val info: Array<EdgeModel> =
            gson.fromJson(resp, object : TypeToken<Array<EdgeModel>>() {}.type)

        for (i in info) {
            if (mapDB.containsKey(i.nodeA + i.nodeB)) {
                db.updateEdge(i)
                Log.v(TAG, "[HTTPREQUEST] EDGE UPDATED $i")
            } else {
                db.insertEdges(i)
                Log.v(TAG, "[HTTPREQUEST] EDGE ADDED $i")
            }

        }
    }


    companion object {

        @JvmStatic
        fun startActionGETUser(context: Context) {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_GET_USER
            }
            context.startService(intent)
        }


        @JvmStatic
        fun startActionGETBeacons(context: Context, macBeacon: String = "") {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_GET_BEACONS
                putExtra(MACBEACON, macBeacon)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionGETLocation(context: Context, macBeacon: String = "") {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_GET_LOCATION
                putExtra(MACBEACON, macBeacon)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionGETHist(context: Context, macBeacon: String = "") {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_GET_HIST
                putExtra(MACBEACON, macBeacon)
            }
            context.startService(intent)
        }


        @JvmStatic
        fun startActionPOSTBeacons(context: Context, beacon: Beacon) {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_POST_BEACONS
                putExtra(IDBEACON, beacon.id)
                putExtra(NAMEBEACON, beacon.name)
                putExtra(MACBEACON, beacon.mac)
                putExtra(RSSIBEACON, beacon.rssi)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionPOSTLoc(context: Context, loc: DtoLocation) {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_POST_LOC
                putExtra(MACBEACON, loc.mac)
                putExtra(PLACELOC, loc.place)
                putExtra(DIVISIONLOC, loc.division)
                putExtra(LONGUITUDELOC, loc.longitude)
                putExtra(LATITUDELOC, loc.latitude)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionDELETELoc(context: Context, mac: String = "") {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_DELETE_LOC
                putExtra(MACBEACON, mac)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionGETEdge(context: Context) {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_GET_EDGE
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionPOSTEdge(context: Context, edgeModel: EdgeModel) {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_POST_EDGE
                putExtra(NODEA, edgeModel.nodeA)
                putExtra(NODEB, edgeModel.nodeB)
                putExtra(WEIGHT, edgeModel.weight)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionDELETEEdge(context: Context, nodeA: String = "") {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_DELETE_EDGE
                putExtra(NODEA, nodeA)
            }
            context.startService(intent)
        }
    }
}