package com.mcs.dojomovie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mcs.dojomovie.ui.HistoryFragment
import com.mcs.dojomovie.ui.HomeFragment
import com.mcs.dojomovie.ui.ProfileFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)

        if(savedInstanceState == null){
            loadFragment(HomeFragment())
            bottomNavigationView.selectedItemId = R.id.navigation_home
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            var addToBackStack = false

            when(item.itemId){
                R.id.navigation_home -> {
                    selectedFragment = HomeFragment()
                    clearBackStack()
                }
                R.id.navigation_history -> {
                    selectedFragment = HistoryFragment()
                    addToBackStack = true
                }
                R.id.navigation_profile -> {
                    selectedFragment = ProfileFragment()
                    addToBackStack = true
                }
            }
            if(selectedFragment != null){
                loadFragment(selectedFragment, addToBackStack)
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean = false){
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

    private fun clearBackStack() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            val first = supportFragmentManager.getBackStackEntryAt(0)
            supportFragmentManager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}