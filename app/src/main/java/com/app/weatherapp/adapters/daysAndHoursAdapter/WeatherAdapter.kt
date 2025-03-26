package com.app.weatherapp.adapters.daysAndHoursAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.weatherapp.R
import com.app.weatherapp.databinding.ListItemBinding
import com.app.weatherapp.models.WeatherModel
import com.squareup.picasso.Picasso

class WeatherAdapter(
    private val listener: Listener?
) : ListAdapter<WeatherModel, WeatherAdapter.Holder>(Comparator()) {

    class Holder(view: View, private val listener: Listener?) : RecyclerView.ViewHolder(view) {
        private val binding = ListItemBinding.bind(view)
        private var itemTemp: WeatherModel? = null

        init {
            itemView.setOnClickListener {
                itemTemp?.let { listener?.onClick(it) }
            }
        }

        fun bind(item: WeatherModel) = with(binding) {
            itemTemp = item
            tvDate.text = item.time
            tvCondition.text = item.condition
            if (!item.current) {
                val parseMaxTemp = item.maxTemp.toFloat().toInt()
                val parseMinTemp = item.minTemp.toFloat().toInt()
                if(parseMaxTemp > 0 && parseMinTemp > 0){
                    tvTemp.text = "+${item.maxTemp}°/+${item.minTemp}°"
                }else if(parseMaxTemp > 0 && parseMinTemp <= 0){
                    tvTemp.text = "+${item.maxTemp}°/${item.minTemp}°"
                }else if(parseMaxTemp <= 0 && parseMinTemp > 0){
                    tvTemp.text = "${item.maxTemp}°/+${item.minTemp}°"
                }else{
                    tvTemp.text = "${item.maxTemp}°/${item.minTemp}°"
                }

            } else {
                val parseTemp = item.currentTemp.toFloat().toInt()
                if (parseTemp <= 0) {
                    tvTemp.text = "$parseTemp°"
                } else {
                    tvTemp.text = "+$parseTemp°"
                }
            }
            Picasso.get().load("https:" + item.imageUrl).into(im)
        }
    }

    class Comparator : DiffUtil.ItemCallback<WeatherModel>() {
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener {
        fun onClick(item: WeatherModel)
    }
}