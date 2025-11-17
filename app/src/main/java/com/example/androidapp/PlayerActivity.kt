package com.example.androidapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import android.Manifest
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.net.Uri
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.content.ContentUris
import android.media.MediaPlayer
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

data class AudioFile(
    val id: Long,
    val uri: Uri,
    val title: String,
    val duration: Long,
    val albumId: Long
)

class PlayerActivity : ComponentActivity() {
    var audioFiles: List<AudioFile> = emptyList()
    val history = mutableListOf<AudioFile>()
    var mediaPlayer : MediaPlayer? = null
    lateinit var name: TextView
    lateinit var oblojka: ImageView
    lateinit var back: Button
    lateinit var pause: Button
    lateinit var next: Button
    lateinit var seekBar: SeekBar
    lateinit var bMenu: Button
    var historyIndex = -1

    val handler = Handler(Looper.getMainLooper())
    val updateSeekBar = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                seekBar.progress = it.currentPosition
                handler.postDelayed(this, 500)
            }
        }
    }

    override fun onRequestPermissionsResult(
        status: Int,
        permissions: Array<String>,
        result: IntArray
    ) {
        super.onRequestPermissionsResult(status, permissions, result)
        if (status == 123 && result.isNotEmpty()
            && result[0] == PackageManager.PERMISSION_GRANTED) {
            audioFiles = getMusicList(this)
            if (mediaPlayer == null) {
                launchTrackList()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        name = findViewById<TextView>(R.id.Nazvanie)
        oblojka = findViewById<ImageView>(R.id.Oblojka)
        back = findViewById<Button>(R.id.bBack)
        pause = findViewById<Button>(R.id.bPause)
        next = findViewById<Button>(R.id.bNext)
        seekBar = findViewById<SeekBar>(R.id.seekBar)
        bMenu = findViewById(R.id.bMenu)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                123
            )
        } else {
            audioFiles = getMusicList(this)
            if (mediaPlayer == null && savedInstanceState == null) {
                launchTrackList()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        next.setOnClickListener {
            Next()
        }

        pause.setOnClickListener {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                    pause.text = "▶"
                } else {
                    it.start()
                    pause.text = "⏸"
                }
            }
        }

        back.setOnClickListener {
            Back()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })

        bMenu.setOnClickListener {
            launchTrackList()
        }
    }

    private fun launchTrackList() {
        if (audioFiles.isEmpty()) {
            Toast.makeText(this, "Музыкальные файлы не найдены", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, TrackListActivity::class.java)
        intent.putExtra("titles", audioFiles.map { it.title }.toTypedArray())
        intent.putExtra("ids", audioFiles.map { it.id }.toLongArray())
        startActivityForResult(intent, 100)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                pause.text = "▶"
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(updateSeekBar)
    }

    fun Next() {
        if (audioFiles.isEmpty()) return

        if (history.isEmpty() || historyIndex == history.size - 1) {
            val Rindex = (audioFiles.indices).random()
            val track = audioFiles[Rindex]
            history.add(track)
            historyIndex = history.lastIndex
            Play(track)
        } else {
            historyIndex += 1
            Play(history[historyIndex])
        }
    }

    fun Back() {
        if (historyIndex > 0) {
            historyIndex -= 1
            Play(history[historyIndex])
        }
    }

    fun Play(track: AudioFile) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(this@PlayerActivity, track.uri)
            prepare()
            start()
            setOnCompletionListener {
                Next()
            }
        }
        pause.text = "⏸"
        seekBar.max = mediaPlayer?.duration ?: 0
        handler.post(updateSeekBar)

        name.text = track.title
        val albumArtUri = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            track.albumId
        )
        oblojka.setImageURI(albumArtUri)
        if (oblojka.drawable == null) {
            oblojka.setImageResource(R.drawable.ic_launcher_background)
        }
    }

    private fun bubbleSort(list: MutableList<AudioFile>) {
        val n = list.size
        for (i in 0 until n - 1) {
            for (j in 0 until n - i - 1) {
                if (list[j].title.compareTo(list[j + 1].title, ignoreCase = true) > 0) {
                    val temp = list[j]
                    list[j] = list[j + 1]
                    list[j + 1] = temp
                }
            }
        }
    }

    fun getMusicList(context: Context): List<AudioFile>{
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val id = MediaStore.Audio.Media._ID
        val title = MediaStore.Audio.Media.TITLE
        val dur = MediaStore.Audio.Media.DURATION
        val albumId = MediaStore.Audio.Media.ALBUM_ID
        val data = arrayOf(id, title, dur, albumId)

        val audio = mutableListOf<AudioFile>()

        val cursor = context.contentResolver.query(uri, data, "${MediaStore.Audio.Media.IS_MUSIC} != 0", null, null)
        cursor?.use {
            while (it.moveToNext()) {
                val audioId = it.getLong(it.getColumnIndexOrThrow(id))
                val audioTitle = it.getString(it.getColumnIndexOrThrow(title)) ?: "Unknown"
                val audioDur = it.getLong(it.getColumnIndexOrThrow(dur))
                val contentUri = ContentUris.withAppendedId(uri, audioId)
                val audioAlbumId = it.getLong(it.getColumnIndexOrThrow(albumId))
                audio.add(AudioFile(audioId, contentUri, audioTitle, audioDur, audioAlbumId))
            }
        }

        val mutableAudio = audio.toMutableList()
        bubbleSort(mutableAudio)

        return mutableAudio
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val trackId = data.getLongExtra("selectedTrackId", -1L)

            if (trackId > 0) {
                val selectedTrack = audioFiles.find { it.id == trackId }

                if (selectedTrack != null) {
                    history.add(selectedTrack)
                    historyIndex = history.lastIndex
                    Play(selectedTrack)
                }
            }
        }
    }
}