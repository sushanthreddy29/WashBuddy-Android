package com.unh.washbuddy_android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.unh.washbuddy_android.databinding.FragmentHomeBinding

// Some parts of this code were generated with the assistance of ChatGPT by OpenAI.
// This includes implementations related to [briefly describe the feature or functionality, e.g., "RecyclerView item click handling" or "Firebase data retrieval"].

class HomeFragment : Fragment() {

private var _binding: FragmentHomeBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    val root: View = binding.root

    val textView: TextView = binding.textHome
    homeViewModel.text.observe(viewLifecycleOwner) {
      textView.text = it
    }
    return root
  }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNewOrder.setOnClickListener {
            val action = HomeFragmentDirections.actionNavigationHomeToNewOrder1Fragment()
            findNavController().navigate(action)
        }
    }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}