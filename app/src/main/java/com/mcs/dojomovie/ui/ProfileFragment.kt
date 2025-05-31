package com.mcs.dojomovie.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mcs.dojomovie.R
import com.mcs.dojomovie.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val sessionFileName = "UserSession"
    private val keyUserPhone = "LOGGED_IN_USER_PHONE"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        loadUserProfile()
        setupListeners()
    }

    private fun setupToolbar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbarProfilePage)
            (activity as AppCompatActivity).supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(true)
            }
        }
        binding.toolbarProfilePage.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun loadUserProfile() {
        val sharedPreferences = requireActivity().getSharedPreferences(sessionFileName, Context.MODE_PRIVATE)
        val userPhoneNumber = sharedPreferences.getString(keyUserPhone, "Nomor tidak tersedia")

        binding.tvNumber.text = userPhoneNumber
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            val message: String? = "Are you sure to logout?"
            showDialogBox(message)
        }
    }

    private fun showDialogBox(message: String?) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.logout_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvMessage: TextView = dialog.findViewById(R.id.tv_message)
        val btnYes: Button = dialog.findViewById(R.id.btn_yes)
        val btnNo: Button  =dialog.findViewById(R.id.btn_no)

        tvMessage.text = message

        btnYes.setOnClickListener{
            val sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                clear()
                apply()
            }

            val intent = Intent(requireActivity(), LoginPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            dialog.dismiss()
        }
        btnNo.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}