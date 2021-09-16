package com.task4.storage.ui.fragments.add

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.task4.storage.R
import com.task4.storage.databinding.FragmentAddBinding
import com.task4.storage.util.exhaustive
import com.task4.storage.viewmodel.AddViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddFragment : Fragment(R.layout.fragment_add) {
    private val viewModel: AddViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddBinding.bind(view)
        binding.apply {
            editTextName.setText(viewModel.animalName)
            textViewDate.text = "${viewModel.animal?.createdDateFormatted ?: ""}"
            editTextAge.setText(viewModel.animalAge.toString())
            editTextBreed.setText(viewModel.animalBreed)

            editTextName.addTextChangedListener {
                viewModel.animalName = it.toString()
            }
            editTextAge.addTextChangedListener {

                viewModel.animalAge = Integer.parseInt(it.toString())
            }
            editTextBreed.addTextChangedListener {
                viewModel.animalBreed = it.toString()
            }

            fabAddAnimalDb.setOnClickListener {
                viewModel.onSaveClick()

            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addAnimalsEvent.collect { event ->
                when (event) {
                    is AddViewModel.AddUpdateAnimalEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is AddViewModel.AddUpdateAnimalEvent.NavigateBackWithResult -> {

                        setFragmentResult(
                            "add_update_request",
                            bundleOf("add_update_request" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }
    }
}