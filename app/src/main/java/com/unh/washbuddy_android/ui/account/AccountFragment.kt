package com.unh.washbuddy_android.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.unh.washbuddy_android.AppData
import com.unh.washbuddy_android.databinding.FragmentAccountBinding
import com.unh.washbuddy_android.signin

class AccountFragment : Fragment() {

private var _binding: FragmentAccountBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val notificationsViewModel =
            ViewModelProvider(this).get(AccountViewModel::class.java)

    _binding = FragmentAccountBinding.inflate(inflater, container, false)
    val root: View = binding.root

      val email = binding.email
      val firstname = binding.firstname
      val lastname = binding.lastname
      val username = binding.username

    email.isFocusable = false
    email.isFocusableInTouchMode = false


    email.setText(AppData.email)
    firstname.setText(AppData.firstname)
    lastname.setText(AppData.lastname)
    username.setText(AppData.username)

    if (AppData.fingerprintsignin == true){
      (binding.dropFieldFingerprint.editText as? AutoCompleteTextView)?.setText("Enable", false)
    }
    else{
      (binding.dropFieldFingerprint.editText as? AutoCompleteTextView)?.setText("Disable", false)
    }

    binding.updateprofile.setOnClickListener {
      val builder = AlertDialog.Builder(requireContext())
      builder.setTitle("Confirm Update")
      builder.setMessage("Are you sure you want to update your details?")

      builder.setPositiveButton("Yes") { dialog, _ ->
        val firstname = binding.firstname.text.toString()
        val lastname = binding.lastname.text.toString()
        val username = binding.username.text.toString()

        updateuserprofile(firstname, lastname, username)

        dialog.dismiss()
        Toast.makeText(requireContext(),"Profile Updated",Toast.LENGTH_SHORT).show()
      }
      builder.setNegativeButton("No") { dialog, _ ->
        dialog.cancel() // Close the dialog if user selects "No"
      }

      // Show the dialog
      val alertDialog = builder.create()
      alertDialog.show()
    }

    binding.logout.setOnClickListener {
      FirebaseAuth.getInstance().signOut()
      AppData.username = ""
      AppData.email = ""
      AppData.firstname = ""
      AppData.lastname = ""
      AppData.useruid = ""
      AppData.documentid = ""

      val intent = Intent(requireContext(), signin::class.java)
      startActivity(intent)

      requireActivity().finishAffinity()
    }

    return root
  }

  private fun updateuserprofile(firstname: String, lastname: String, username: String){
    val fingerprintstatus: Boolean
    if(binding.fingerprint.text.toString() == "Enable"){
      fingerprintstatus = true
      AppData.fingerprintsignin = true
    }
    else{
      fingerprintstatus = false
      AppData.fingerprintsignin = false
    }

    val sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putBoolean("fingerprint_sign_in", fingerprintstatus )
    editor.apply()

    val userupdate =
      AppData.documentid?.let {
        FirebaseFirestore.getInstance().collection("UserCredentials").document(
          it
        )
      }

    val updates = mapOf(
      "Firstname" to firstname,
      "Lastname" to lastname,
      "Username" to username,
      "Fingerprintsignin" to fingerprintstatus
    )
    if (userupdate != null) {
      userupdate.update(updates)
        .addOnSuccessListener {
          AppData.username = username
          AppData.firstname = firstname
          AppData.lastname = lastname

          Toast.makeText(requireContext(),"Profile Updated",Toast.LENGTH_SHORT).show()
        }
        .addOnFailureListener {
          Toast.makeText(requireContext(),"Failed to Update",Toast.LENGTH_SHORT).show()
        }
    }

  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}