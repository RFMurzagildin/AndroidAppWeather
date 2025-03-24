package com.app.weatherapp.adapters.citiesAdapter

import androidx.recyclerview.widget.DiffUtil
import com.app.weatherapp.db.entities.CitiesEntity

class CityDiffCallback(
    private val oldLIst: List<CitiesEntity>,
    private val newList: List<CitiesEntity>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldLIst.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldLIst[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldLIst[oldItemPosition] == newList[newItemPosition]
    }
}