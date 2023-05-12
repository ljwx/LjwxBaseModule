package com.ljwx.baseview.toolbar

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar

/**
 * @author ljwx
 * @since 2023/5/9
 * @desc activity通用toolbar
 */
open class CommonToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : Toolbar(context, attrs, defStyleAttr) {

    init {
        // 标题和返回按钮的间距
        contentInsetStartWithNavigation = 0
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // android:minHeight=""要设置成跟toolbar一样高,不然可能会没有垂直居中
        minimumHeight = measuredHeight
    }

}