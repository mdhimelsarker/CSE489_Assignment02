package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentBroadcastReceiverBinding

class BroadcastReceiverFragment : Fragment() {

    private var _binding: FragmentBroadcastReceiverBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBroadcastReceiverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = arrayOf(
            getString(R.string.option_custom_broadcast),
            getString(R.string.option_battery_receiver),
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            options,
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.spinnerReceiverOptions.adapter = adapter

        binding.btnProceed.setOnClickListener {
            val selectedPosition = binding.spinnerReceiverOptions.selectedItemPosition
            if (selectedPosition == 0) {
                val intent = Intent(requireContext(), CustomMessageActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(requireContext(), BatteryReceiverActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}