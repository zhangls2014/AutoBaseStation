package com.zhangls.auto

import android.view.View

/**
 * RecyclerView Item 点击事件监听接口
 *
 * @author zhangls
 */
interface OnItemClickListener {

    /**
     * item 点击事件响应
     *
     * @param view 被点击 View
     * @param position 所在位置
     */
    fun onItemClick(view: View, position: Int)
}