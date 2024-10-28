package com.unh.washbuddy_android.ui.orders

data class OrdersData(
    var id: String ="",
    var address: String = "",
    var pickupTime: String = "",
    var selectLaundromat: String = "",
    var detergentType: String = "",
    var speed: String = "",
    var smallbag: String = "",
    var regularbag: String = "",
    var largebag: String = "",
    var extras: String = ""
)

val ordersList: ArrayList<OrdersData> = arrayListOf()
