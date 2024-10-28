package com.unh.washbuddy_android.ui.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.unh.washbuddy_android.R
import com.unh.washbuddy_android.databinding.FragmentHomeBinding
import com.unh.washbuddy_android.databinding.FragmentNewOrder1Binding
import com.unh.washbuddy_android.ui.orders.OrdersData
import com.unh.washbuddy_android.ui.orders.ordersList


class NewOrder1Fragment : Fragment() {
   
    companion object {
        fun newInstance() = NewOrder1Fragment()
    }

    private val viewModel: NewOrder1ViewModel by viewModels()

    private var _binding: FragmentNewOrder1Binding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewOrder1Binding.inflate(inflater, container, false)
        binding.btncontinue2.setOnClickListener {
            if (binding.enteraddress.text.toString().isEmpty() || binding.enterpickuptime.text.toString().isEmpty() || binding.enterlaundromat.text.toString().isEmpty() || binding.enterdetergent.text.toString().isEmpty() || binding.enterdelivery.text.toString().isEmpty() || binding.enterextras.text.toString().isEmpty()) {

            } else {
                ordersList.add(
                    OrdersData(
                        binding.enteraddress.text.toString(),
                        binding.enterpickuptime.text.toString(),
                        binding.enterlaundromat.text.toString(),
                        binding.enterdetergent.text.toString(),
                        binding.enterdelivery.text.toString(),
                        0,
                        0,
                        0,
                        binding.enterextras.text.toString()
                    )
                )
                val action = NewOrder1FragmentDirections.actionNewOrder1FragmentToNavigationHome()
                findNavController().navigate(action)

                saveLaundryDetailsToFirebase()
            }
        }


        return binding.root
    }

    private fun saveLaundryDetailsToFirebase(){
        
    }
}