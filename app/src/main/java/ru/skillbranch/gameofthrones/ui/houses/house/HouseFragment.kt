package ru.skillbranch.gameofthrones.ui.houses.house

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_house.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.ui.houses.HousesFragmentDirections
import ru.skillbranch.gameofthrones.utils.ItemDivider

class HouseFragment : Fragment() {

    private lateinit var charactersAdapter: CharactersAdapter
    private lateinit var viewModel: HouseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val houseName = arguments?.getString(HOUSE_NAME) ?: HouseType.STARK.title
        val vmFactory = HouseViewModelFactory(houseName)
        charactersAdapter = CharactersAdapter {
            val action = HousesFragmentDirections.actionNavHousesToNavCharacter(it.id, it.house, it.name )
            findNavController().navigate(action)
        }
        viewModel = ViewModelProvider(this, vmFactory).get(HouseViewModel::class.java)
        viewModel.getCharacters().observe(this, Observer<List<CharacterItem>> {
            charactersAdapter.updateItems(it)
        })
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        with(menu.findItem(R.id.search).actionView as SearchView) {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.handleSearchQuery(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.handleSearchQuery(newText)
                    return true
                }
            })
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_house, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(rv_characters_list) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(ItemDivider())
            adapter = charactersAdapter
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(HouseViewModel::class.java)
    }

    companion object {
        private const val HOUSE_NAME = "house_name"

        @JvmStatic
        fun newInstance(houseName: String): HouseFragment =
            HouseFragment().apply {
                arguments = bundleOf(HOUSE_NAME to houseName)
            }
    }

}