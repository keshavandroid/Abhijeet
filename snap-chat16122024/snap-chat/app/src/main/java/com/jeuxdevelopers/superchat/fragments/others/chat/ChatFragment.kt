package com.jeuxdevelopers.superchat.fragments.others.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.options
import com.jeuxdevelopers.superchat.adapters.ChatAdapter
import com.jeuxdevelopers.superchat.databinding.FragmentChatBinding
import com.jeuxdevelopers.superchat.dialogs.WaitingDialog
import com.jeuxdevelopers.superchat.enums.MessageType
import com.jeuxdevelopers.superchat.models.ChatModel
import com.jeuxdevelopers.superchat.utils.Constants
import com.jeuxdevelopers.superchat.utils.Constants.OTHER_USER_IMAGE
import com.jeuxdevelopers.superchat.utils.MyPreferences
import com.jeuxdevelopers.superchat.utils.Utils
import com.vanniktech.emoji.EmojiPopup


class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var viewModel: ChatViewModel
    private lateinit var waitingDialog: WaitingDialog

    private lateinit var imageLauncher: ActivityResultLauncher<CropImageContractOptions>

    private lateinit var adapter: ChatAdapter

    private lateinit var emojiPopup: EmojiPopup

    private lateinit var otherUserId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)
        var otherUserId = arguments?.getString(Constants.OTHER_USER_ID)
        if (otherUserId == null) {
            otherUserId = ""
        }
        this.otherUserId = otherUserId
        viewModel.setChatRepository(otherUserId, MyPreferences(requireContext()))
        viewModel.setChatRates(otherUserId)
        waitingDialog = WaitingDialog(requireContext())
        emojiPopup = EmojiPopup.Builder.fromRootView(binding.root).build(binding.etMessage)
        initViews()
        initClickListeners()
        initObservers()
        initLaunchers()
        initChatRecycler()
    }

    private fun initViews() {
        if (arguments?.getString(Constants.OTHER_USER_NAME) != null) {
            binding.tvUserName.text = arguments?.getString(Constants.OTHER_USER_NAME)
        }
    }

    private fun initChatRecycler() {
        val otherUserProfileImage: String = if (arguments?.getString(OTHER_USER_IMAGE) != null) {
            arguments?.getString(OTHER_USER_IMAGE)!!
        } else {
            ""
        }
        val list: MutableList<ChatModel> = ArrayList()
        adapter = ChatAdapter(list, requireContext(), otherUserProfileImage)
        binding.recyclerChat.adapter = adapter
        viewModel.setChatRecyclerListener(
            adapter,
            list,
            binding.recyclerChat,
            requireContext(),
            requireActivity()
        ,otherUserId)
    }

    private fun initObservers() {
        viewModel.isWaiting.observe(viewLifecycleOwner) { waitState ->
            waitState?.let {
                if (it.status) {
                    waitingDialog.show(it.message)
                } else {
                    waitingDialog.dismiss()
                }
            }
        }

        viewModel.isValidated.observe(viewLifecycleOwner) { validation ->
            validation?.let {
                if (!validation.status && validation.inputView != null) {
                    validation.inputView!!.error = validation.message
                } else if (!validation.status && validation.inputView == null) {
                    if (validation.message != "Empty")
                        Utils.showShortToast(requireContext(), validation.message)
                } else if (validation.status) {
                    binding.etMessage.setText("")
                    viewModel.sendMessage(
                        it.message,
                        MessageType.TEXT,
                        otherUserId,
                        requireContext()
                    )
                }
            }
        }

        viewModel.addImage.observe(viewLifecycleOwner) { profileUrl ->
            if (profileUrl.isNotEmpty() && profileUrl != "dum") {
                viewModel.sendMessage(profileUrl, MessageType.IMAGE, otherUserId, requireContext())
            } else if (profileUrl.isEmpty()) {
                Utils.showShortToast(requireContext(), "Unable to upload image")
            }
        }

        viewModel.isMessageSent.observe(viewLifecycleOwner) { isSent ->
            isSent?.let {
                if (it) {
                    binding.etMessage.setText("")
                }
            }
        }

        viewModel.isAbleToSend.observe(viewLifecycleOwner) { isAble ->
            isAble?.let {
                if (!it) {
                    Utils.showShortToast(
                        requireContext(),
                        "Message can't be sent due to insufficient balance"
                    )
                }
            }
        }

    }

    private fun initClickListeners() {
        binding.btnBack.setOnClickListener { requireActivity().onBackPressed() }
        binding.btnSend.setOnClickListener { viewModel.validate(binding.etMessage.text.toString()) }
        binding.btnCamera.setOnClickListener { imageLauncher.launch(options { }) }
        binding.btnEmoji.setOnClickListener {
            if (emojiPopup.isShowing) {
                emojiPopup.dismiss()
            } else {
                emojiPopup.show()
            }
        }
    }

    private fun initLaunchers() {
        imageLauncher = this.registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                val uriContent = result.uriContent
                if (uriContent != null) {
                    viewModel.addChatImage(uriContent, MessageType.IMAGE)
                } else {
                    Utils.showShortToast(requireContext(), "Image not found")
                }
            } else {
                // an error occurred
                //val exception = result.error
                Utils.showShortToast(
                    requireContext(),
                    "Something went wrong!${result.error?.message}"
                )
            }
        }
    }

    companion object {
        const val TAG = "ChatFragment"
    }
}