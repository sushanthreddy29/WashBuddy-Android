package com.unh.washbuddy_android.ui.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    private var TAG = "WashBuddy-Android"

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
                Toast.makeText(requireContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show()
            } else {
                ordersList.add(
                    OrdersData(
                        "",
                        binding.enteraddress.text.toString(),
                        binding.enterpickuptime.text.toString(),
                        binding.enterlaundromat.text.toString(),
                        binding.enterdetergent.text.toString(),
                        binding.enterdelivery.text.toString(),
                        binding.smallbag.text.toString(),
                        binding.smallbag.text.toString(),
                        binding.smallbag.text.toString(),
                        binding.enterextras.text.toString()
                    )
                )
                val action = NewOrder1FragmentDirections.actionNewOrder1FragmentToNavigationHome()
                findNavController().navigate(action)

                saveLaundryDetailsToFirebase(ordersList.count() - 1)
            }
        }


        return binding.root
    }

    private fun saveLaundryDetailsToFirebase(index: Int){
        val db = Firebase.firestore



        val newOrder = hashMapOf(
            "address" to ordersList[index].address,
            "pickuptime" to ordersList[index].pickupTime,
            "laundromat" to ordersList[index].selectLaundromat,
            "detergent" to ordersList[index].detergentType,
            "speed" to ordersList[index].speed,
            "smallbag" to ordersList[index].smallbag,
            "regularbag" to ordersList[index].regularbag,
            "largebag" to ordersList[index].largebag,
            "extras" to ordersList[index].extras,
        )


        if (binding.enteraddress.text.toString().isEmpty() || binding.enterpickuptime.text.toString().isEmpty() || binding.enterlaundromat.text.toString().isEmpty() || binding.enterdetergent.text.toString().isEmpty() || binding.enterdelivery.text.toString().isEmpty() || binding.enterextras.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), "Please enter all the fields", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            db.collection("/UserCredentials/0UlkXB4hIqc5uGRU2lHw/LaundryOrders")
                .add(newOrder)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG,"DocumentSnapshot written successfully with ID: ${documentReference.id}")
                    ordersList[index].id = documentReference.id
                }
                .addOnFailureListener{ exception ->
                    Log.w(TAG,"Error adding document", exception)
                }
        }


    }
}