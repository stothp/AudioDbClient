package com.example.audiodbclient.screens.artistlist

import android.util.Log
import androidx.core.text.htmlEncode
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.audiodbclient.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ArtistListModel : ViewModel(){
    val _artists = MutableLiveData<MutableList<Artist>>()
    // This should be converted to List<String> somehow
//    val artists: LiveData<MutableList<Artist>>
//        get() = _artists

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

//            _artists.value?.clear()
            var artistList = mutableListOf<Artist>()

            if (!obj.isNull("artists")) {
                var artistsJSON = obj.getJSONArray("artists")

                for (i in IntRange(0, artistsJSON.length() - 1)) {
                    var a = artistsJSON.getJSONObject(i).getString("strArtist")
                    Log.i("Info", a)
                    artistList.add(Artist(a))
                }
            }

            _artists.postValue(artistList)
        }
    }
}