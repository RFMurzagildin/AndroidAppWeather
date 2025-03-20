package com.app.weatherapp.adapters.citiesAdapter

import androidx.recyclerview.widget.RecyclerView
import com.app.weatherapp.databinding.CityItemBinding
import com.app.weatherapp.db.entities.CitiesEntity

class CitiesHolder(private val binding: CityItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(city: CitiesEntity, onLongClickListener: (CitiesEntity) -> Unit) {
        with(binding) {
            tvCity.text = city.cityName
            itemView.setOnLongClickListener{
                onLongClickListener(city)
                true
            }
        }
    }
}