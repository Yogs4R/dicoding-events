package com.example.dicoding_events.ui.finished

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dicoding_events.databinding.FragmentFinishedBinding
import com.example.dicoding_events.ui.EventAdapter
import com.example.dicoding_events.ui.ViewModelFactory

class FinishedFragment : Fragment() {
    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FinishedViewModel
    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[FinishedViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
        viewModel.findEvents()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter()
        binding.rvEvents.apply {
            layoutManager = GridLayoutManager(requireActivity(), 2)
            adapter = eventAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.listEvents.observe(viewLifecycleOwner) { data ->
            eventAdapter.submitList(data)
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
