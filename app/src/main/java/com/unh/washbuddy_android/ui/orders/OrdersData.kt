package com.unh.washbuddy_android.ui.orders

data class OrdersData(
    var address: String = "",
    var pickupTime: String = "",
    var selectLaundromat: String = "",
    var detergentType: String = "",
    var speed: String = "",
    var smallbag: Int = 0,
    var regularbag: Int = 0,
    var largebag: Int = 0,
    var extras: String = ""
)

val ordersList: ArrayList<OrdersData> = arrayListOf()
