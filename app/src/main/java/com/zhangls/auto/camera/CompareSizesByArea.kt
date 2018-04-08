package com.zhangls.auto.camera

import android.util.Size
import java.lang.Long.signum

/**
 *
 *
 * @author zhangls
 */
internal class CompareSizesByArea : Comparator<Size> {

    // We cast here to ensure the multiplications won't overflow
    override fun compare(lhs: Size, rhs: Size) =
            signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)

}