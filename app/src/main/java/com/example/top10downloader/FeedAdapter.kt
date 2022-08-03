package com.example.top10downloader

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ViewHolder(view: View) {
    var tvName: TextView = view.findViewById(R.id.tvName)
    var tvArtis: TextView = view.findViewById(R.id.tvArtis)
    var tvSummary: TextView = view.findViewById(R.id.tvSummary)
}

class FeedAdapter(context: Context, var resource: Int, var application: List<FeedEntry>) :
    ArrayAdapter<FeedEntry>(context, resource) {
    private val TAG = "FreeAdapter"
    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int = application.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        inflate view every time getView() called
        val viewHolder: ViewHolder
        val view: View
        if (convertView != null) {
            Log.d(TAG, "getView:  called with a OLD contentView")
            view = convertView
//            tag: return stored view object
            viewHolder = view.tag as ViewHolder

        } else {
            Log.d(TAG, "getView: called with a NULL contentView")
            view = inflater.inflate(resource, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }

        val currentApp = application[position]
        viewHolder.tvName.text = currentApp.name
        viewHolder.tvArtis.text = currentApp.artist
        viewHolder.tvSummary.text = currentApp.summary
        Log.d(TAG, "getView: called -> ${application.size}")
        return view
    }
}