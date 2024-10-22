package com.unh.washbuddy_android.ui.orders

data class OrdersData(
    var address: String = "",
    var pickupTime: String = "",
    var detergentType: String = "",
    var selectLaundromat: String = "",
    var speed: String = "",
    var bagCount: Int = 0,
    var extras: String = ""
)

val ordersList: ArrayList<OrdersData>
= arrayListOf(
    OrdersData("new haven","5PM","Tide","Topkat","standard",4,"Basic"),
    OrdersData("west haven","6PM","Wash","Laundromax","Express",3,"Premium"),
    OrdersData("hartford","6:30PM","Tide","WashNFold","standard",6,"Basic"),
    OrdersData("new haven","5PM","Tide","Topkat","standard",4,"Basic"),
    OrdersData("west haven","6PM","Wash","Laundromax","Express",3,"Premium"),
    OrdersData("hartford","6:30PM","Tide","WashNFold","standard",6,"Basic"),
    OrdersData("new haven","5PM","Tide","Topkat","standard",4,"Basic"),
    OrdersData("west haven","6PM","Wash","Laundromax","Express",3,"Premium"),
    OrdersData("hartford","6:30PM","Tide","WashNFold","standard",6,"Basic")
)
