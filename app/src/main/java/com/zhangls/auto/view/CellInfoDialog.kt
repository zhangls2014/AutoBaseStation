package com.zhangls.auto.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatDialogFragment
import android.view.View
import android.widget.TextView
import com.zhangls.auto.R
import org.jetbrains.anko.find

/**
 * CellInfo 信息展示对话框
 *
 * @author zhangls
 */
class CellInfoDialog : AppCompatDialogFragment() {

    private var type = 0
    private lateinit var info: Array<String>


    companion object {
        private const val EXTRA_INFO = "extra_info"
        private const val EXTRA_TYPE = "extra_type"
        const val ACTION_GSM = 1
        const val ACTION_LTE = 2
        const val ACTION_CDMA = 3
        const val ACTION_WCDMA = 4

        fun newInstance(type: Int, arrays: Array<String>): CellInfoDialog {
            val fragment = CellInfoDialog()
            val args = Bundle()
            args.putInt(EXTRA_TYPE, type)
            args.putCharSequenceArray(EXTRA_INFO, arrays)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments.getInt(EXTRA_TYPE)
        info = arguments.getStringArray(EXTRA_INFO)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        val view: View?

        // 设置布局文件，并对文本赋值
        when (type) {
            ACTION_GSM -> {
                view = activity.layoutInflater.inflate(R.layout.dialog_info_gsm, null)
                view.find<TextView>(R.id.text1).text = "运营商：" + info[0] + "(GSM)"
                view.find<TextView>(R.id.text2).text = "状态：" + info[1]
                view.find<TextView>(R.id.text3).text = "测量时间：" + info[2]
                view.find<TextView>(R.id.text4).text = "mnc：" + info[3]
                view.find<TextView>(R.id.text5).text = "cid：" + info[4]
                view.find<TextView>(R.id.text6).text = "lac：" + info[5]
                view.find<TextView>(R.id.text7).text = "rssi：" + info[6]
                view.find<TextView>(R.id.text8).text = "asulevel：" + info[7]
                view.find<TextView>(R.id.text9).text = "arfcn：" + info[8]
                view.find<TextView>(R.id.text10).text = "basic：" + info[9]
            }
            ACTION_LTE -> {
                view = activity.layoutInflater.inflate(R.layout.dialog_info_lte, null)
                view.find<TextView>(R.id.text1).text = "运营商：" + info[0] + "(LTE)"
                view.find<TextView>(R.id.text2).text = "状态：" + info[1]
                view.find<TextView>(R.id.text3).text = "测量时间：" + info[2]
                view.find<TextView>(R.id.text4).text = "mnc：" + info[3]
                view.find<TextView>(R.id.text5).text = "ci：" + info[4]
                view.find<TextView>(R.id.text6).text = "pci：" + info[5]
                view.find<TextView>(R.id.text7).text = "tac：" + info[6]
                view.find<TextView>(R.id.text8).text = "asulevel：" + info[7]
                view.find<TextView>(R.id.text9).text = "rsrp：" + info[8]
                view.find<TextView>(R.id.text10).text = "rsrq：" + info[9]
                view.find<TextView>(R.id.text11).text = "earfcn：" + info[10]
            }
            ACTION_CDMA -> {
                view = activity.layoutInflater.inflate(R.layout.dialog_info_cdma, null)
                view.find<TextView>(R.id.text1).text = "运营商：" + info[0] + "(CDMA)"
                view.find<TextView>(R.id.text2).text = "状态：" + info[1]
                view.find<TextView>(R.id.text3).text = "测量时间：" + info[2]
                view.find<TextView>(R.id.text4).text = "mnc：" + info[3]
                view.find<TextView>(R.id.text5).text = "basestationId：" + info[4]
                view.find<TextView>(R.id.text6).text = "networkId：" + info[5]
                view.find<TextView>(R.id.text7).text = "systemId：" + info[6]
                view.find<TextView>(R.id.text8).text = "rssi：" + info[7]
                view.find<TextView>(R.id.text9).text = "ecio：" + info[8]
                view.find<TextView>(R.id.text10).text = "evdoDbm：" + info[9]
                view.find<TextView>(R.id.text11).text = "evdoEcio：" + info[10]
                view.find<TextView>(R.id.text12).text = "asulevel：" + info[11]
                view.find<TextView>(R.id.text13).text = "latitude：" + info[12]
                view.find<TextView>(R.id.text14).text = "longitude：" + info[13]
            }
            ACTION_WCDMA -> {
                view = activity.layoutInflater.inflate(R.layout.dialog_info_wcdma, null)
                view.find<TextView>(R.id.text1).text = "运营商：" + info[0] + "(WCDMA)"
                view.find<TextView>(R.id.text2).text = "状态：" + info[1]
                view.find<TextView>(R.id.text3).text = "测量时间：" + info[2]
                view.find<TextView>(R.id.text4).text = "mnc：" + info[3]
                view.find<TextView>(R.id.text5).text = "cid：" + info[4]
                view.find<TextView>(R.id.text6).text = "psc：" + info[5]
                view.find<TextView>(R.id.text7).text = "lac：" + info[6]
                view.find<TextView>(R.id.text8).text = "rssi：" + info[7]
                view.find<TextView>(R.id.text9).text = "asulevel：" + info[8]
                view.find<TextView>(R.id.text10).text = "uarfcn：" + info[9]
            }
            else -> {
                view = null
            }
        }

        return if (view == null) {
            AlertDialog.Builder(activity)
                    .setTitle(R.string.titleCellInfoDialog)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialogClose, null)
                    .create()
        } else {
            AlertDialog.Builder(activity)
                    .setTitle(R.string.titleCellInfoDialog)
                    .setView(view)
                    .setCancelable(false)
                    .setPositiveButton(R.string.dialogClose, null)
                    .create()
        }
    }
}