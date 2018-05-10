package com.adehehe.hqcommon.controls

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.klfront.baseui.R

/**
 */
class LoadingEmptyView : LinearLayout
{
    private var mTextView: TextView? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    {
        LayoutInflater.from(context).inflate(R.layout.loading_empty_view, this, true)
        mTextView = findViewById(R.id.tv_loading)
    }


    var loadingStatus: LoadingStatus = LoadingStatus.Loading
        set(value)
        {
            field = value
            when (value)
            {
                LoadingStatus.Loading -> mTextView!!.text = LoadingContent
                LoadingStatus.Error -> mTextView!!.text = ErrorContent
                LoadingStatus.Empty -> mTextView!!.text = EmptyContent
            }
        }
    var LoadingContent: String = context.getString(R.string.loading)
    var ErrorContent: String = context.getString(R.string.load_failed)
    var EmptyContent: String = context.getString(R.string.nodata)


    enum class LoadingStatus
    {
        Loading, Error, Empty
    }

}