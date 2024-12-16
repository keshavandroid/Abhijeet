package com.jeuxdevelopers.superchat.fragments.main.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.jeuxdevelopers.superchat.adapters.UsersAdapter
import com.jeuxdevelopers.superchat.databinding.FragmentHomeBinding
import com.jeuxdevelopers.superchat.interfaces.MainNavigationListener
import com.jeuxdevelopers.superchat.models.UserModel


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var navigationListener: MainNavigationListener
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: UsersAdapter
    val list:MutableList<UserModel> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        navigationListener = requireContext() as MainNavigationListener
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        initClickListeners()
        initRecycler()
        initSearch()
    }

    private fun initSearch() {
        binding.autoSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
             if (p0?.isNotEmpty()!!){
                 adapter.filter.filter(p0.toString())
             }else{
                 adapter.setData(list)
             }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    private fun initRecycler() {
        adapter = UsersAdapter(list,requireContext(),object : UsersAdapter.UserSelectListener{
            override fun onUserSelect(userModel: UserModel) {
                navigationListener.fromHomeFragmentToOtherProfileFragment(userModel)
            }

        })
        binding.recyclerUsers.adapter = adapter
        viewModel.setRecyclerListener(adapter,list)
    }

    private fun initClickListeners() {
        binding.btnSettings.setOnClickListener { navigationListener.fromHomeFragmentToSettingsFragment() }
    }

    companion object {
        const val TAG = "HomeFragment"
    }

    override fun onStop() {
        super.onStop()
        list.clear()
    }
}