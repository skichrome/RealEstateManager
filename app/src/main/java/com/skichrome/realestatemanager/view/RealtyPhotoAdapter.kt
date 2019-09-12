package com.skichrome.realestatemanager.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.PhotoRvItemBinding
import java.lang.ref.WeakReference

class RealtyPhotoAdapter(var list: MutableList<String?> = mutableListOf(null), private val callback: WeakReference<OnClickPictureListener>) :
    RecyclerView.Adapter<RealtyPhotoAdapter.RealtyPhotoViewHolder>()
{
    private lateinit var binding: PhotoRvItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RealtyPhotoViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(inflater, R.layout.photo_rv_item, parent, false)
        return RealtyPhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RealtyPhotoViewHolder, position: Int) =
        holder.bind(list[position], callback)

    override fun getItemCount(): Int = list.size

    fun addPictureToAdapter(image: String)
    {
        list.add(image)
        notifyDataSetChanged()
    }

    fun removePictureFromAdapter(position: Int)
    {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    class RealtyPhotoViewHolder(private val binding: PhotoRvItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(imageLocation: String?, callback: WeakReference<OnClickPictureListener>)
        {
            if (imageLocation == null)
            {
                binding.photoRvItemImg.visibility = View.INVISIBLE
                Glide.with(binding.root).clear(binding.photoRvItemImg)
                binding.photoRvItemImgDefaultAdd.visibility = View.VISIBLE
                binding.photoRvItemImgDefaultAdd.setImageResource(R.drawable.ic_add_circle_24dp)
                binding.photoRvItemMaterialCardView.setOnClickListener { callback.get()?.onClickAddPicture() }
            } else
            {
                binding.photoRvItemImgDefaultAdd.visibility = View.GONE
                binding.photoRvItemImg.visibility = View.VISIBLE
                Glide.with(binding.root).load(imageLocation).into(binding.photoRvItemImg)

                binding.photoRvItemMaterialCardView.setOnLongClickListener {
                    callback.get()?.onLongClickPicture(adapterPosition)
                    return@setOnLongClickListener true
                }
            }
            binding.executePendingBindings()
        }
    }

    interface OnClickPictureListener
    {
        fun onClickAddPicture()
        fun onLongClickPicture(position: Int)
    }
}