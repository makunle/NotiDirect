package com.noest.notidirect.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.app_item_layout.view.*

data class AppInfo(
    val pkg: String,
    val name: String,
    val icon: Drawable,
    var focus: Boolean
)

class FocusAppAdapter(
    context: Context,
    val resource: Int,
    val appList: Array<AppInfo>,
    val listener: View.OnClickListener
) :
    ArrayAdapter<AppInfo>(context, resource, appList) {

    data class ViewHolder(val appIcon: ImageView, val appName: TextView, val appChecked: CheckBox)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resource, null)
            viewHolder = ViewHolder(view.ivAppIcon, view.txvAppName, view.cbAppSelect)
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val info = appList[position]

        viewHolder.appName.text = info.name
        viewHolder.appChecked.isChecked = info.focus
        viewHolder.appIcon.setImageDrawable(info.icon)

        viewHolder.appChecked.setOnClickListener(listener)
        viewHolder.appChecked.tag = position

        return view
    }
}