package com.mcs.dojomovie.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mcs.dojomovie.adapter.TransactionAdapter
import com.mcs.dojomovie.database.DatabaseHelper
import com.mcs.dojomovie.databinding.FragmentHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var transactionAdapter: TransactionAdapter

    private var currentUserId: Int = -1

    private val sessionFileName = "UserSession"
    private val keyUserId = "LOGGED_IN_USER_ID"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(requireContext())

        val sharedPreferences = requireActivity().getSharedPreferences(sessionFileName, Context.MODE_PRIVATE)
        currentUserId = sharedPreferences.getInt(keyUserId, -1)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        loadTransactionHistory()
    }

    private fun setupToolbar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbarHistoryPage)
            (activity as AppCompatActivity).supportActionBar?.apply {
                setDisplayShowTitleEnabled(false)
                setDisplayHomeAsUpEnabled(true)
            }
        }
        binding.toolbarHistoryPage.setNavigationOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(ArrayList())
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }

    private fun loadTransactionHistory() {
        if (currentUserId == -1) {
            if (::transactionAdapter.isInitialized) {
                transactionAdapter.updateTransactions(emptyList())
            }
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val detailedTransactions = withContext(Dispatchers.IO) {
                dbHelper.getTransaction(currentUserId)
            }

            if (isAdded) {
                if (detailedTransactions.isNotEmpty()) {
                    transactionAdapter.updateTransactions(detailedTransactions)
                } else {
                    transactionAdapter.updateTransactions(emptyList())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}