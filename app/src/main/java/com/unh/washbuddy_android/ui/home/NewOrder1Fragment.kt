package com.unh.washbuddy_android.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unh.washbuddy_android.AppData
import com.unh.washbuddy_android.databinding.FragmentNewOrder1Binding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

// Some parts of this code were generated with the assistance of ChatGPT by OpenAI.
// This includes implementations related to [briefly describe the feature or functionality, e.g., "RecyclerView item click handling" or "Firebase data retrieval"].

class NewOrder1Fragment : Fragment() {

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1001
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private val viewModel: NewOrder1ViewModel by viewModels()

    private var _binding: FragmentNewOrder1Binding? = null
    private val binding get() = _binding!!
    private var TAG = "WashBuddy-Android"
    private lateinit var laundryAddress: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermission()
        if (!Places.isInitialized()) {
            //API key is restricted in the google cloud console, no exploitation threats
            Places.initialize(requireContext(), "AIzaSyAT5rJfK25wi5_0V5qcyVTcRsoyTv4FbSQ")
        }
    }

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewOrder1Binding.inflate(inflater, container, false)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        setHasOptionsMenu(true)

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            title = "Place Order"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.enterlaundromat.setOnClickListener {
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS)

            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(requireContext())

            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        val smallBagPrice = 5
        val regularBagPrice = 10

        val basicPrice = 0
        val premiumPrice = 3
        val premiumPlusPrice = 5

        fun updateTotalAmount(extraCharge: Int = 0) {
            val smallBagCount = binding.smallbag.text.toString().toIntOrNull() ?: 0
            val regularBagCount = binding.regularbag.text.toString().toIntOrNull() ?: 0

            val amount =
                (smallBagCount * smallBagPrice + regularBagCount * regularBagPrice + extraCharge).toFloat()

            val tax = 0.06f * amount

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

        updatePickupDateOptions()

        updatePickupTimeOptions()

        binding.btncontinue2.setOnClickListener {
            if (binding.enteraddress.text.toString().isEmpty() ||
                binding.enterpickupdate.text.toString().isEmpty() ||
                binding.enterpickuptime.text.toString().isEmpty() ||
                binding.enterlaundromat.text.toString().isEmpty() ||
                binding.enterdetergent.text.toString().isEmpty() ||
                binding.enterdelivery.text.toString().isEmpty() ||
                binding.smallbag.text.toString().isEmpty() ||
                binding.regularbag.text.toString().isEmpty() ||
                binding.enterextras.text.toString().isEmpty()
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

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)

                        val addressWithoutCountry = place.address?.substringBeforeLast(",")
                        laundryAddress = addressWithoutCountry.toString()
                        binding.enterlaundromat.setText(place.name) // Update the TextView with the selected place
                        binding.enterlaundryaddress.setText(addressWithoutCountry)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = data?.let { Autocomplete.getStatusFromIntent(it) }
                    Log.i(TAG, "An error occurred: ${status?.statusMessage}")
                }
                AppCompatActivity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                    Log.i(TAG, "Autocomplete canceled.")
                }
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // At least one of the permissions is not granted, request for permissions
            requestLocationPermissions()
        } else {
            // Both permissions are already granted
            // You can perform your functionality that requires these permissions
        }
    }

    @Suppress("DEPRECATION")
    private fun requestLocationPermissions() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                ) {
                    // Both permissions were granted
                    // You can perform your functionality that requires these permissions
                } else {
                    // At least one of the permissions was denied
                    // You can disable the functionality that depends on these permissions or inform the user
                }
            }
        }
    }

    //https://firebase.google.com/docs/firestore/manage-data/add-data
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
        val orderId = UUID.randomUUID().toString()
        val laundryaddress = binding.enterlaundryaddress.text.toString()

        val amountText = binding.totalAmountTextView.text.toString()
        val amountWithDollar = amountText.substringAfter("Total Amount: ").trim()


        val newOrder = hashMapOf(
            "email" to email,
            "address" to address,
            "pickupdate" to pickupdate,
            "pickuptime" to pickuptime,
            "laundromat" to selectlaundromat,
            "laundryaddress" to laundryaddress,
            "detergent" to detergent,
            "speed" to speed,
            "smallbag" to smallbag,
            "regularbag" to regularbag,
            "extras" to extras,
            "amount" to amountWithDollar,
            "status" to status,
            "orderId" to orderId,
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

    fun isTimeBetween6PMAndMidnight(): Boolean {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Check if time is between 6:00 PM (18:00) and 11:59 PM (23:59)
        return (currentHour in 18..23) || (currentHour == 23 && currentMinute == 59)
    }

    //https://developer.android.com/reference/com/google/android/material/textfield/MaterialAutoCompleteTextView#setSimpleItems(int%5B%5D)
    private fun updatePickupDateOptions(){
        if (isTimeBetween6PMAndMidnight()) {
            val today = Calendar.getInstance()
            val formatter = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())

            val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
            val dayAfterTomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }
            val theNextDay = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 3) }

            val itemsDay = arrayOf(
                "${formatter.format(tomorrow.time)} (Tomorrow)",
                "${formatter.format(dayAfterTomorrow.time)} (${
                    SimpleDateFormat(
                        "EEEE",
                        Locale.getDefault()
                    ).format(dayAfterTomorrow.time)
                })",
                "${formatter.format(theNextDay.time)} (${
                    SimpleDateFormat(
                        "EEEE",
                        Locale.getDefault()
                    ).format(theNextDay.time)
                })"
            )
            val textFieldDay = binding.dropFieldDate
            (textFieldDay.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(itemsDay)
        } else {
            val today = Calendar.getInstance()
            val formatter = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())

            val tomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
            val dayAfterTomorrow = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }

            val itemsDay = arrayOf(
                "${formatter.format(today.time)} (Today)",
                "${formatter.format(tomorrow.time)} (Tomorrow)",
                "${formatter.format(dayAfterTomorrow.time)} (${
                    SimpleDateFormat(
                        "EEEE",
                        Locale.getDefault()
                    ).format(dayAfterTomorrow.time)
                })"
            )
            val textFieldDay = binding.dropFieldDate
            (textFieldDay.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(itemsDay)
        }
    }

    //https://developer.android.com/reference/com/google/android/material/textfield/MaterialAutoCompleteTextView#setSimpleItems(int%5B%5D)
    private fun updatePickupTimeOptions() {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)

        val timeSlots = arrayOf(
            "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM", "03:00 PM", "03:30 PM",
            "04:00 PM", "04:30 PM", "05:00 PM", "05:30 PM", "06:00 PM"
        )

        val filteredTimeSlots = if (currentHour >= 18) {
            timeSlots
        } else {
            timeSlots.filter { timeSlot ->
                val timeParts = timeSlot.split(":")
                val hour = timeParts[0].toInt()
                val minute = timeParts[1].substring(0, 2).toInt()
                val isPM = timeSlot.contains("PM")

                val adjustedHour = if (isPM && hour < 12) hour + 12 else hour

                val timeCalendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, adjustedHour)
                    set(Calendar.MINUTE, minute)
                }
                timeCalendar.after(currentTime)
            }.toTypedArray()
        }

        val textFieldTime = binding.dropFieldTime
        (textFieldTime.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(filteredTimeSlots)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
