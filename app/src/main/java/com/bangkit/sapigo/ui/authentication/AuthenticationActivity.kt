package com.bangkit.sapigo.ui.authentication

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.sapigo.MainActivity
import com.bangkit.sapigo.R
import com.bangkit.sapigo.databinding.ActivityAuthenticationBinding

@Suppress("OverrideDeprecatedMigration")
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        if(savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container, LoginFragment.newInstance())
                .commit()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}