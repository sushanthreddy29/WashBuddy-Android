package com.unh.washbuddy_android.ui.orders

interface OnOrderButtonClickListener {
    fun onViewOrderClick(order: OrdersCard)
    fun onReorderClick(order: OrdersCard)
}