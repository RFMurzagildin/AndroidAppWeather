package com.app.weatherapp.adapters.citiesAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.weatherapp.databinding.CityItemBinding
import com.app.weatherapp.db.di.ServiceLocator
import com.app.weatherapp.db.entities.CitiesEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CitiesAdapter(
    private var cities: MutableList<CitiesEntity>,
    private var context: Context
) : RecyclerView.Adapter<CitiesHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitiesHolder {
        val binding = CityItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CitiesHolder(binding, context)
    }

    override fun onBindViewHolder(holder: CitiesHolder, position: Int) {
        holder.bind(cities[position]) { city ->
            showDeleteConfirmationDialog(city)
        }
    }

    override fun getItemCount(): Int = cities.size

    private fun showDeleteConfirmationDialog(city: CitiesEntity) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Удаление")
        builder.setMessage("Вы уверены, что хотите удалить этот город?")
        builder.setPositiveButton("Да") { _, _ ->
            deleteCity(city)
        }
        builder.setNegativeButton("Нет", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteCity(city: CitiesEntity) {
        var repository = ServiceLocator.getCityRepository()
        GlobalScope.launch(Dispatchers.IO) {
            repository.delete(city.cityName)

            withContext(Dispatchers.Main) {
                val index = cities.indexOfFirst { it.cityName == city.cityName }
                if (index != -1) {
                    cities.removeAt(index)
                    notifyItemRemoved(index)
                }
            }
        }
    }

    fun updateData(newCities: List<CitiesEntity>) {
        val diffCallback = CityDiffCallback(cities, newCities.toMutableList())
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        cities.clear()
        cities.addAll(newCities)
        diffResult.dispatchUpdatesTo(this)
    }
}