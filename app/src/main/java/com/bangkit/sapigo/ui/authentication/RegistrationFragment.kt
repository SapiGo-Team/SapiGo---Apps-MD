package com.bangkit.sapigo.ui.authentication

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.bangkit.sapigo.BaseFragment
import com.bangkit.sapigo.MainActivity
import com.bangkit.sapigo.R
import com.bangkit.sapigo.data.Login
import com.bangkit.sapigo.data.Register
import com.bangkit.sapigo.data.api.ApiConfigAuth
import com.bangkit.sapigo.databinding.FragmentRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationFragment : BaseFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentRegistrationBinding
    val error = MutableLiveData("")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        auth = Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val username = binding.edName.text.toString()
            val email = binding.edEmail.text.toString()
            val password = binding.edPassword.text.toString()
            createAccount(username,email, password)
        }
        setProgressBar(binding.progressBar)
    }

    private fun createAccount(username: String, email: String, password: String) {
        Log.d(TAG, "createAccount:$email")
        if (!validateForm()) {
            return
        }

        showProgressBar()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(
                        context,
                        "Registration Successful.",
                        Toast.LENGTH_SHORT,
                    ).show()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        context,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                hideProgressBar()
            }

    }


    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).setBottomNavigationBar(false)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).setBottomNavigationBar(true)
    }

    private fun validateForm(): Boolean {
        var valid = true

        val name = binding.edName.text.toString()
        if (TextUtils.isEmpty(name)) {
            binding.edName.error = "Required."
            valid = false
        } else {
            binding.edName.error = null
        }

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
        } else if (password.length < 6){
            binding.edPassword.error = "Password Should be at least 6 characters"
        }
        else {
            binding.edPassword.error = null
        }
        return valid
    }

    private fun setName(name: String){
        binding.edName.setText(name)
    }

}