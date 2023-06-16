package com.bangkit.sapigo.ui.navigation

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.sapigo.MainActivity
import com.bangkit.sapigo.adapter.ResultsAdapter
import com.bangkit.sapigo.data.ResultData
import com.bangkit.sapigo.data.database.MyDatabaseHelper
import com.bangkit.sapigo.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ResultsAdapter
    private val resultsList = ArrayList<ResultData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        recyclerView = binding.recyclerViewResults

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setBottomNavigationBar(visible = true)

        setupRecyclerView()
        displayDataFromDatabase()

        val username = arguments?.getString("username")
        if (!username.isNullOrEmpty()) {
            val welcomeMessage = "Hello $username, How may I help you today?"
            binding.tvWelcome.text = welcomeMessage
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ResultsAdapter(resultsList)
        recyclerView.adapter = adapter
    }

    private fun displayDataFromDatabase() {
        val databaseHelper = MyDatabaseHelper(requireContext())
        val resultList = databaseHelper.getAllData()

        resultsList.clear()
        resultsList.addAll(resultList)
        adapter.notifyDataSetChanged()
    }
}
