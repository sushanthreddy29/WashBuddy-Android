package com.unh.washbuddy_android.ui.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.unh.washbuddy_android.R
import com.unh.washbuddy_android.databinding.FragmentHomeBinding
import com.unh.washbuddy_android.databinding.FragmentNewOrder1Binding


class NewOrder1Fragment : Fragment() {
   
    companion object {
        fun newInstance() = NewOrder1Fragment()
    }

    private val viewModel: NewOrder1ViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this){
            //TODO: pop up for are you sure you want to go back?
            findNavController().navigate(NewOrder1FragmentDirections.actionNewOrder1FragmentToNavigationHome())
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_new_order1, container, false)
    }
}