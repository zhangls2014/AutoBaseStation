package com.zhangls.auto.type

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.zhangls.auto.GlideApp
import com.zhangls.auto.OnItemClickListener

import com.zhangls.auto.R
import com.zhangls.auto.model.PictureModel

import me.drakeet.multitype.ItemViewBinder
import org.jetbrains.anko.find
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author zhangls
 */
class PictureViewBinder(listener: OnItemClickListener) : ItemViewBinder<PictureModel, PictureViewBinder.ViewHolder>() {

    private val clickListener = listener

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val root = inflater.inflate(R.layout.item_picture, parent, false)
        return ViewHolder(root)
    }

    override fun onBindViewHolder(holder: ViewHolder, pictureModel: PictureModel) {
        val context = holder.itemView.context
        GlideApp.with(context)
                .load(pictureModel.path)
                .error(R.drawable.ic_broken_image_gray_24dp)
                .placeholder(R.drawable.ic_image_gray_24dp)
                .centerCrop()
                .into(holder.picture)

        holder.upload.text = if (pictureModel.uploaded) {
            holder.upload.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            "已上传"
        } else {
            holder.upload.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            "未上传"
        }

        val date = Date(pictureModel.createTime)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        holder.time.text = format.format(date)

        holder.delete.setOnClickListener {
            clickListener.onItemClick(holder.delete, holder.adapterPosition)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val picture = itemView.find<ImageView>(R.id.picture)
        val time = itemView.find<TextView>(R.id.tvTime)
        val upload = itemView.find<TextView>(R.id.tvUpload)
        val delete = itemView.find<ImageButton>(R.id.ibDelete)
    }
}
