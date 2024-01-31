package ru.yandex.practicum.sprint12koh12

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.yandex.practicum.sprint12koh12.databinding.VCartItemBinding
import java.text.NumberFormat

class CartItemViewHolder(
    parent: ViewGroup,
    private val binding: VCartItemBinding = VCartItemBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
    )
) : RecyclerView.ViewHolder(
    binding.root
) {

    var onCartItemMinusClickListener: OnCartItemMinusClickListener? = null
    var onCartItemPlusClickListener: OnCartItemPlusClickListener? = null

    private val priceFormatter = NumberFormat.getCurrencyInstance()

    fun bind(cartItem: CartItem) {
        binding.title.text = cartItem.product.name
        binding.price.text = priceFormatter.format(cartItem.product.price)
        binding.count.text = cartItem.count.toString()

        binding.minus.setOnClickListener {
            onCartItemMinusClickListener?.onMinusClick(cartItem)
        }
        binding.plus.setOnClickListener {
            onCartItemPlusClickListener?.onPlusClick(cartItem)
        }
    }
}

fun interface OnCartItemMinusClickListener {
    fun onMinusClick(cartItem: CartItem)
}

fun interface OnCartItemPlusClickListener {
    fun onPlusClick(cartItem: CartItem)
}