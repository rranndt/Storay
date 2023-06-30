package dev.rranndt.storay.presentation.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import dev.rranndt.storay.R
import dev.rranndt.storay.databinding.ActivityMainBinding
import dev.rranndt.storay.presentation.auth.AuthActivity
import dev.rranndt.storay.presentation.main.addstory.AddStoryActivity
import dev.rranndt.storay.presentation.main.home.HomeFragment
import dev.rranndt.storay.presentation.main.maps.MapsFragment
import dev.rranndt.storay.util.Helper.alert
import dev.rranndt.storay.util.Helper.negativeButton
import dev.rranndt.storay.util.Helper.positiveButton

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setSupportActionBar(binding.mainToolbar)
        setContentView(binding.root)

        setupNavHost()
        setupToolbar()
        setupTabLayout()
    }

    private fun setupNavHost() {
        val navHost = supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        navController = navHost.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupToolbar() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.actionChangeLanguage -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))

                    R.id.actionSignOut -> {
                        alert {
                            setTitle(getString(R.string.text_sign_out))
                            setMessage(getString(R.string.alert_dialog_message))
                            positiveButton {
                                viewModel.signOut()
                                startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                                finish()
                            }
                            negativeButton { it.dismiss() }
                        }
                    }

                    R.id.actionAddStory -> startActivity(
                        Intent(
                            this@MainActivity,
                            AddStoryActivity::class.java
                        )
                    )
                }
                return true
            }
        })
    }

    private fun setupTabLayout() {
        val titles = ArrayList<String>().apply {
            add(getString(R.string.title_dashboard))
            add(getString(R.string.title_maps))
        }
        val fragment = ArrayList<Fragment>().apply {
            add(HomeFragment())
            add(MapsFragment())
        }
        val pagerAdapter = PagerAdapter(fragment, this)
        binding.apply {
            viewPager.apply {
                adapter = pagerAdapter
                isUserInputEnabled = false
            }
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }
    }
}