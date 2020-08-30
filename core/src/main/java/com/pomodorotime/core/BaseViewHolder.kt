package com.pomodorotime.core

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<in M>(private val binding: ViewBinding) :
    RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(data: M)
}