package com.skichrome.realestatemanager.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.RealtyListRvItemBinding
import com.skichrome.realestatemanager.model.database.Realty
import com.skichrome.realestatemanager.model.database.minimalobj.RealtyPreviewExtras
import java.lang.ref.WeakReference

class RealtyListAdapter(private var realtyList: List<Pair<Realty, RealtyPreviewExtras?>> = listOf(),
                        private val callback: WeakReference<RealtyItemListener>,
                        private val userCurrency: Pair<Int, Float>
) :
    RecyclerView.Adapter<RealtyListAdapter.RealtyListViewHolder>()
{
    private lateinit var binding: RealtyListRvItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealtyListViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(inflater, R.layout.realty_list_rv_item, parent, false)
        return RealtyListViewHolder(binding, userCurrency)
    }

    override fun onBindViewHolder(holder: RealtyListViewHolder, position: Int) =
        holder.bind(realtyList[position].first, realtyList[position].second, callback)

    override fun getItemCount(): Int = realtyList.size

    fun replaceRealtyList(realtyAndMediaRefList: List<Pair<Realty, RealtyPreviewExtras?>>) = realtyAndMediaRefList.let {
        realtyList = realtyAndMediaRefList
        notifyDataSetChanged()
    }

    class RealtyListViewHolder(private val binding: RealtyListRvItemBinding, private val userCurrency: Pair<Int, Float>) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(realty: Realty, realtyPreviewExtras: RealtyPreviewExtras?, callback: WeakReference<RealtyItemListener>)
        {
            realtyPreviewExtras?.mediaUrl?.let {
                Glide.with(binding.root).load(it).circleCrop().into(binding.realtyPreviewImg)
            } ?: Glide.with(binding.root).load(R.drawable.ic_app_logo_default_realty).into(binding.realtyPreviewImg)

            val priceText = when (userCurrency.first)
            {
                1 ->
                {
                    if (realty.currency == 0)
                        "${userCurrency.second * realty.price} $"
                    else
                        "${realty.price} $"
                }
                else ->
                {
                    if (realty.currency == 1)
                        "${userCurrency.second * realty.price} €"
                    else
                        "${realty.price} €"
                }
            }
            binding.realtyListItemPrice.text = priceText

            binding.realtyType = realtyPreviewExtras?.realtyTypeName
            binding.realty = realty
            binding.root.setOnClickListener { callback.get()?.onClickRealty(realty.id) }
            binding.executePendingBindings()
        }
    }

    interface RealtyItemListener
    {
        fun onClickRealty(id: Long)
    }
}