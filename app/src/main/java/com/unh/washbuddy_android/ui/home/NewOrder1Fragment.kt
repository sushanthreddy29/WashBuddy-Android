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
            if (binding.enteraddress.text.toString()
                    .isEmpty() || binding.enterpickuptime.text.toString()
                    .isEmpty() || binding.enterlaundromat.text.toString()
                    .isEmpty() || binding.enterdetergent.text.toString()
                    .isEmpty() || binding.enterdelivery.text.toString()
                    .isEmpty() || binding.smallbag.text.toString()
                    .isEmpty() || binding.regularbag.text.toString()
                    .isEmpty() || binding.largebag.text.toString()
                    .isEmpty() || binding.enterextras.text.toString().isEmpty()
            ) {
                Toast.makeText(requireContext(), "Please enter all the fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val action = NewOrder1FragmentDirections.actionNewOrder1FragmentToNavigationHome()
                findNavController().navigate(action)

                saveLaundryDetailsToFirebase()
            }
        }


        return binding.root
    }

    private fun saveLaundryDetailsToFirebase() {
        val db = Firebase.firestore

        val address = binding.enteraddress.text.toString()
        val pickuptime = binding.enterpickuptime.text.toString()
        val selectlaundromat = binding.enterlaundromat.text.toString()
        val detergent = binding.enterdetergent.text.toString()
        val speed = binding.enterdelivery.text.toString()
        val smallbag = binding.smallbag.text.toString()
        val regularbag = binding.regularbag.text.toString()
        val largebag = binding.largebag.text.toString()
        val extras = binding.enterextras.text.toString()


        val newOrder = hashMapOf(
            "address" to address,
            "pickuptime" to pickuptime,
            "laundromat" to selectlaundromat,
            "detergent" to detergent,
            "speed" to speed,
            "smallbag" to smallbag,
            "regularbag" to regularbag,
            "largebag" to largebag,
            "extras" to extras,
        )

        db.collection("LaundryOrders")
            .add(newOrder)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written successfully with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error adding document", exception)
            }
    }


}
