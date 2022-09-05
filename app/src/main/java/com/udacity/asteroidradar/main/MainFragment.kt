@file:Suppress("OverrideDeprecatedMigration", "OverrideDeprecatedMigration")

package com.udacity.asteroidradar.main

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.Filter
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    // define view model and view model factory
    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }

        ViewModelProvider(
            this,
            MainViewModel.MainViewModelFactory(activity.application)
        ).get(MainViewModel::class.java)
    }

    // define out adapter
    private val asteroidAdapter = MainAdapter(MainAdapter.AsteroidClickListner {asteroid ->
        viewModel.onAsteroidClicked(asteroid)
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter = asteroidAdapter

        viewModel.navigateToDetailAsteroid.observe(viewLifecycleOwner, Observer {

            if(it != null)
            {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.onAsteroidNav()
            }
        })

        // setHasOptions menu is deprecated
        // So we use requireActivity().addMenuProvider()
        requireActivity().addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater)
            {
                menuInflater.inflate(R.menu.main_overflow_menu, menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean
            {
                viewModel.onFilterChange(
                    when(menuItem.itemId){
                        R.id.show_rent_menu -> Filter.TODAY
                        R.id.show_all_menu -> Filter.ALL
                        else ->{
                            Filter.ALL
                        }
                    }
                )
                return false
            }

        },viewLifecycleOwner, Lifecycle.State.RESUMED)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.asteroidList.observe(viewLifecycleOwner, Observer {
            it.apply {
                asteroidAdapter.submitList(this)
            }
        })
    }

}
