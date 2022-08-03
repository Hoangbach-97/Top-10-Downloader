package com.example.top10downloader

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import java.net.URL
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private val TAG = "menu"

    //Remember: lateinit: Allow declare global variable without initialized, lazy: Only initialized when the first using
    private val xmlListView by lazy { findViewById<ListView>(R.id.xmlListView) }
    private var downloadData:DownloadData? =null
    private  var feedUrl:String = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private  var feedLimit = 10

    private var feedCacheUrl = "INVALIDATED"
    private val STATE_URL = "feedUrl"
    private val STATE_LIMIT = "feedLimit"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("TAG", "onCreate is CALLED")
        if (savedInstanceState != null ){
            feedUrl = savedInstanceState.getString(STATE_URL).toString()
            feedLimit = savedInstanceState.getInt(STATE_LIMIT)
        }
        downloadUrl(feedUrl.format(feedLimit))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_URL, feedUrl)
        outState.putInt(STATE_LIMIT, feedLimit)
    }
//Cause: downloadUrl called in onCreate -> but onRestoreInstanceState after onCreate -> so get value in onCreate()
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//
//    }

    private  fun  downloadUrl(feedUrl:String){
        if (feedUrl != feedCacheUrl){
//           enhance performance with the same URL
            Log.d(TAG, "downloadUrl starting AsyncTask")
            downloadData = DownloadData(this, xmlListView)
            downloadData?.execute(feedUrl)
            feedCacheUrl = feedUrl //reassign to  current URL
            Log.d(TAG, "downloadUrl DONE")
        } else {
            Log.d(TAG, "downloadUrl: downloadUrl - URL not changed")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_free_apps
            -> {
                feedUrl =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topMovies/xml"
                Log.d(TAG, "onOptionsItemSelected:menu_free_apps called")
            }
            R.id.menu_paid_apps
            -> {
                feedUrl =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
                Log.d(TAG, "onOptionsItemSelected:menu_paid_apps called")
            }
            R.id.menu_songs
            -> {
                feedUrl =
                    "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
                Log.d(TAG, "onOptionsItemSelected:menu_songs called")
            }
            R.id.top_10, R.id.top_25 -> {
                if (!item.isChecked){
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} and limited: $feedLimit")
                } else {
                    Log.d(TAG, "onOptionsItemSelected: ${item.title} and limited unchanged")
                }
            }
            R.id.menu_refresh -> feedCacheUrl = "INVALIDATED"
            else -> {
                Log.d(TAG, "onOptionsItemSelected:no item called")
                return super.onOptionsItemSelected(item)
            }
        }
        downloadUrl(feedUrl.format(feedLimit))
        return true
    }
    //    called when inflated the activities menu: create the menu object from XML
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)
        if (feedLimit ==10){
            menu?.findItem(R.id.top_10)?.isChecked = true
        } else {
            menu?.findItem(R.id.top_25)?.isChecked = true
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
    }

    //    ************************Start AsyncTask(not supported any more) -> CoRoutines
    companion object {
        //        params-progress-result
        private class DownloadData(context: Context, listView: ListView) :
            AsyncTask<String, Void, String>() {
            var propContext: Context by Delegates.notNull()
            var propListView: ListView by Delegates.notNull()

            init {
                propContext = context
                propListView = listView
            }

            //    get result from doInBackGround method
            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                val parseApplication = ParseApplication()
                parseApplication.parse(result)

                val feedAdapter =
                    FeedAdapter(propContext, R.layout.list_record, parseApplication.application)
                propListView.adapter = feedAdapter
            }

            //task in background
            override fun doInBackground(vararg params: String?): String {
                Log.d("TAG", "doInBackground: CALLED")
                val rss = downloadXML(params[0])
                if (rss.isEmpty()) {
                    Log.e("TAG", "doInBackground: ERROR")
                }
                return rss
            }

            private fun downloadXML(params: String?): String {
                return URL(params).readText()
            }
        }
    }
//    **************************END: AsyncTask
}
