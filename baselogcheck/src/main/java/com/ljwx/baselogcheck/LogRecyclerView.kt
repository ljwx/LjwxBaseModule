package com.ljwx.baselogcheck

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ljwx.baselogcheck.display.LogCheckPool
import com.ljwx.baselogcheck.display.FixSizeVector
import com.ljwx.baselogcheck.recycler.BaseLogAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LogRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private var filterCategory = ""
    private var filterTag = ""
    private var intervalSecond = 2

    private var logPool: FixSizeVector<CharSequence>? = null

    init {
        adapter = BaseLogAdapter()
        layoutManager =
            layoutManager ?: LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val a = getContext().obtainStyledAttributes(attrs, R.styleable.LogRecyclerView)
        try {
            filterCategory = a.getString(R.styleable.LogRecyclerView_logrv_filter_category) ?: ""
            filterTag = a.getString(R.styleable.LogRecyclerView_logrv_filter_tag) ?: ""
            intervalSecond = a.getInt(R.styleable.LogRecyclerView_logrv_interval_second, 2)
        } finally {
            a.recycle()
        }
    }

    open fun run(lifecycleScope: CoroutineScope) {
        if (!filterCategory.isNullOrEmpty()) {
            logPool = LogCheckPool.getLogPool(filterCategory)
            lifecycleScope.launch(Dispatchers.IO) {
                while (true) {
                    delay(intervalSecond * 1000L)
                    val filterResult =
                        if (!filterTag.isNullOrEmpty()) logPool?.filter { it.contains(filterTag) } else logPool
                    withContext(Dispatchers.Main) {
                        (adapter as? BaseLogAdapter)?.submitData(filterResult)
                    }
                }
            }
        }
    }

    fun addData(list: List<Any>?) {
        (adapter as? BaseLogAdapter)?.addData(list)
    }

}