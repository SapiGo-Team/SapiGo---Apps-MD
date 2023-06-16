package com.bangkit.sapigo

import com.bangkit.sapigo.ui.navigation.HomeFragment
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.bangkit.sapigo.databinding.ActivityMainBinding
import com.bangkit.sapigo.ui.navigation.AccountFragment
import com.bangkit.sapigo.ui.scan.CameraActivity
import com.bangkit.sapigo.utils.Constanta
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var fragmentHomeFragment: HomeFragment? = null
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var topAppBar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(intent.getBooleanExtra("FROM_SPLASH_SCREEN", false)){
            finish()
            return
        }
        if (intent.getBooleanExtra("navigateToHome",false )) {
            val navController = findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.homeFragment)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.addOnBackStackChangedListener(onBackStackChangedListener)
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomAppBar = findViewById(R.id.bottomAppBar)

        // Obtain the NavController from the NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        fragmentHomeFragment = navHostFragment.childFragmentManager.findFragmentById(R.id.homeFragment) as? HomeFragment

        // Set the start destination as the current fragment
        val currentFragment = navHostFragment.childFragmentManager.fragments[0]
        if (currentFragment is HomeFragment) {
            fragmentHomeFragment = currentFragment
        }
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (navController.currentDestination?.id != R.id.homeFragment) {
                        navController.navigate(R.id.homeFragment)
                    }
                    true
                }
//                R.id.navigation_scanner -> {
//                    if (isPermissionGranted(this, Manifest.permission.CAMERA)) {
//                        val intent = Intent(this@MainActivity, CameraActivity::class.java)
//                        startActivity(intent)
//                    } else {
//                        ActivityCompat.requestPermissions(
//                            this@MainActivity,
//                            arrayOf(Manifest.permission.CAMERA),
//                            Constanta.CAMERA_PERMISSION_CODE
//                        )
//                    }
//                    true
                // Handle other navigation menu items if needed
                R.id.navigation_profile -> {
                        navController.navigate(R.id.accountFragment)

                    true
                }

                R.id.navigation_monitoring -> {
                    navController.navigate(R.id.monitoringFragment)

                    true
                }


                else -> false
            }
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.fabCamera.visibility = View.VISIBLE
                    bottomNavigationView.menu.findItem(R.id.navigation_home).isChecked = true

                }
                else -> {
                    binding.fabCamera.visibility = View.GONE
                    binding.fabCamera.visibility = View.GONE
                    bottomNavigationView.menu.findItem(destination.id)?.isChecked = true

                }
            }
        }
        binding.fabCamera.setOnClickListener {
            /* ask permission for camera first before launch camera */
            if (isPermissionGranted(this, Manifest.permission.CAMERA)) {
                val intent = Intent(this@MainActivity, CameraActivity::class.java)
                startActivity(intent)
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    Constanta.CAMERA_PERMISSION_CODE
                )

            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        val currentDestination = navController.currentDestination
        if (currentDestination?.id == R.id.loginFragment) {
            finish()
        } else if(currentDestination?.id == R.id.homeFragment){
            finish()
        }
        else {
            super.onBackPressed()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constanta.CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navController.navigate(R.id.action_homeFragment_to_cameraActivity)
            } else {
                // Handle permission denied case
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.removeOnBackStackChangedListener(onBackStackChangedListener)
    }


    fun setBottomNavigationBar(visible : Boolean) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val bottomAppBar = findViewById<BottomAppBar>(R.id.bottomAppBar)
        val buttonCamera = findViewById<FloatingActionButton>(R.id.fab_camera)
        if (visible) {
            bottomNavigationView.visibility = View.VISIBLE
            bottomAppBar.visibility = View.VISIBLE
            buttonCamera.visibility = View.VISIBLE

        } else {
            bottomNavigationView.visibility = View.GONE
            bottomAppBar.visibility = View.GONE
            buttonCamera.visibility = View.GONE
        }
    }


    private fun isPermissionGranted(context: Context, permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private val onBackStackChangedListener = FragmentManager.OnBackStackChangedListener {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        if (currentFragment is HomeFragment) {
            bottomNavigationView.selectedItemId = R.id.navigation_home
        } else if (currentFragment is AccountFragment) {
            bottomNavigationView.selectedItemId = R.id.navigation_profile
//        } else if(currentFragment is CameraActivity) {
//            bottomNavigationView.selectedItemId = R.id.navigation_scanner}
        }
    }

}
