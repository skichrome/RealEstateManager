package com.skichrome.realestatemanager.view

import android.view.View
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.FragmentRealtyDetailsBinding
import com.skichrome.realestatemanager.utils.ARG_DETAILS_REALTY_NAME
import com.skichrome.realestatemanager.utils.AutoClearedValue
import com.skichrome.realestatemanager.utils.SHARED_PREFS_DOLLARS_CONV_RATE_KEY
import com.skichrome.realestatemanager.utils.SHARED_PREFS_EURO_CONV_RATE_KEY
import com.skichrome.realestatemanager.view.base.BaseMapFragment
import com.skichrome.realestatemanager.view.ui.CheckboxAdapter
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

    private var photoAdapter by AutoClearedValue<RealtyPhotoAdapter>()
    private var poiAdapter by AutoClearedValue<CheckboxAdapter>()

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_realty_details
    override fun getViewModelClass(): Class<RealtyViewModel> = RealtyViewModel::class.java
    override fun getMap(): MapView = realtyDetailsFragmentMapView

    override fun configureFragment()
    {
        super.configureFragment()
        getBundleArgs()
        configureRecyclerView()
    }

    override fun onMapReady(gMap: GoogleMap)
    {
        super.onMapReady(gMap)
        configureMapPosition()
    }

    // =================================
    //              Methods
    // =================================

    private fun getBundleArgs()
    {
        arguments?.let {
            var realtyId = DetailsRealtyFragmentArgs.fromBundle(it).realtyId

            if (realtyId == -1L)
                realtyId = it.getLong(ARG_DETAILS_REALTY_NAME)

            if (realtyId == -1L)
                throw Exception("Something went wrong during DetailsRealtyFragment initialisation")
            configureViewModel(realtyId)
        }
    }

    // --------------- UI --------------

    private fun configureViewModel(realtyToDetail: Long)
    {
        viewModel.getRealty(realtyToDetail)
        binding.realtyViewModel = viewModel
        viewModel.realtyDetailedLoading.observe(this, Observer {
            it?.let { isLoading ->
                if (!isLoading)
                {
                    configureDateTextView()
                    configurePriceTextView()
                }
            }
        })
        viewModel.poi.observe(this, Observer { it?.let { list -> poiAdapter.replaceCheckboxData(list) } })
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

    private fun configurePriceTextView()
    {
        viewModel.realtyDetailed.get()?.let {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)

            realtyDetailsFragmentPrice.text =
                when (prefs.getString(getString(R.string.settings_fragment_display_currency_key), "0")?.toIntOrNull() ?: 0)
                {
                    1 ->
                        if (it.currency == 0) "${prefs.getFloat(SHARED_PREFS_EURO_CONV_RATE_KEY, 1f) * it.price} $" else "${it.price} $"
                    else ->
                        if (it.currency == 1) "${prefs.getFloat(SHARED_PREFS_DOLLARS_CONV_RATE_KEY, 1f) * it.price} €" else "${it.price} €"
                }
        }
    }

    private fun configureRecyclerView()
    {
        photoAdapter = RealtyPhotoAdapter(list = mutableListOf(), callback = WeakReference(this))
        binding.realtyDetailsFragmentRecyclerViewPhoto.adapter = photoAdapter
        viewModel.realtyDetailedPhotos.observe(this, Observer {
            it?.let { list ->
                photoAdapter.replacePhotoList(list)
                binding.realtyDetailsFragmentNoMediaAvailable.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        })
        poiAdapter = CheckboxAdapter(context)
        binding.realtyDetailsFragmentRecyclerViewPoi.adapter = poiAdapter
        viewModel.poiRealty.observe(this, Observer { it?.let { list -> poiAdapter.setCheckboxesCheckedAndDeleteUnchecked(list) } })
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