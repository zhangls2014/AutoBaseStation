package com.zhangls.auto.type

import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.ViewGroup

import com.zhangls.auto.ListViewHolder
import com.zhangls.auto.R
import com.zhangls.auto.model.CellInfoLteModel
import com.zhangls.auto.view.CellInfoDialog

import me.drakeet.multitype.ItemViewBinder
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author zhangls
 */
class LteViewBinder(manager: FragmentManager) : ItemViewBinder<CellInfoLteModel, ListViewHolder>() {

    private val fragmentManager = manager


    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ListViewHolder {
        val root = inflater.inflate(R.layout.item_list, parent, false)
        return ListViewHolder(root)
    }

    override fun onBindViewHolder(holder: ListViewHolder, lte: CellInfoLteModel) {
        val context = holder.itemView.context

        val operator = when (lte.lteMnc) {
            "0", "00", "02", "07" -> "中国移动"
            "01", "06" -> "中国联通"
            "03", "05", "11" -> "中国电信"
            "20" -> "中国铁通"
            else -> "未知"
        }
        holder.title.text = operator + "(LTE)"

        val status = if (lte.uploaded) {
            holder.upload.setTextColor(ContextCompat.getColor(context, android.R.color.black))
            "已上传"
        } else {
            holder.upload.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            "未上传"
        }
        holder.upload.text = status

        val date = Date(lte.createTime)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        format.format(date)

        holder.time.text = format.format(date)

        holder.container.setOnClickListener {
            val arrays = arrayOf(
                    operator,
                    status,
                    format.format(date),
                    lte.lteMnc,
                    lte.lteCi,
                    lte.ltePci,
                    lte.lteTac,
                    lte.lteAsulevel,
                    lte.lteRsrp,
                    lte.lteRsrq,
                    lte.lteEarfcn
            )
            CellInfoDialog.newInstance(CellInfoDialog.ACTION_LTE, arrays)
                    .show(fragmentManager, null)
        }
    }
}
