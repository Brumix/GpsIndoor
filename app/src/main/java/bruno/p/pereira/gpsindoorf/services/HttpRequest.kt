package bruno.p.pereira.gpsindoorf.services

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.util.Log
import bruno.p.pereira.gpsindoorf.TAG
import bruno.p.pereira.gpsindoorf.models.Beacon
import com.android.volley.toolbox.StringRequest
import com.android.volley.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject


private const val URL = "https://23f5-188-250-33-145.ngrok.io"

private const val ACTION_GET = "bruno.p.pereira.gpsindoorf.services.action.GET"
private const val ACTION_POST = "bruno.p.pereira.gpsindoorf.services.action.POST"

// TODO: Rename parameters
private const val IDBEACON = "bruno.p.pereira.gpsindoorf.services.extra.IDBEACON"
private const val EXTRA_PARAM2 = "bruno.p.pereira.gpsindoorf.services.extra.PARAM2"


class HttpRequest : IntentService("HttpRequest") {

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            ACTION_GET -> {
                val param1 = intent.getIntExtra(IDBEACON, -1)
                handleActionGET(param1)
            }
            ACTION_POST -> {
                val param1 = intent.getStringExtra(IDBEACON)
                val param2 = intent.getStringExtra(EXTRA_PARAM2)
                handleActionPOST(param1, param2)
            }
        }
    }


    private fun handleActionGET(param1: Int) {

        var url = "$URL/beacon"
        if (param1 != -1)
            url = "$url/$param1"

        Log.v(TAG,"[URL] $url")
        val queue = SingletonVolleyRequestQueue.getInstance(this.applicationContext).requestQueue
        // Create Request with Listeners:
        // GET METHOD
        val request = StringRequest(
            Request.Method.GET, url,
            { //Handle Response
                    response ->
                getResponseGEt(response,param1)
            },
            { //Handle Error
                    error ->
                Log.e(TAG, "launchAsyncVolleyHttpRequest(): Response.Listener Error=$error")
            }
        )
        queue.add(request)
    }

    private fun getResponseGEt(resp: String, id:Int) {
        Log.v(TAG, resp)
        val gson = Gson()
        if (id != -1) {
          val  info : Beacon = gson.fromJson(resp, Beacon::class.java)
            Log.v(TAG, "> From JSON String:  $info")
        }else{

            val arrayTutorialType = object : TypeToken<Array<Beacon>>() {}.type
            val info: Array<Beacon> = gson.fromJson(resp, arrayTutorialType)
            Log.v(TAG, "> From JSON String:  ${info[2]}")
        }

    }

    private fun handleActionPOST(param1: String?, param2: String?) {
        TODO("Handle action Baz")
    }

    fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
        when (val value = this[it]) {
            is JSONArray -> {
                val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
                JSONObject(map).toMap().values.toList()
            }
            is JSONObject -> value.toMap()
            JSONObject.NULL -> null
            else -> value
        }
    }

    companion object {


        @JvmStatic
        fun startActionGET(context: Context, idBeacon: Int = -1) {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_GET
                putExtra(IDBEACON, idBeacon)
            }
            context.startService(intent)
        }


        @JvmStatic
        fun startActionPOST(context: Context, param1: String, param2: String) {
            val intent = Intent(context, HttpRequest::class.java).apply {
                action = ACTION_POST
                putExtra(IDBEACON, param1)
                putExtra(EXTRA_PARAM2, param2)
            }
            context.startService(intent)
        }
    }
}