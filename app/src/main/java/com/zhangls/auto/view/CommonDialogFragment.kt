package com.zhangls.auto.view

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialogFragment


/**
 * 通用对话框类，用于展示对话框
 *
 * @author zhangls
 */
class CommonDialogFragment : AppCompatDialogFragment() {

    private var onPositiveClick: (() -> Unit)? = null
    private var onNegativeClick: (() -> Unit)? = null


    companion object {
        // 入口方法
        fun newInstance(message: String): CommonDialogFragment {
            val fragment = CommonDialogFragment()
            val args = Bundle()
            args.putString("message", message)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = arguments.getString("message")
        isCancelable = false

        // 返回一个对话框对象
        return AlertDialog.Builder(activity)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { _, _ -> onPositiveClick?.invoke() }
                .setNegativeButton(android.R.string.cancel) { _, _ -> onNegativeClick?.invoke() }
                .create()
    }


    /**
     * 设置确定点击事件监听
     */
    fun setPositiveListener(listener: () -> Unit) {
        onPositiveClick = listener
    }

    /**
     * 设置取消点击事件监听
     */
    fun setNegativeListener(listener: () -> Unit) {
        onNegativeClick = listener
    }
}