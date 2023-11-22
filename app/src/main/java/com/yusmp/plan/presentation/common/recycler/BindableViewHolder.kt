package com.yusmp.plan.presentation.common.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yusmp.plan.presentation.common.models.Identity

abstract class BindableViewHolder<T : Identity<*>>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var item: T
    fun onBind(item: T) {
        this.item = item
        item.bind()
    }

    protected abstract fun T.bind()
}