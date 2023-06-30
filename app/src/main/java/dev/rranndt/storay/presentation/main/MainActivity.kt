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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.rranndt.storay.R
import dev.rranndt.storay.databinding.ActivityMainBinding
import dev.rranndt.storay.presentation.auth.AuthActivity
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
        setSupportActionBar(binding.toolbar)
        setContentView(binding.root)

        setupNavHost()
        setupToolbar()
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

                navController.addOnDestinationChangedListener { _, destination, _ ->
                    menu.apply {
                        if (destination.id == R.id.homeFragment) {
                            findItem(R.id.actionAddStory).isVisible = true
                            findItem(R.id.actionChangeLanguage).isVisible = true
                            findItem(R.id.actionSignOut).isVisible = true
                            supportActionBar?.show()
                            supportActionBar?.setDisplayHomeAsUpEnabled(false)
                        } else {
                            supportActionBar?.show()
                            supportActionBar?.setDisplayHomeAsUpEnabled(true)
                            findItem(R.id.actionAddStory).isVisible = false
                            findItem(R.id.actionChangeLanguage).isVisible = false
                            findItem(R.id.actionSignOut).isVisible = false
                        }
                    }
                }
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

                    R.id.actionAddStory -> navController.navigate(R.id.action_homeFragment_to_addStoryFragment)

                    android.R.id.home -> onBackPressed()
                }
                return true
            }
        })
    }
}