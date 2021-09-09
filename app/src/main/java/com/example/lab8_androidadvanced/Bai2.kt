package com.example.lab8_androidadvanced

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lab8_androidadvanced.databinding.ActivityBai2Binding
import java.io.File
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class Bai2 : AppCompatActivity() {
    private lateinit var binding: ActivityBai2Binding

    companion object {
        lateinit var mediaPlayer: MediaPlayer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBai2Binding.inflate(layoutInflater)
        setContentView(binding.root)



        requestPermission()
        setup()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setup()
        } else {
            Toast.makeText(
                this,
                "We need permissions to access songs on your device",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constant.READ_EXTERNAL_STORAGE
            )
        }
    }

    private fun setup() {
        initVariables()
        displayAllSongs()
        onListViewClick()
        onButtonClick()
    }

    private fun initVariables() {
        mediaPlayer = MediaPlayer()
        binding.sbProgress.isEnabled = false
    }

    private fun onListViewClick() {
        val songs = getAllSongsOnDevice(Environment.getExternalStorageDirectory())
        binding.lvSongList.setOnItemClickListener { _, _, index, _ ->
            val currentSong = songs[index]

            // 0: Song name, 1, start time, 2, final time

            mediaPlayer.stop()

            startMusic(Uri.parse(currentSong.toString()))

            val properties = getSongProperties(currentSong, mediaPlayer)
            val songName = properties[0]
            val startTime = properties[1]
            val finalTime = properties[2]
            val finalTimeInt = properties[3]


            binding.sbProgress.max = mediaPlayer.duration


            setSongProperties(songName, startTime, finalTime, finalTimeInt.toInt())

            updateSongProperties(currentSong)
        }
    }

    private fun startMusic(uri: Uri) {
        mediaPlayer = MediaPlayer.create(this, uri)
        mediaPlayer.start()
    }

    private fun setSongProperties(
        name: String,
        startTime: String,
        finalTime: String,
        startTimeInt: Int
    ) {
        binding.txtSongName.text = name
        binding.txtSongTime.text = finalTime
        binding.txtTimePlayed.text = startTime
        binding.sbProgress.progress = startTimeInt
    }

    private fun getAllSongsOnDevice(file: File): ArrayList<File> {
        val result = ArrayList<File>()
        val files = file.listFiles()
        files?.forEach { singleFile ->
            if (singleFile.isDirectory && !singleFile.isHidden) {
                result.addAll(getAllSongsOnDevice(singleFile))
            } else {
                if (singleFile.name.endsWith(".mp3")) {
                    result.add(singleFile)
                }
            }
        }
        return result
    }

    private fun displayAllSongs() {
        val mySongs = getAllSongsOnDevice(Environment.getExternalStorageDirectory())
        val songNameList = ArrayList<String>()

        for (singleSong in mySongs) {
            songNameList.add(singleSong.name.toString())
        }

        val arrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, songNameList)
        binding.lvSongList.adapter = arrayAdapter

    }

    private fun getSongProperties(file: File, mediaPlayer: MediaPlayer): ArrayList<String> {
        val result = ArrayList<String>()
        val fileName = file.name
        val startTime = mediaPlayer.currentPosition
        val finalTime = mediaPlayer.duration

        // Add file name
        result.add(fileName)

        // Start time
        result.add(
            String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong())
                )
            )
        )

        // Final time
        result.add(
            String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong())
                )
            )
        )

        // start time in Int type
        result.add(startTime.toString())

        return result
    }


    private fun updateSongProperties(currentSong: File) {
        val properties = getSongProperties(currentSong, mediaPlayer)

        val songName = properties[0]
        val startTime = properties[1]
        val finalTime = properties[2]


        setSongProperties(
            songName,
            startTime,
            finalTime,
            getSongProperties(currentSong, mediaPlayer)[3].toInt()
        )

        val thread = Thread() {
            runOnUiThread {
                updateSongProperties(currentSong)
                sleep(3000)
            }
        }

        thread.start()




    }

    private fun onButtonClick() {
        onPlayClick()
        onPauseClick()
        onStopClick()
    }

    private fun onPlayClick() {
        binding.btnStartMusic.setOnClickListener {
            mediaPlayer.start()
        }
    }

    private fun onPauseClick() {
        binding.btnPauseMusic.setOnClickListener {
            mediaPlayer.pause()
        }
    }

    private fun onStopClick() {
        binding.btnStopMusic.setOnClickListener {
            mediaPlayer.stop()
        }
    }


}


