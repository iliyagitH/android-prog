package com.example.androidapp

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.telephony.*
import android.util.Log
import androidx.core.app.ActivityCompat
import org.json.JSONArray
import org.json.JSONObject
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import java.io.File
import java.util.Date

class LocationService : Service(), LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var telephonyManager: TelephonyManager

    var lastPayload: String? = null
    var serverIp: String = ""
    var zmqThread: Thread? = null
    var isWorking = true

    companion object {
        const val EXTRA_SERVER_IP = "server_ip"
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serverIp = intent?.getStringExtra(EXTRA_SERVER_IP) ?: ""
        isWorking = true

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 0f, this)
        }

        if (serverIp.isNotEmpty()) {
            startZmqThread()
        }

        return START_STICKY
    }

    override fun onLocationChanged(location: Location) {
        val locJson = JSONObject()
        locJson.put("Latitude", location.latitude)
        locJson.put("Longitude", location.longitude)
        locJson.put("Altitude", location.altitude)
        locJson.put("Current Time", Date(location.time).toString())
        locJson.put("Accuracy", location.accuracy.toDouble())

        val root = JSONObject()
        root.put("location", locJson)
        root.put("telephony", getCellInfo())

        val jsonStr = root.toString()

        try {
            val file = File(getExternalFilesDir(null), "location_log.json")
            file.appendText(jsonStr + "\n")
        } catch (e: Exception) {
            Log.e("LocationService", e.message ?: "error")
        }

        sendBroadcast(Intent("LocationUpdates").apply {
            putExtra("json", jsonStr)
        })

        lastPayload = jsonStr
    }

    private fun getCellInfo(): JSONArray {
        val result = JSONArray()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return result
        }

        val cells = telephonyManager.allCellInfo ?: return result

        for (cell in cells) {
            val obj = JSONObject()
            obj.put("isRegistered", cell.isRegistered)

            if (cell is CellInfoLte) {
                val id = cell.cellIdentity
                val sig = cell.cellSignalStrength
                obj.put("type", "LTE")
                obj.put("mcc", id.mccString)
                obj.put("mnc", id.mncString)
                obj.put("pci", id.pci)
                obj.put("tac", id.tac.toString())
                obj.put("earfcn", id.earfcn)
                obj.put("rsrp", sig.rsrp)
                obj.put("rsrq", sig.rsrq)
                obj.put("rssi", sig.rssi)
                obj.put("rssnr", sig.rssnr)
                obj.put("timingAdvance", sig.timingAdvance)
            } else if (cell is CellInfoGsm) {
                val id = cell.cellIdentity
                val sig = cell.cellSignalStrength
                obj.put("type", "GSM")
                obj.put("mcc", id.mccString)
                obj.put("mnc", id.mncString)
                obj.put("lac", id.lac.toString())
                obj.put("arfcn", id.arfcn)
                obj.put("bsic", id.bsic)
                obj.put("dbm", sig.dbm)
            } else if (cell is CellInfoNr) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val id = cell.cellIdentity as CellIdentityNr
                    val sig = cell.cellSignalStrength as CellSignalStrengthNr
                    obj.put("type", "NR")
                    obj.put("mcc", id.mccString)
                    obj.put("mnc", id.mncString)
                    obj.put("pci", id.pci)
                    obj.put("nci", id.nci.toString())
                    obj.put("nrarfcn", id.nrarfcn)
                    obj.put("ssRsrp", sig.ssRsrp)
                    obj.put("ssRsrq", sig.ssRsrq)
                    obj.put("ssSinr", sig.ssSinr)
                }
            }

            result.put(obj)
        }

        return result
    }

    private fun startZmqThread() {
        zmqThread = Thread {
            val context = ZContext()
            val socket = context.createSocket(SocketType.PUSH)
            socket.linger = 0
            socket.connect("tcp://$serverIp:2222")

            while (isWorking) {
                val payload = lastPayload
                if (payload != null) {
                    try {
                        socket.send(payload.toByteArray(ZMQ.CHARSET), 0)
                    } catch (e: Exception) {
                        Log.e("LocationService", e.message ?: "zmq error")
                    }
                }
                Thread.sleep(2000)
            }

            socket.close()
            context.close()
        }
        zmqThread?.start()
    }

    override fun onDestroy() {
        isWorking = false
        locationManager.removeUpdates(this)
        zmqThread?.interrupt()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
