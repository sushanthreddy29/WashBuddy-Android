package com.unh.washbuddy_android.ui.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class OrdersViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Orders Fragment"
    }
    val text: LiveData<String> = _text
}