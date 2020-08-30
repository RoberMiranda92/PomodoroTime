package com.pomodorotime.core

import android.util.SparseBooleanArray
import androidx.core.util.isNotEmpty
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

abstract class BaseMultiSelectorAdapter<M : ListItem, VH : BaseViewHolder<M>>(
    private val onItemSelector: OnItemClick<M>,
    callback: DiffUtil.ItemCallback<M>
) : ListAdapter<M, VH>(callback) {

    private val selectedItems: SparseBooleanArray = SparseBooleanArray()

    override fun onBindViewHolder(holder: VH, position: Int) {

        if (isViewHolderSelectable(holder.itemViewType)) {
            holder.itemView.setOnLongClickListener {
                if (!hasSelectedItems()) {
                    toogleItemSelection(position)
                    onItemSelector.onLongPress(selectedItems.size())
                    true
                } else {
                    false
                }
            }
            holder.itemView.setOnClickListener {
                if (hasSelectedItems()) {
                    toogleItemSelection(position)
                    onItemSelector.onItemSelectedClick(selectedItems.size())
                } else {
                    onItemSelector.onItemClick(getItem(position))
                }
            }
        }

        holder.bind(getItem(position))
    }

    fun setDataWithSection(counterList: MutableList<M>, selectedItems: SparseBooleanArray) {
        this.selectedItems.clear()
        for (i in 0 until selectedItems.size()) {
            this.selectedItems.put(selectedItems.keyAt(i), selectedItems.valueAt(i))
        }
        super.submitList(counterList)
    }

    fun getSelectedItems(): List<M> {
        val items: MutableList<M> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(currentList[selectedItems.keyAt(i)])
        }
        return items
    }

    fun getSelectedSparseArray(): SparseBooleanArray = selectedItems

    private fun toogleItemSelection(position: Int) {
        if (selectedItems[position, false]) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position, true)
        }
        notifyItemChanged(position)
    }

    private fun hasSelectedItems(): Boolean {
        return selectedItems.isNotEmpty()
    }

    fun clearSelection() {
        if (hasSelectedItems()) {
            selectedItems.clear()
            notifyDataSetChanged()
        }
    }

    protected fun isItemSelected(position: Int): Boolean =
        selectedItems.get(position, false)

    override fun getItemViewType(position: Int): Int = getItem(position).getType()

    protected abstract fun isViewHolderSelectable(itemViewType: Int): Boolean

    interface OnItemClick<M> {
        fun onItemSelectedClick(selectedSize: Int)
        fun onLongPress(selectedSize: Int)
        fun onItemClick(element: M)
    }
}