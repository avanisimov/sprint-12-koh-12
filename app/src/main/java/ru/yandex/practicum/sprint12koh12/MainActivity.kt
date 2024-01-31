package ru.yandex.practicum.sprint12koh12

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.yandex.practicum.sprint12koh12.databinding.ActivityMainBinding
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var cartItems = emptyList<CartItem>()
    private val cartItemsAdapter = CartItemsAdapter()
    private lateinit var sharedPrefs: SharedPreferences
    private val gson = Gson()

    lateinit var binding :ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPrefs = getSharedPreferences(CART_ITEMS_PREFS_NAME, Context.MODE_PRIVATE)

        binding.itemsRv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = cartItemsAdapter
        }
        cartItemsAdapter.onCartItemPlusClickListener =
            OnCartItemPlusClickListener { cartItem ->
                Log.d("SPRINT_12", "onPlusClick $cartItem")
                increaseCartItemCount(cartItem)
            }
        cartItemsAdapter.onCartItemMinusClickListener =
            OnCartItemMinusClickListener { cartItem ->
                Log.d("SPRINT_12", "onMinusClick $cartItem")
                decreaseCartItemCount(cartItem)
            }

        updateCartItems(getCartItems())
        Log.d("SPRINT_12", "getCartItemsFromServer cartItems=$cartItems")

        binding.clearBtn.setOnClickListener {
            updateCartItems(emptyList())
        }
    }

    override fun onStop() {
        super.onStop()
        saveCartItems(cartItems)
    }


    private fun increaseCartItemCount(cartItem: CartItem) {
        val mutableCartItems = cartItems.toMutableList()
        val index = mutableCartItems.indexOf(cartItem)
        if (index >= 0) {
            val modifiedCartItem = mutableCartItems[index].copy(count = cartItem.count + 1)
            mutableCartItems[index] = modifiedCartItem
            updateCartItems(mutableCartItems)
        }
    }

    private fun decreaseCartItemCount(cartItem: CartItem) {
        val mutableCartItems = cartItems.toMutableList()
        val index = mutableCartItems.indexOf(cartItem)
        if (index >= 0) {
            val modifiedCartItem = mutableCartItems[index].copy(count = cartItem.count - 1)
            if (modifiedCartItem.count == 0) {
                mutableCartItems.removeAt(index)
            } else {
                mutableCartItems[index] = modifiedCartItem
            }
            updateCartItems(mutableCartItems)
        }
    }

    fun updateCartItems(newCartItems: List<CartItem>) {
        cartItems = newCartItems
        cartItemsAdapter.updateItems(cartItems)
        val sum = getCartItemsSum()
        val numberFormat = NumberFormat.getCurrencyInstance()
        binding.sumBtn.text = numberFormat.format(sum)
        saveCartItems(cartItems)
    }

    private fun getCartItemsSum(): BigDecimal {
        return cartItems.map { cartItem ->
            cartItem.product.price.multiply(BigDecimal(cartItem.count))
        }.sumOf { it }
    }

    private fun getCartItems(): List<CartItem> {
        val localCartItems = getLocalCartItems()
        return if (localCartItems.isNullOrEmpty()) {
            val cartItemsFromServer = getCartItemsFromServer()
            saveCartItems(cartItemsFromServer)
            cartItemsFromServer
        } else {
            localCartItems
        }
    }

    private fun saveCartItems(cartItems: List<CartItem>) {
        val json: String = gson.toJson(cartItems)
//        var startTime = System.nanoTime()
        sharedPrefs
            .edit()
            .putString(KEY_CART_ITEMS, json)
            .apply()
//        Log.d("SPRINT_12", "save apply ${System.nanoTime() - startTime}")
//        startTime = System.nanoTime()
//        sharedPrefs
//            .edit()
//            .putString(KEY_CART_ITEMS, json)
//            .commit()
//        Log.d("SPRINT_12", "save commit ${System.nanoTime() - startTime}")
    }

    private fun getLocalCartItems(): List<CartItem>? {
//        sharedPrefs.getBoolean("key_is_account_exists", false)
        val json: String? = sharedPrefs.getString(KEY_CART_ITEMS, null)
        return if (json != null) {
            val list: List<CartItem> =
                gson.fromJson(json, object : TypeToken<List<CartItem>>() {}.type)
            Log.d("SPRINT_12", "fromJson ${list.javaClass}")
            list
        } else {
            null
        }
    }

    private fun getCartItemsFromServer(): List<CartItem> {
        return (1..5).map {
            CartItem(
                product = Product(
                    name = "Товар $it",
                    price = BigDecimal(Random.nextDouble(0.0, 5.0))
                        .setScale(2, RoundingMode.FLOOR),
                ),
                count = 1
            )
        }
    }

    companion object {
        const val CART_ITEMS_PREFS_NAME = "cart_items_storage"
        const val KEY_CART_ITEMS = "cart_items_value"
    }
}