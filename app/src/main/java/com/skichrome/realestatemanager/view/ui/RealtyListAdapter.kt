package com.skichrome.realestatemanager.view.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.RealtyListRvItemBinding
import com.skichrome.realestatemanager.model.database.Realty
import com.skichrome.realestatemanager.model.database.minimalobj.MediaReferencePreview
import java.lang.ref.WeakReference

class RealtyListAdapter(private var realtyList: List<Realty> = listOf(),
                        private var mediaRefList: List<MediaReferencePreview> = listOf(),
                        private val callback: WeakReference<RealtyItemListener>
) :
    RecyclerView.Adapter<RealtyListAdapter.RealtyListViewHolder>()
{
    private lateinit var binding: RealtyListRvItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealtyListViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(inflater, R.layout.realty_list_rv_item, parent, false)
        return RealtyListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RealtyListViewHolder, position: Int) = holder.bind(realtyList[position], mediaRefList[position], callback)

    override fun getItemCount(): Int = realtyList.size

    fun replaceRealtyList(list: List<Realty>) = list.let {
        realtyList = list
    }

    fun replaceMediaRefList(list: List<MediaReferencePreview>)
    {
        mediaRefList = list
        notifyDataSetChanged()
    }

    class RealtyListViewHolder(private val binding: RealtyListRvItemBinding) :
        RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: Realty, mediaRef: MediaReferencePreview, callback: WeakReference<RealtyItemListener>)
        {
            mediaRef.reference?.let {
                Glide.with(binding.root).load(it).circleCrop().into(binding.realtyPreviewImg)
            } ?: Glide.with(binding.root).load(R.drawable.ic_app_logo_default_realty).into(binding.realtyPreviewImg)

            item.realtyTypeId = item.realtyTypeId - 1
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