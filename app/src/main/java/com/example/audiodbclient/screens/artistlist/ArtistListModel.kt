package com.example.audiodbclient.screens.artistlist

import android.util.Log
import androidx.core.text.htmlEncode
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ArtistListModel : ViewModel(){
    private val _artists = MutableLiveData<MutableList<String>>()
    // This should be converted to List<String> somehow
    val artists: LiveData<MutableList<String>>
        get() = _artists

    init {

    }

    override fun onCleared() {
        super.onCleared()
    }

    public suspend fun getTextFromUrl(urlText : String) : String {
        val url = URL(urlText)

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "GET"  // optional default is GET
            val data = inputStream.bufferedReader().use {
                it.readText()
            }

            return data
        }
    }

    fun getArtists(searchString : String){
        GlobalScope.launch(Dispatchers.IO) {
            var jsonString = getTextFromUrl("https://theaudiodb.com/api/v1/json/1/search.php?s=".plus(searchString.replace(" ", "_").plus("%").htmlEncode()))
            val obj = JSONObject(jsonString)

            _artists.value?.clear()

            if (!obj.isNull("artists")) {
                var artists = obj.getJSONArray("artists")

                for (i in IntRange(0, artists.length() - 1)) {
                    var artist = artists.getJSONObject(i).getString("strArtist")
                    Log.i("Info", artist)
                    _artists.value?.add(artist)
                }
            }
        }
    }
}