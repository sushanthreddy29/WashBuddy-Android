package com.unh.washbuddy_android.ui.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unh.washbuddy_android.AppData
import com.unh.washbuddy_android.databinding.FragmentViewReorderBinding
import com.unh.washbuddy_android.ui.home.NewOrder1FragmentDirections
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReorderFragment: Fragment() {

    private var _binding: FragmentViewReorderBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        _binding = FragmentViewReorderBinding.inflate(inflater, container, false)

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Reorder"
        }

        // Retrieve data passed through arguments
        val address = arguments?.getString("order_address") ?: "N/A"
        val date = arguments?.getString("order_date") ?: "N/A"
        val time = arguments?.getString("order_time") ?: "N/A"
        val laundromat = arguments?.getString("order_laundry") ?: "N/A"
        val detergent = arguments?.getString("order_detergent") ?: "N/A"
        val deliverySpeed = arguments?.getString("order_delivery") ?: "N/A"
        val smallBag = arguments?.getString("order_smallbag") ?: "N/A"
        val regularBag = arguments?.getString("order_regularbag") ?: "N/A"
        val extras = arguments?.getString("order_extras") ?: "N/A"
        val totalAmount = arguments?.getString("order_amount") ?: "$0.00"

        binding.enteraddress.setText(address)
        (binding.dropFieldTime.editText as? AutoCompleteTextView)?.setText(time, false)
        binding.enterlaundromat.setText(laundromat)
        binding.enterdetergent.setText(detergent)
        (binding.dropFieldDelivery.editText as? AutoCompleteTextView)?.setText(deliverySpeed, false)
        (binding.dropFieldSmallBag.editText as? AutoCompleteTextView)?.setText(smallBag, false)
        (binding.dropFieldRegularBag.editText as? AutoCompleteTextView)?.setText(regularBag, false)
        (binding.dropFieldExtra.editText as? AutoCompleteTextView)?.setText(extras, false)
        binding.totalAmountTextView.text = "Total Amount: $totalAmount"

        if(isTimeBetween6PMAndMidnight()){
            val today = Calendar.getInstance()
            val formatter = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())

            val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
            val dayAfterTomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }
            val theNextDay = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3) }

            val itemsDay = arrayOf("${formatter.format(tomorrow.time)} (Tomorrow)",
                "${formatter.format(dayAfterTomorrow.time)} (${SimpleDateFormat("EEEE", Locale.getDefault()).format(dayAfterTomorrow.time)})",
                "${formatter.format(theNextDay.time)} (${SimpleDateFormat("EEEE", Locale.getDefault()).format(theNextDay.time)})"
            )
            val textFieldDay = binding.dropFieldDate
            (textFieldDay.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(itemsDay)
        }
        else{
            val today = Calendar.getInstance()
            val formatter = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())

            val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
            val dayAfterTomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }

            val itemsDay = arrayOf("${formatter.format(today.time)} (Today)", "${formatter.format(tomorrow.time)} (Tomorrow)",
                "${formatter.format(dayAfterTomorrow.time)} (${SimpleDateFormat("EEEE", Locale.getDefault()).format(dayAfterTomorrow.time)})"
            )
            val textFieldDay = binding.dropFieldDate
            (textFieldDay.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(itemsDay)
        }

        // Define prices for bags
        val smallBagPrice = 5
        val regularBagPrice = 10

        // Define prices for extra services
        val basicPrice = 0
        val premiumPrice = 3
        val premiumPlusPrice = 5

        // Update total amount based on selected bag counts and extras
        fun updateTotalAmount(extraCharge: Int = 0) {
            val smallBagCount = binding.smallbag.text.toString().toIntOrNull() ?: 0
            val regularBagCount = binding.regularbag.text.toString().toIntOrNull() ?: 0

            // Calculate subtotal (Amount) with extra charge
            val amount = (smallBagCount * smallBagPrice + regularBagCount * regularBagPrice + extraCharge).toFloat()
            // Calculate tax (6% of the amount)
            val tax = 0.06f * amount
            // Calculate total amount
            val totalAmount = amount + tax

            // Update TextViews
            binding.amountTextView.text = "Amount: $${"%.2f".format(amount)}"
            binding.taxTextView.text = "Tax: $${"%.2f".format(tax)}"
            binding.totalAmountTextView.text = "Total Amount: $${"%.2f".format(totalAmount)}"
        }

        var charge = 0
        // Initialize listener for extra selection
        binding.enterextras.setOnItemClickListener { _, _, position, _ ->
            val extraCharge = when (position) {
                0 -> basicPrice      // Position 0 corresponds to "Basic"
                1 -> premiumPrice    // Position 1 corresponds to "Premium"
                2 -> premiumPlusPrice // Position 2 corresponds to "Premium+"
                else -> 0
            }
            charge = extraCharge
            updateTotalAmount(extraCharge)
        }

        // Set listeners to update amounts when bags are selected
        binding.smallbag.setOnItemClickListener { _, _, _, _ -> updateTotalAmount(charge) }
        binding.regularbag.setOnItemClickListener { _, _, _, _ -> updateTotalAmount(charge) }

        binding.btncontinue2.setOnClickListener{
            if (binding.enteraddress.text.toString()
                    .isEmpty() || binding.enterpickupdate.text.toString()
                    .isEmpty() || binding.enterpickuptime.text.toString()
                    .isEmpty() || binding.enterlaundromat.text.toString()
                    .isEmpty() || binding.enterdetergent.text.toString()
                    .isEmpty() || binding.enterdelivery.text.toString()
                    .isEmpty() || binding.smallbag.text.toString()
                    .isEmpty() || binding.regularbag.text.toString()
                    .isEmpty() || binding.enterextras.text.toString().isEmpty()
            ) {
                Toast.makeText(requireContext(), "Please enter all the fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                val action = ReorderFragmentDirections.actionReorderFragment2ToNavigationOrders()
                findNavController().navigate(action)

                saveLaundryDetailsToFirebase()
            }
        }

        updateTotalAmount(charge)

        return binding.root
    }

    private fun saveLaundryDetailsToFirebase() {
        val db = Firebase.firestore

        val email = AppData.email
        val address = binding.enteraddress.text.toString()
        val pickupdateorder = binding.enterpickupdate.text.toString()
        val pickupdate = pickupdateorder.take(11)
        val pickuptime = binding.enterpickuptime.text.toString()
        val selectlaundromat = binding.enterlaundromat.text.toString()
        val detergent = binding.enterdetergent.text.toString()
        val speed = binding.enterdelivery.text.toString()
        val smallbag = binding.smallbag.text.toString()
        val regularbag = binding.regularbag.text.toString()
        val extras = binding.enterextras.text.toString()
        val status = "Pending"

        val amountText = binding.totalAmountTextView.text.toString()
        val amountWithDollar = amountText.substringAfter("Total Amount: ").trim()


        val newOrder = hashMapOf(
            "email" to email,
            "address" to address,
            "pickupdate" to pickupdate,
            "pickuptime" to pickuptime,
            "laundromat" to selectlaundromat,
            "detergent" to detergent,
            "speed" to speed,
            "smallbag" to smallbag,
            "regularbag" to regularbag,
            "extras" to extras,
            "amount" to amountWithDollar,
            "status" to status,
        )

        db.collection("LaundryOrders")
            .add(newOrder)
            .addOnSuccessListener { documentReference ->
                Log.d("DB", "DocumentSnapshot written successfully with ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.w("DB", "Error adding document", exception)
            }
    }

    fun isTimeBetween6PMAndMidnight(): Boolean {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Check if time is between 6:00 PM (18:00) and 11:59 PM (23:59)
        return (currentHour in 18..23) || (currentHour == 23 && currentMinute == 59)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp() // Handle back navigation
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}