package com.unh.washbuddy_android

import java.io.Serializable

data class AdminCard (
    val date: String,
    val time: String,
    val email:String,
    val address: String,
    val laundry: String,
    val amount: String,
    val delivery: String,
    val status: String,
    val detergent: String,
    val smallbag: String,
    val regularbag: String,
    val extras: String,
    val orderId: String
):Serializable