package com.example.dicoding_events.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicoding_events.R
import com.example.dicoding_events.databinding.FragmentHomeBinding
import com.example.dicoding_events.ui.EventAdapter
import com.example.dicoding_events.ui.ViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HomeViewModel
    private val upcomingAdapter = EventAdapter()
    private val finishedAdapter = EventAdapter()
    private lateinit var searchAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        searchAdapter = EventAdapter()
        setupRecyclerViews()
        setupHeaderNavigation()
        setupSearchView()
        observeViewModel()

        viewModel.findUpcomingEvents()
        viewModel.findFinishedEvents()
    }

    private fun setupRecyclerViews() {
        binding.rvUpcomingHome.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }

        binding.rvFinishedHome.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            adapter = finishedAdapter
        }

        binding.rvSearchEvents.apply {
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            adapter = searchAdapter
        }
    }

    private fun setupHeaderNavigation() {
        binding.layoutUpcomingHeader.setOnClickListener {
            navigateToDestination(R.id.navigation_upcoming)
        }

        binding.layoutFinishedHeader.setOnClickListener {
            navigateToDestination(R.id.navigation_finished)
        }
    }

    private fun navigateToDestination(destinationId: Int) {
        val navView = requireActivity().findViewById<BottomNavigationView?>(R.id.nav_view)
        if (navView != null) {
            navView.selectedItemId = destinationId
            return
        }
        findNavController().navigate(destinationId)
    }

    private fun setupSearchView() {
        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchView.editText.setOnEditorActionListener { textView, _, _ ->
            val searchBarText = textView.text.toString()
            binding.searchBar.setText(searchBarText)
            viewModel.searchEvents(searchBarText)
            binding.searchView.clearFocus()
            false
        }
    }

    private fun observeViewModel() {
        viewModel.listUpcoming.observe(viewLifecycleOwner) { data ->
            upcomingAdapter.submitList(data.take(5))
        }

        viewModel.listFinished.observe(viewLifecycleOwner) { data ->
            finishedAdapter.submitList(data.take(5))
        }

        viewModel.listSearch.observe(viewLifecycleOwner) { data ->
            searchAdapter.submitList(data)
            binding.tvEmptySearch.isVisible = data.isEmpty()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNullOrBlank()) {
                binding.tvErrorMessage.visibility = View.GONE
            } else {
                binding.tvErrorMessage.text = message
                binding.tvErrorMessage.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
