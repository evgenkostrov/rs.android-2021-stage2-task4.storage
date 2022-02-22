package com.task4.storage.ui.fragments.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.task4.storage.data.Animal
import com.task4.storage.databinding.ItemRowBinding

class ListAnimalAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Animal, ListAnimalAdapter.AnimalsViewHolder>(
        DiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalsViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimalsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimalsViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
        ItemAnimation.animateLeftRight(holder.itemView, position)


    }

    inner class AnimalsViewHolder(private val binding: ItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val animal = getItem(position)
                        listener.onItemClick(animal)
                    }
                }
                checkBoxFavorite.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val animal = getItem(position)
                        listener.onCheckBoxClick(animal, checkBoxFavorite.isChecked)
                    }
                }
            }
        }

        fun bind(animal: Animal) {
            binding.apply {
                checkBoxFavorite.isChecked = animal.favorite
                textViewName.text = animal.name
                textViewAge.text = animal.age.toString()
                textViewBreed.text = animal.breed
                textViewId.text = animal.id.toString()
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(animal: Animal)
        fun onCheckBoxClick(animal: Animal, isFavorite: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Animal>() {
        override fun areItemsTheSame(oldItem: Animal, newItem: Animal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Animal, newItem: Animal): Boolean {
            return oldItem == newItem
        }
    }
}