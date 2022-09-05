package com.udacity.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.AsteroidItemLayoutBinding

class MainAdapter(private val clickListner:AsteroidClickListner):androidx.recyclerview.widget.ListAdapter
<Asteroid,MainAdapter.AsteroidViewHolder>(DiffCallback)
{

    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

    }

    // handle asteroid click
    class AsteroidClickListner(val clickListner: (asteroid:Asteroid) -> Unit)
    {
        fun click(asteroid: Asteroid) = clickListner(asteroid)
    }

    class AsteroidViewHolder(var binding: AsteroidItemLayoutBinding): RecyclerView.ViewHolder(binding.root)
    {
        fun bind(asteroid: Asteroid)
        {
            binding.asteroid = asteroid
            binding.executePendingBindings()
        }
    }

    // Here we add custom layout for asteroid
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val withDataBinding: AsteroidItemLayoutBinding =
            AsteroidItemLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return AsteroidViewHolder(withDataBinding)
    }


    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        val asteroid = getItem(position)

        holder.also {
            it.itemView.setOnClickListener{
                clickListner.click(asteroid)
                // clickListner.onClick(asteroid)
            }
            it.bind(asteroid)
        }
    }
}