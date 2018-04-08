package com.zhangls.auto.type

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.zhangls.auto.R
import com.zhangls.auto.model.LocationModel

import me.drakeet.multitype.ItemViewBinder
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author zhangls
 */
class LocationViewBinder : ItemViewBinder<LocationModel, LocationViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_location, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, location: LocationModel) {
        val context = holder.itemView.context

        holder.latitude.text = "纬度：${location.latitude}"
        holder.longitude.text = "经度：${location.longitude}"
        holder.accuracy.text = "精度：${location.accuracy}"
        holder.altitude.text = "海拔：${location.altitude}"

        holder.upload.text = if (location.uploaded) {
            holder.upload.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            "已上传"
        } else {
            holder.upload.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            "未上传"
        }

        val date = Date(location.createTime)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        holder.time.text = format.format(date)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val latitude = itemView.find<TextView>(R.id.tvLatitude)
        val longitude = itemView.find<TextView>(R.id.tvLongitude)
        val accuracy = itemView.find<TextView>(R.id.tvAccuracy)
        val altitude = itemView.find<TextView>(R.id.tvAltitude)
        val upload = itemView.find<TextView>(R.id.tvUpload)
        val time = itemView.find<TextView>(R.id.tvTime)
    }
}
