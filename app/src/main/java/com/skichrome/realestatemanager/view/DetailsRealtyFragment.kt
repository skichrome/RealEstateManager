package com.skichrome.realestatemanager.view

import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.FragmentRealtyDetailsBinding
import com.skichrome.realestatemanager.view.base.BaseMapFragment
import com.skichrome.realestatemanager.view.ui.RealtyPhotoAdapter
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel
import kotlinx.android.synthetic.main.fragment_realty_details.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat

class DetailsRealtyFragment : BaseMapFragment<FragmentRealtyDetailsBinding, RealtyViewModel>(),
    RealtyPhotoAdapter.OnClickPictureListener
{
    // =================================
    //              Fields
    // =================================

    private lateinit var photoAdapter: RealtyPhotoAdapter

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_realty_details
    override fun getViewModelClass(): Class<RealtyViewModel> = RealtyViewModel::class.java
    override fun getMap(): MapView = binding.realtyDetailsFragmentMapView

    override fun configureFragment()
    {
        getBundleArgs()
        configureRecyclerView()
    }

    override fun onMapReady(gMap: GoogleMap?)
    {
        super.onMapReady(gMap)
        configureMapPosition()
    }

    override fun onDestroy()
    {
        binding.realtyDetailsFragmentRecyclerView.adapter = null
        super.onDestroy()
    }

    // =================================
    //              Methods
    // =================================

    private fun getBundleArgs()
    {
        arguments?.let {
            var realtyId = DetailsRealtyFragmentArgs.fromBundle(it).realtyId

            if (realtyId == -1L)
                realtyId = it.getLong("realtyId")

            if (realtyId == -1L)
                throw Exception("Something went wrong during DetailsRealtyFragment initialisation")
            configureViewModel(realtyId)
        }
    }

    // --------------- UI --------------

    private fun configureViewModel(realtyToDetail: Long)
    {
        viewModel.getRealty(realtyToDetail)
        viewModel.realtyDetailedLoading.observe(this, Observer {
            it?.let { isLoading ->
                if (!isLoading)
                    configureDateTextView()
            }
        })
        binding.realtyViewModel = viewModel
    }

    private fun configureDateTextView()
    {
        val date = SimpleDateFormat.getDateInstance()
        val realtyDetailed = viewModel.realtyDetailed.get()
        realtyDetailed?.let {
            val dateLong = if (it.status) it.dateSell else it.dateAdded
            realtyDetailsFragmentDate.text = date.format(dateLong)

            realtyDetailsFragmentDateTitle.text = if (it.status) getString(R.string.realty_details_fragment_date_sold)
            else getString(R.string.realty_details_fragment_date_created)

            realtyDetailsFragmentStatus.text = if (it.status) getString(R.string.realty_details_fragment_status_sold)
            else getString(R.string.realty_details_fragment_status_available)
        }
    }

    private fun configureRecyclerView()
    {
        photoAdapter = RealtyPhotoAdapter(list = mutableListOf(), callback = WeakReference(this))
        binding.realtyDetailsFragmentRecyclerView.adapter = photoAdapter
        viewModel.realtyDetailedPhotos.observe(this, Observer { it?.let { list -> photoAdapter.replacePhotoList(list) } })
    }

    private fun configureMapPosition()
    {
        viewModel.realtyDetailedLatLng.observe(this, Observer {
            it?.let { realtyMinimal ->
                val latLng = LatLng(realtyMinimal.latitude, realtyMinimal.longitude)
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))

                map?.apply {
                    val markerOpt = MarkerOptions()
                        .position(latLng)
                    addMarker(markerOpt)
                }
            }
        })
    }

    // =================================
    //            Callbacks
    // =================================

    override fun onClickAddPicture() = Unit
    override fun onLongClickPicture(position: Int) = Unit
}