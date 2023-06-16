package com.bangkit.sapigo.ui.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bangkit.sapigo.R
import com.bangkit.sapigo.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth


class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAccountBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        with(binding){
            btn1.setOnClickListener {
                auth.signOut()
                findNavController().navigate(R.id.action_accountFragment_to_loginFragment)
            }
        }
    }
}