package ru.skillbranch.gameofthrones.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import ru.skillbranch.gameofthrones.ui.main.LoadResult
import ru.skillbranch.gameofthrones.ui.main.MainViewModel
import ru.skillbranch.gameofthrones.R

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel : MainViewModel
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.characters_list_screen)
        initViewModel()
        if (savedInstanceState == null)
            prepareData()
        navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )
//        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
//        val viewPager: ViewPager = findViewById(R.id.view_pager)
//        viewPager.adapter = sectionsPagerAdapter
//        val tabs: TabLayout = findViewById(R.id.tabs)
//        tabs.setupWithViewPager(viewPager)
    }

    private fun prepareData() {
        viewModel.syncDataIfNeeded().observe(this, Observer<LoadResult<Boolean>> {
            when (it) {
                is LoadResult.Loading -> {
                    navController.navigate(R.id.nav_splash)
                }
                is LoadResult.Success -> {
//                    val action = SplashFragmentDirections.actionNavSplashToNavHouses()
//                    navController.navigate(action)
                }
                is LoadResult.Error -> {
//                    Snackbar.make(
//                        root_container,
//                        it.errorMessage.toString(),
//                        Snackbar.LENGTH_INDEFINITE
//                    ).show()
                }
            }
        })
    }

    private fun initViewModel() {
        TODO("Not yet implemented")
    }
}