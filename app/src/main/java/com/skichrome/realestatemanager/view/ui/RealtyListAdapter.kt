package com.skichrome.realestatemanager.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.RealtyListRvItemBinding
import com.skichrome.realestatemanager.model.database.Realty
import java.lang.ref.WeakReference

class RealtyListAdapter(private var realtyList: List<Realty> = listOf(), private val callback: WeakReference<RealtyItemListener>) :
    RecyclerView.Adapter<RealtyListAdapter.RealtyListViewHolder>()
{
    private lateinit var binding: RealtyListRvItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealtyListViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(inflater, R.layout.realty_list_rv_item, parent, false)
        return RealtyListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RealtyListViewHolder, position: Int) = holder.bind(realtyList[position], callback)

    override fun getItemCount(): Int = realtyList.size

    fun replaceRealtyList(list: List<Realty>) = list.let {
        realtyList = list
        notifyDataSetChanged()
    }

    class RealtyListViewHolder(private val binding: RealtyListRvItemBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: Realty, callback: WeakReference<RealtyItemListener>)
        {
            binding.realty = item
            binding.root.setOnClickListener { callback.get()?.onClickRealty(item.id) }
            binding.executePendingBindings()
        }
    }

    interface RealtyItemListener
    {
        fun onClickRealty(id: Long)
    }
}