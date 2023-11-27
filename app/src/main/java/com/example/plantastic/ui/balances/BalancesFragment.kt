package com.example.plantastic.ui.balances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plantastic.databinding.FragmentBalancesBinding

class BalancesFragment : Fragment() {

    private var _binding: FragmentBalancesBinding? = null
    private lateinit var balancesViewModel: BalancesViewModel
    private lateinit var balancesAdapter: BalancesAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBalancesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        balancesViewModel =
            ViewModelProvider(this)[BalancesViewModel::class.java]

        // Set up RecyclerView
        val recyclerView = binding.balancesRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        balancesAdapter = BalancesAdapter(balancesViewModel.balancesList)
        recyclerView.adapter = balancesAdapter

        // val textView: TextView = binding.balancesFragmentText
        balancesViewModel.text.observe(viewLifecycleOwner) {
            // textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
