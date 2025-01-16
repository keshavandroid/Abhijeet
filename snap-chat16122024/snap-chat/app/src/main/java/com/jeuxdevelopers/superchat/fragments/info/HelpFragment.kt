package com.jeuxdevelopers.superchat.fragments.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jeuxdevelopers.superchat.databinding.FragmentHelpBinding


class HelpFragment : Fragment() {
    private lateinit var binding: FragmentHelpBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHelpBinding.inflate(inflater,container,false)
        init()
        return binding.root
    }

    private fun init(){
    setClickListeners()
    }

    private fun setClickListeners() {
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
    }

    companion object {
    const val TAG = "HelpFragment"
    }
}