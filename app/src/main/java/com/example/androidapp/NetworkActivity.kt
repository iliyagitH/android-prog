package com.example.androidapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ

class NetworkActivity : AppCompatActivity() {
    private val LOG_TAG = "ZMQ_LOG"

    private lateinit var tvStatus: TextView
    private lateinit var btnSend: Button
    private lateinit var btnBack: Button
    private val mainHandler = Handler(Looper.getMainLooper())
    private val serverIp = "192.168.0.2"
    private val port = "2222"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sockets)

        tvStatus = findViewById(R.id.tv_network_status)
        btnSend = findViewById(R.id.btn_send_zmq)
        btnBack = findViewById(R.id.btn_back_to_menu)

        btnBack.setOnClickListener {
            finish()
        }

        btnSend.setOnClickListener {
            tvStatus.text = "Отправка..."
            Thread {
                try {
                    sendZmqData()
                } catch (e: Exception) {
                    Log.e(LOG_TAG, "Ошибка: ${e.message}")
                    updateUi("Ошибка: ${e.message}")
                }
            }.start()
        }
    }

    private fun sendZmqData() {
        ZContext().use { context ->
            val socket = context.createSocket(SocketType.REQ)
            socket.receiveTimeOut = 5000

            val address = "tcp://$serverIp:$port"
            socket.connect(address)

            val message = "Hello from Android!"
            socket.send(message.toByteArray(ZMQ.CHARSET), 0)
            Log.d(LOG_TAG, "Данные отправлены")

            val reply = socket.recv(0)

            if (reply != null) {
                val response = String(reply, ZMQ.CHARSET)
                updateUi("Сервер ответил: $response")
            } else {
                updateUi("Ошибка: Сервер не ответил (Таймаут)")
            }
        }
    }


    private fun updateUi(text: String) {
        mainHandler.post {
            tvStatus.text = text
        }
    }
}