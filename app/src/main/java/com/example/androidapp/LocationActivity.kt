package com.example.androidapp

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import org.json.JSONObject

class LocationActivity : AppCompatActivity() {

    private lateinit var tvLat: TextView
    private lateinit var tvLon: TextView
    private lateinit var tvAlt: TextView
    private lateinit var tvTime: TextView
    private lateinit var tvAccuracy: TextView
    private lateinit var tvTelemetry: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnStart: Button
    private lateinit var btnBack: Button
    private lateinit var etIp: EditText

    var isRunning = false

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val jsonStr = intent?.getStringExtra("json") ?: return
            val root = JSONObject(jsonStr)
            val loc = root.optJSONObject("location") ?: return

            tvLat.setText("Широта: " + loc.optDouble("Latitude").toString())
            tvLon.setText("Долгота: " + loc.optDouble("Longitude").toString())
            tvAlt.setText("Высота: " + loc.optDouble("Altitude").toString())
            tvTime.setText("Время: " + loc.optString("Current Time"))
            tvAccuracy.setText("Точность: " + loc.optDouble("Accuracy").toString())

            val telephony = root.optJSONArray("telephony")
            if (telephony != null && telephony.length() > 0) {
                val cell = telephony.optJSONObject(0)
                if (cell != null) {
                    val type = cell.optString("type")
                    if (type == "LTE") {
                        tvTelemetry.setText("Тип: LTE | PCI: " + cell.optInt("pci") + " | RSRP: " + cell.optInt("rsrp") + " | RSRQ: " + cell.optInt("rsrq"))
                    } else if (type == "GSM") {
                        tvTelemetry.setText("Тип: GSM | DBM: " + cell.optInt("dbm"))
                    } else if (type == "NR") {
                        tvTelemetry.setText("Тип: NR (5G) | PCI: " + cell.optInt("pci") + " | RSRP: " + cell.optInt("ssRsrp"))
                    } else {
                        tvTelemetry.setText("Нет данных сети")
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        tvLat = findViewById(R.id.tv_lat) as TextView
        tvLon = findViewById(R.id.tv_lon) as TextView
        tvAlt = findViewById(R.id.tv_alt) as TextView
        tvTime = findViewById(R.id.tv_time) as TextView
        tvAccuracy = findViewById(R.id.tv_accuracy) as TextView
        tvTelemetry = findViewById(R.id.tv_telemetry) as TextView
        tvStatus = findViewById(R.id.tv_service_status) as TextView
        btnStart = findViewById(R.id.btn_start_service) as Button
        btnBack = findViewById(R.id.back_to_main) as Button
        etIp = findViewById(R.id.et_server_ip) as EditText

        btnBack.setOnClickListener {
            finish()
        }

        btnStart.setOnClickListener {
            if (!isRunning) {
                val ip = etIp.text.toString().trim()
                val serviceIntent = Intent(this, LocationService::class.java)
                serviceIntent.putExtra(LocationService.EXTRA_SERVER_IP, ip)
                startService(serviceIntent)
                tvStatus.setText("Статус: Работает")
                btnStart.setText("Остановить")
                isRunning = true
            } else {
                stopService(Intent(this, LocationService::class.java))
                tvStatus.setText("Статус: Остановлен")
                btnStart.setText("Запустить")
                isRunning = false
            }
        }

        checkPermissions()
    }

    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE
        )
        var needRequest = false
        for (p in permissions) {
            if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                needRequest = true
            }
        }
        if (needRequest) {
            ActivityCompat.requestPermissions(this, permissions, 100)
        }
    }

    override fun onResume() {
        super.onResume()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, IntentFilter("LocationUpdates"), RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(receiver, IntentFilter("LocationUpdates"))
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }
}