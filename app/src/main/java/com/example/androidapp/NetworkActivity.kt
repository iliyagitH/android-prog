package com.example.androidapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ

class NetworkActivity : AppCompatActivity() {

    private lateinit var etIp: EditText
    private lateinit var tvStatus: TextView
    private lateinit var btnSend: Button
    private lateinit var btnBack: Button

    val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sockets)

        etIp = findViewById(R.id.et_server_ip) as EditText
        tvStatus = findViewById(R.id.tv_network_status) as TextView
        btnSend = findViewById(R.id.btn_send_zmq) as Button
        btnBack = findViewById(R.id.btn_back_to_menu) as Button

        btnSend.setOnClickListener {
            val ip = etIp.text.toString().trim()
            if (ip.isEmpty()) {
                tvStatus.setText("Введите IP адрес")
                return@setOnClickListener
            }
            tvStatus.setText("Отправка...")
            Thread {
                sendData(ip)
            }.start()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun sendData(ip: String) {
        val file = java.io.File(getExternalFilesDir(null), "location_log.json")

        if (!file.exists()) {
            handler.post {
                tvStatus.setText("Файл не найден")
            }
            return
        }

        val lines = file.readLines()
        val payload = "[" + lines.filter { it.isNotBlank() }.joinToString(",") + "]"

        val context = ZContext()
        val socket = context.createSocket(SocketType.PUSH)
        socket.linger = 1000

        try {
            socket.connect("tcp://$ip:2222")
            socket.send(payload.toByteArray(ZMQ.CHARSET), 0)
            handler.post {
                tvStatus.setText("Отправлено: ${lines.size} записей")
            }
        } catch (e: Exception) {
            handler.post {
                tvStatus.setText("Ошибка: ${e.message}")
            }
        }

        socket.close()
        context.close()
    }
}
