package com.example.ipcclient

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ipcclient.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.prefs.Preferences
import kotlin.properties.ReadOnlyProperty



class MainActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityMainBinding;
    private val binding get() = _binding;
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val navView: BottomNavigationView = binding.navView
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.navigation_aidl,R.id.navigation_messenger,R.id.navigation_broadcast))
        binding.apply {
            setContentView(root)
            navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
            setupActionBarWithNavController(navController,appBarConfiguration)
            navView.setupWithNavController(navController)
        }
        setContentView(binding.root)
        val share = SharedPrefUtils.getInstance(this)
        binding.fab.setOnClickListener{
            run {
                if (isDarkTheme(this)) {
                    Log.i("CHANGE THEME", "onCreate: MODE_NIGHT_NO")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    Log.i("CHANGE THEME", "onCreate: MODE_NIGHT_YES")
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }
        share.putBoolean("isNightTheme",isDarkTheme(this))
    }

    private fun isDarkTheme(context: Context): Boolean {
        val flag: Int = Configuration.UI_MODE_NIGHT_MASK and context.resources.configuration.uiMode
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

    fun isHideInput(view: View, me: MotionEvent): Boolean {
        if(view is EditText) {
            val location = intArrayOf(0,0)
            view.getLocationInWindow(location)
            val left = location[0]
            val top = location[1]
            val right = left + view.width
            val bottom = top + view.height
            return !(me.x > left && me.x < right && me.y > top && me.y < bottom)
        }
        return false
    }

    fun hideSoftInput(v: View){
        if(v.windowToken != null) {
            val manager = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(v.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}