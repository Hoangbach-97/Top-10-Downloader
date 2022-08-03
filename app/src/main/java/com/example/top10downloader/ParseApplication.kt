package com.example.top10downloader

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.lang.Exception

class ParseApplication {
    private val TAG = "RSS_XML"
 val application = ArrayList<FeedEntry>()

    fun parse(xmlData:String):Boolean{
        Log.d(TAG, "parse: $xmlData")
        var status = true
        var inEntry = false
        var textValue = ""

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware =  true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = xpp.name?.lowercase()
                when(eventType){
                    XmlPullParser.START_TAG -> {
                        Log.d(TAG, "parse:  + $tagName")
                        if (tagName == "entry"){
                            inEntry =  true
                        }
                    }
                    XmlPullParser.TEXT ->textValue =  xpp.text
                     XmlPullParser.END_TAG ->{
                         Log.d(TAG, "parse: + $tagName")
                         if (inEntry){
                             when(tagName){
                                 "entry"->{
                                     application.add(currentRecord)
                                     inEntry = false
                                     currentRecord = FeedEntry()
                                 }
                                 "name"->currentRecord.name = textValue
                                 "artist"->currentRecord.artist = textValue
                                 "releaseDate"->currentRecord.releaseDate=textValue
                                 "summary"->currentRecord.summary =textValue
                                 "image"->currentRecord.imgUrl = textValue
                             }
                         }
                     }
                }
                eventType = xpp.next()
            }
        } catch (e:Exception){
            e.printStackTrace()
            status = false
        }
        return status
    }

}