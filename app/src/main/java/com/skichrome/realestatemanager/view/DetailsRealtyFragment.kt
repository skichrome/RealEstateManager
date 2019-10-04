package com.skichrome.realestatemanager.view

import androidx.lifecycle.Observer
import com.google.android.gms.maps.MapView
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.FragmentRealtyDetailsBinding
import com.skichrome.realestatemanager.view.base.BaseMapFragment
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel
import java.lang.ref.WeakReference

class DetailsRealtyFragment : BaseMapFragment<FragmentRealtyDetailsBinding, RealtyViewModel>(),
    RealtyPhotoAdapter.OnClickPictureListener
{
    // =================================
    //              Fields
    // =================================

    private val photoAdapter = RealtyPhotoAdapter(list = mutableListOf(), callback = WeakReference(this))

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_realty_details
    override fun getViewModelClass(): Class<RealtyViewModel> = RealtyViewModel::class.java
    override fun getMap(): MapView = binding.realtyDetailsFragmentMapView

    override fun configureFragment()
    {
        configureViewModel()
        configureRecyclerView()
    }

    // =================================
    //              Methods
    // =================================

    // --------------- UI --------------

    private fun configureViewModel()
    {
        binding.realtyViewModel = viewModel
    }

    private fun configureRecyclerView()
    {
        binding.realtyDetailsFragmentRecyclerView.adapter = photoAdapter
        viewModel.realtyDetailedPhotos.observe(this, Observer { it?.let { list -> photoAdapter.replacePhotoList(list) } })
    }

    // =================================
    //            Callbacks
    // =================================

    override fun onClickAddPicture() = Unit
    override fun onLongClickPicture(position: Int) = Unit
}