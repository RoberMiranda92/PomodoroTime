package com.pomodorotime.task.taskList

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes

class ActionModeCallback private constructor() : ActionMode.Callback {

    @MenuRes
    private var menuResId: Int = 0
    private var title: String? = null
    private var subtitle: String? = null
    private var mode: ActionMode? = null
    private var view: View? = null
    private var onClickItemActionMode: ((menuItem: MenuItem) -> Unit)? = null
    private var onShowActionMode: (() -> Unit)? = null
    private var onFinisActionMode: (() -> Unit)? = null

    constructor(builder: Builder) : this() {
        this.menuResId = builder.menuResId
        this.title = builder.title
        this.subtitle = builder.subtitle
        this.view = builder.view
        this.onClickItemActionMode = builder.onClickItemActionMode
        this.onShowActionMode = builder.onShowActionMode
        this.onFinisActionMode = builder.onFinisActionMode
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        this.mode = mode
        mode.menuInflater.inflate(menuResId, menu)
        mode.title = title
        mode.subtitle = subtitle
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        onShowActionMode?.invoke()
        return false
    }
    override fun onDestroyActionMode(mode: ActionMode) {
        onFinisActionMode?.invoke()
        this.mode = null
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        onClickItemActionMode?.invoke(item)
        return true
    }
    fun startActionMode() =
        view?.startActionMode(this)

    class Builder {

        @MenuRes
        internal var menuResId: Int = 0
        internal var title: String? = null
        internal var subtitle: String? = null
        internal var view: View? = null
        internal var onClickItemActionMode: ((menuItem: MenuItem) -> Unit)? = null
        internal var onShowActionMode: (() -> Unit)? = null
        internal var onFinisActionMode: (() -> Unit)? = null

        fun setView(view: View) = this.apply { this.view = view }

        fun setTitle(title: String) = this.apply { this.title = title }

        fun setSubtitle(subtitle: String) = this.apply { this.subtitle = subtitle }

        fun setMenu(@MenuRes menu: Int) = this.apply { this.menuResId = menu }

        fun setOnShowActionMode(onShowActionMode: () -> Unit) =
            this.apply { this.onShowActionMode = onShowActionMode }

        fun setOnFinisActionMode(onFinisActionMode: () -> Unit) =
            this.apply { this.onFinisActionMode = onFinisActionMode }

        fun setOnItemClick(onClickItemActionMode: (menuItem: MenuItem) -> Unit) =
            this.apply { this.onClickItemActionMode = onClickItemActionMode }


        fun build(): ActionModeCallback =
            ActionModeCallback(this)
    }
}
