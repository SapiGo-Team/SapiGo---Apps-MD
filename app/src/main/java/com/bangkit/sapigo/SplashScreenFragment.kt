package com.bangkit.sapigo

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.bangkit.sapigo.databinding.FragmentSplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@SuppressLint("CustomSplashScreen")
class SplashScreenFragment : BaseFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentSplashScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = Firebase.auth
        val currentUser = auth.currentUser
        setProgressBar(binding.progressBar)
        showProgressBar()
        Handler(Looper.getMainLooper()).postDelayed({
            if (currentUser != null) {
                navigateToHomeFragment()
                hideProgressBar()
            } else {
                navigateToLoginScreen()
                hideProgressBar()
            }
        }, 2000) // 2 seconds delay
        return view
    }

    private fun navigateToLoginScreen() {
        findNavController().navigate(R.id.action_splashScreenFragment_to_loginFragment)
    }

    private fun navigateToHomeFragment() {
        findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setBottomNavigationBar(false)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).setBottomNavigationBar(true)
    }

}
