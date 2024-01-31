package ru.yandex.practicum.sprint12koh12

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DiffUtil.DiffResult
import androidx.recyclerview.widget.RecyclerView

class CartItemsAdapter : RecyclerView.Adapter<CartItemViewHolder>() {

    private var items: List<CartItem> = emptyList()

    var onCartItemMinusClickListener: OnCartItemMinusClickListener? = null
    var onCartItemPlusClickListener: OnCartItemPlusClickListener? = null

    fun updateItems(newItems: List<CartItem>) {
        val oldItems = items
        val diffResult: DiffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldItems.size
            }

            override fun getNewListSize(): Int {
                return newItems.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].id == newItems[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }

        })
        items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        return CartItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bind(items[position])
        holder.onCartItemPlusClickListener = onCartItemPlusClickListener
        holder.onCartItemMinusClickListener = onCartItemMinusClickListener
    }
}