package com.skichrome.realestatemanager.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.RealtyListRvItemBinding
import com.skichrome.realestatemanager.model.database.Realty

class RealtyListAdapter(private var list: List<Realty> = listOf()) :
    RecyclerView.Adapter<RealtyListAdapter.RealtyListViewHolder>()
{
    private lateinit var binding: RealtyListRvItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealtyListViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(inflater, R.layout.realty_list_rv_item, parent, false)
        return RealtyListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RealtyListViewHolder, position: Int)
    {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun replaceData(newList: List<Realty>)
    {
        list = newList
        notifyDataSetChanged()
    }

    class RealtyListViewHolder(private val binding: RealtyListRvItemBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: Realty)
        {
            val price = "\$ ${item.price}"
            binding.realtyListItemPrice.text = price
            binding.realtyListItemName.text = item.agent
            binding.realtyListItemLocation.text = item.city
            binding.executePendingBindings()
        }
    }
}