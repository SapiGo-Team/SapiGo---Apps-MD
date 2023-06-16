package com.bangkit.sapigo.ui.authentication

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bangkit.sapigo.BaseFragment
import com.bangkit.sapigo.MainActivity
import com.bangkit.sapigo.R
import com.bangkit.sapigo.data.api.ApiConfigAuth
import com.bangkit.sapigo.data.api.ApiService1
import com.bangkit.sapigo.data.api.ApiServiceAuth
import com.bangkit.sapigo.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : BaseFragment() {

    private  var _binding : FragmentLoginBinding? = null
    private val binding : FragmentLoginBinding
        get() = _binding!!

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        with(binding){
            btnSignin.setOnClickListener{
                val email = binding.edEmail.text.toString()
                val password = binding.edPassword.text.toString()
                signIn(email, password)
                (requireActivity()as MainActivity)

            }
            tvRegister.setOnClickListener{
                findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
            }
        }
        setProgressBar(binding.progressBar)

    }

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setBottomNavigationBar(false)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).setBottomNavigationBar(true)
    }

    private fun signIn(email: String, password: String) {
        Log.d(TAG, "signIn:$email")
        if (!validateForm()) {
            return
        }

        showProgressBar()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        context,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                hideProgressBar()
            }

}
    private fun validateForm(): Boolean {
        var valid = true

        val email = binding.edEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.edEmail.error = "Required."
            valid = false
        } else {
            binding.edEmail.error = null
        }

        val password = binding.edPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.edPassword.error = "Required."
            valid = false
        } else {
            binding.edPassword.error = null
        }

        return valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}