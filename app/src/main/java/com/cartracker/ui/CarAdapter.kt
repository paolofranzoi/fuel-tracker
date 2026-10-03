package com.cartracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cartracker.R
import com.cartracker.data.Car
import com.cartracker.databinding.ItemCarBinding

class CarAdapter(
    private val onCarClick: (Car) -> Unit,
    private val onCarLongClick: (Car, View) -> Unit
) : ListAdapter<Car, CarAdapter.VH>(DIFF) {

    private var selectedTarga: String? = null

    fun setSelectedTarga(targa: String?) {
        selectedTarga = targa
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemCarBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val b: ItemCarBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(car: Car) {
            b.textCarName.text = car.nome ?: car.targa
            b.textCarPlate.text = car.targa
            b.textCarFuel.text = car.tipoCarburantePreferito
            val sel = car.targa == selectedTarga
            b.cardCar.setCardBackgroundColor(b.root.context.getColor(
                if (sel) R.color.selected_item_background else R.color.card_background))
            b.cardCar.strokeColor = b.root.context.getColor(
                if (sel) R.color.primary else R.color.card_stroke)
            b.cardCar.strokeWidth = if (sel) 6 else 2
            b.root.setOnClickListener { onCarClick(car) }
            b.root.setOnLongClickListener { onCarLongClick(car, it); true }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Car>() {
            override fun areItemsTheSame(a: Car, b: Car) = a.targa == b.targa
            override fun areContentsTheSame(a: Car, b: Car) = a == b
        }
    }
}
