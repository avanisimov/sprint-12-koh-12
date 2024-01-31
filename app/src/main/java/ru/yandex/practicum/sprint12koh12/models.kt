package ru.yandex.practicum.sprint12koh12

import java.math.BigDecimal
import java.util.UUID

data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val price: BigDecimal,
//    val imageUrl: String, TODO
)

data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val product: Product,
    val count: Int,
)