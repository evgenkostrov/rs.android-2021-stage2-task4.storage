package com.task4.storage.ui.fragments.list

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.coroutines.flow.collect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.task4.storage.R
import com.task4.storage.data.Animal
import com.task4.storage.databinding.FragmentListBinding
import com.task4.storage.util.HideKeyboard
import com.task4.storage.util.exhaustive
import com.task4.storage.util.onQueryTextChanged
import com.task4.storage.viewmodel.AnimalViewModel
import com.task4.storage.viewmodel.SortOrder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment(R.layout.fragment_list), ListAnimalAdapter.OnItemClickListener {
    private val viewModel: AnimalViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentListBinding.bind(view)
        val animalAdapter = ListAnimalAdapter(this)

        val context=requireContext()
        val viewReq=requireView()
        HideKeyboard.hideKeyboard(context,viewReq)

        binding.apply {
            recyclerViewList.apply {
                adapter = animalAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
            ItemTouchHelper(object :ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                   val animal = animalAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onAnimalSwiped(animal)
                }
            }).attachToRecyclerView(recyclerViewList)

            fabAddAnimalFragment.setOnClickListener {
                viewModel.onAddNewAnimalClick()
            }
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            when (sharedPreferences.getString("sort", "Name")) {
                "Name" -> viewModel.sortOrder.value = SortOrder.BY_NAME
                "Age" -> viewModel.sortOrder.value = SortOrder.BY_AGE
                "Breed" -> viewModel.sortOrder.value = SortOrder.BY_BREED
            }

setFragmentResultListener("add_update_request"){_,bundle->
    val result = bundle.getInt("add_update_request")
    viewModel.onAddUpdateResult(result)

}
        viewModel.animals.observe(viewLifecycleOwner) {
            animalAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.animalsEvent.collect{ event->
                when(event){
                    is AnimalViewModel.AnimalsEvent.ShowUndoDeleteMessage -> {
                        Snackbar.make(requireView(), "Animal deleted",Snackbar.LENGTH_LONG)
                            .setAction("UNDO"){
                             viewModel.onUndoDeleteClick(event.animal)
                            }.show()
                    }
                    is AnimalViewModel.AnimalsEvent.NavigateToAddAnimalScreen -> {
                        val action = ListFragmentDirections.actionListFragmentToAddFragment(null,"New Animal")
                        findNavController().navigate(action)
                    }
                    is AnimalViewModel.AnimalsEvent.NavigateToEditAnimalScreen -> {
                        val action = ListFragmentDirections.actionListFragmentToAddFragment(event.animal, "Update Animal")
                        findNavController().navigate(action)
                    }
                    is AnimalViewModel.AnimalsEvent.ShowAnimalSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg,Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onItemClick(animal: Animal) {
        viewModel.onAnimalSelected(animal)
    }

    override fun onCheckBoxClick(animal: Animal, isFavorite: Boolean) {
        viewModel.onAnimalCheckedChanged(animal,isFavorite)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_list, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_settings -> {
                findNavController().navigate(R.id.settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }
}