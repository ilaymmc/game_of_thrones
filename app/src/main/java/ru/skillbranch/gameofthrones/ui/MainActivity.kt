package ru.skillbranch.gameofthrones.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_houses.*
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.ui.main.LoadResult
import ru.skillbranch.gameofthrones.ui.main.MainViewModel
import ru.skillbranch.gameofthrones.ui.splash.SplashFragmentDirections

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel : MainViewModel
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.characters_list_screen)
        initViewModel()
        if (savedInstanceState == null)
            prepareData()
        navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )
    }

    private fun prepareData() {
        viewModel.syncDataIfNeeded().observe(this, Observer<LoadResult<Boolean>> {
            when (it) {
                is LoadResult.Loading -> {
                    navController.navigate(R.id.nav_splash)
                }
                is LoadResult.Success -> {
                    val action = SplashFragmentDirections.actionNavSplashToNavHouses()
                    navController.navigate(action)
                }
                is LoadResult.Error -> {
                    Snackbar.make(
                        view_pager,
                        it.errorMessage.toString(),
                        Snackbar.LENGTH_INDEFINITE
                    ).show()
                }
            }
        })
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory(App.instance)).get(MainViewModel::class.java)
    }
}