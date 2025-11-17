package com.example.androidapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.ComponentActivity

class TrackListActivity : ComponentActivity() {

    private lateinit var trackListView: ListView
    private var trackIds: LongArray = longArrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_list)

        trackListView = findViewById(R.id.trackListView)

        val titles = intent.getStringArrayExtra("titles") ?: arrayOf()
        trackIds = intent.getLongArrayExtra("ids") ?: longArrayOf()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, titles)
        trackListView.adapter = adapter

        trackListView.setOnItemClickListener { parent, view, position, id ->
            if (position < trackIds.size) {
                val selectedTrackId = trackIds[position]
                val resultIntent = Intent()
                resultIntent.putExtra("selectedTrackId", selectedTrackId)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}