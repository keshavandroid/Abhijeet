package com.jeuxdevelopers.superchat.fragments.main.inbox

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.jeuxdevelopers.superchat.adapters.InboxAdapter
import com.jeuxdevelopers.superchat.databinding.FragmentInboxBinding
import com.jeuxdevelopers.superchat.interfaces.MainNavigationListener
import com.jeuxdevelopers.superchat.models.InboxModel
import com.jeuxdevelopers.superchat.utils.MyPreferences


class InboxFragment : Fragment() {
    private lateinit var binding: FragmentInboxBinding
    private lateinit var navigationListener: MainNavigationListener
    private lateinit var viewModel: InboxViewModel

    private lateinit var adapter: InboxAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInboxBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        navigationListener = requireContext() as MainNavigationListener
        viewModel = ViewModelProvider(this).get(InboxViewModel::class.java)
        initListeners()
        initRecycler()
    }

    private fun initRecycler() {
        val list: MutableList<InboxModel> = ArrayList()
        adapter = InboxAdapter(list, requireContext(), object : InboxAdapter.InboxSelectListener {
            override fun onInboxSelect(model: InboxModel) {
                navigationListener.fromInboxFragmentToChatFragment(model)
            }

        })
        binding.recyclerInbox.adapter = adapter
        viewModel.setInboxListener(
            adapter,
            list,
            MyPreferences(requireContext()).getCurrentUser().userId
        )
    }

    private fun initListeners() {
        binding.btnSettings.setOnClickListener { navigationListener.fromInboxFragmentToSettingsFragment() }
    }

    companion object {
        const val TAG = "InboxFragment"
    }
}