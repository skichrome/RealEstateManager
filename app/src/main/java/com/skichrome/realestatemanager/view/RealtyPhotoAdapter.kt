package com.skichrome.realestatemanager.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.PhotoRvItemBinding
import com.skichrome.realestatemanager.model.database.MediaReference
import java.lang.ref.WeakReference

class RealtyPhotoAdapter(var list: MutableList<MediaReference?> = mutableListOf(null), private val callback: WeakReference<OnClickPictureListener>) :
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

    fun addPictureToAdapter(image: MediaReference)
    {
        list.add(image)
        notifyDataSetChanged()
    }

    fun removePictureFromAdapter(position: Int)
    {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getAllPicturesReferences(): List<MediaReference?> = list

    class RealtyPhotoViewHolder(private val binding: PhotoRvItemBinding) : RecyclerView.ViewHolder(binding.root)
    {
        fun bind(image: MediaReference?, callback: WeakReference<OnClickPictureListener>)
        {
            binding.isImgLocationNull = image == null

            if (image == null)
            {
                Glide.with(binding.root).clear(binding.photoRvItemImg)
                binding.photoRvItemMaterialCardView.setOnClickListener { callback.get()?.onClickAddPicture() }
            } else
            {
                Glide.with(binding.root).load(image.reference).into(binding.photoRvItemImg)

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