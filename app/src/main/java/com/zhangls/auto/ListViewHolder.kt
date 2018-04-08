package com.zhangls.auto

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import org.jetbrains.anko.find

/**
 * ViewHolder 子类
 *
 * @author zhangls
 */
class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val title: TextView = itemView.find(R.id.tvOperator)
    val upload: TextView = itemView.find(R.id.tvUpload)
    val time: TextView = itemView.find(R.id.tvTime)
    val container: ConstraintLayout = itemView.find(R.id.clCellItem)
}