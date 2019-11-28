package com.skichrome.realestatemanager.view

import android.os.Bundle
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.utils.*
import com.skichrome.realestatemanager.view.base.BaseClassicFragment
import com.skichrome.realestatemanager.view.ui.CheckboxAdapter
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : BaseClassicFragment<RealtyViewModel>(), DatePickerDialogFragment.DatePickerListener
{
    // =======================================
    //                  Fields
    // =======================================

    private lateinit var adapter: CheckboxAdapter

    private var isSold: Boolean? = null
    private var creationDateSearch: Calendar? = null
    private var soldDateSearch: Calendar? = null

    private var minPrice = PRICE_MIN_VALUE
    private var maxPrice = -1
    private var minSurface = SURFACE_MIN_VALUE
    private var maxSurface = -1
    private var minPictures = 0
    private var maxPictures = -1

    companion object
    {
        private const val DEFAULT_MAX_PRICE = "10000000"
        private const val DEFAULT_MAX_SURFACE = "1000"
        private const val DEFAULT_MAX_PICTURES = "10"
    }

    // =======================================
    //           Superclass Methods
    // =======================================

    override fun getFragmentLayout(): Int = R.layout.fragment_search
    override fun getViewModelClass(): Class<RealtyViewModel> = RealtyViewModel::class.java

    override fun configureFragment()
    {
        getDefaultValuesFromSharedPreferences()
        configureFeedbackFields()

        configurePriceSeekBars()
        configureSurfaceSeekBars()
        configureMediaRefProgressBar()
        configureDateFields()

        configureRecyclerView()
        configureViewModel()

        searchFragmentSearchBtn.setOnClickListener { fetchQueryParameters() }
    }

    override fun onDestroy()
    {
        searchFragmentPoiRecyclerView?.adapter = null
        super.onDestroy()
    }

    // =======================================
    //                 Methods
    // =======================================

    // ------------ UI ------------

    private fun getDefaultValuesFromSharedPreferences()
    {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        maxPrice = sharedPrefs.getString(getString(R.string.settings_fragment_search_param_price_key), DEFAULT_MAX_PRICE)?.toInt()
            ?: DEFAULT_MAX_PRICE.toInt()
        maxSurface = sharedPrefs.getString(getString(R.string.settings_fragment_search_param_surface_key), DEFAULT_MAX_SURFACE)?.toInt()
            ?: DEFAULT_MAX_SURFACE.toInt()
        maxPictures = sharedPrefs.getString(getString(R.string.settings_fragment_search_param_pictures_key), DEFAULT_MAX_PICTURES)?.toInt()
            ?: DEFAULT_MAX_PICTURES.toInt()
    }

    private fun configureFeedbackFields()
    {
        searchFragmentMinPriceValue.text = getString(R.string.search_fragment_price_value, PRICE_MIN_VALUE)
        searchFragmentMaxPriceValue.text = getString(R.string.search_fragment_price_value, maxPrice)
        searchFragmentMinSurfaceValue.text = getString(R.string.search_fragment_surface_value, SURFACE_MIN_VALUE)
        searchFragmentMaxSurfaceValue.text = getString(R.string.search_fragment_surface_value, maxSurface)
        searchFragmentMaxPictureValue.text = "$minPictures"
    }

    private fun configurePriceSeekBars()
    {
        searchFragmentMinPriceSeekBar.max = maxPrice
        searchFragmentMaxPriceSeekBar.max = maxPrice
        searchFragmentMaxPriceSeekBar.progress = maxPrice

        searchFragmentMinPriceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                searchFragmentMinPriceValue.text = getString(R.string.search_fragment_price_value, progress)
                minPrice = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        searchFragmentMaxPriceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                searchFragmentMinPriceSeekBar.max = progress
                searchFragmentMaxPriceValue.text = getString(R.string.search_fragment_price_value, progress)
                maxPrice = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }

    private fun configureSurfaceSeekBars()
    {
        searchFragmentMinSurfaceSeekBar.max = maxSurface
        searchFragmentMaxSurfaceSeekBar.max = maxSurface
        searchFragmentMaxSurfaceSeekBar.progress = maxSurface

        searchFragmentMinSurfaceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                searchFragmentMinSurfaceValue.text = getString(R.string.search_fragment_surface_value, progress)
                minSurface = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        searchFragmentMaxSurfaceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                searchFragmentMinSurfaceSeekBar.max = progress
                searchFragmentMaxSurfaceValue.text = getString(R.string.search_fragment_surface_value, progress)
                maxSurface = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }

    private fun configureMediaRefProgressBar()
    {
        searchFragmentMaxPictureSeekBar.max = maxPictures

        searchFragmentMaxPictureSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                minPictures = progress
                searchFragmentMaxPictureValue.text = "$progress"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) = Unit
            override fun onStopTrackingTouch(p0: SeekBar?) = Unit
        })
    }

    private fun configureDateFields()
    {
        searchFragmentStatusSwitch.setOnClickListener { isSold = it.isActivated }
        searchFragmentDateCreatedEditText.setOnClickListener { showDateDialogFragment(CREATION_DATE_SEARCH_TAG) }
        searchFragmentDateSoldEditText.setOnClickListener { showDateDialogFragment(SOLD_DATE_SEARCH_TAG) }
    }

    private fun configureRecyclerView()
    {
        adapter = CheckboxAdapter(context)
        searchFragmentPoiRecyclerView.adapter = adapter
    }

    private fun configureViewModel()
    {
        viewModel.poi.observe(this, Observer { it?.let { list -> adapter.replaceCheckboxData(list) } })
    }

    // ------------ Action ------------

    private fun fetchQueryParameters()
    {
        viewModel.getRealtyMatchingParams(
            minPrice = if (minPrice == PRICE_MIN_VALUE) null else minPrice,
            maxPrice = if (maxPrice == DEFAULT_MAX_PRICE.toInt()) null else maxPrice,
            poiList = if (adapter.getSelectedId().isEmpty()) null else adapter.getSelectedId(),
            minSurface = if (minSurface == SURFACE_MIN_VALUE) null else minSurface,
            maxSurface = if (maxSurface == DEFAULT_MAX_SURFACE.toInt()) null else maxSurface,
            isSold = isSold?.let { if (it) 0 else 1 },
            creationDate = creationDateSearch?.timeInMillis,
            soldDate = soldDateSearch?.timeInMillis,
            mediaRefMinNumber = if (minPictures == 0) null else minPictures
        )
        navigateToFragmentList()
    }

    private fun navigateToFragmentList()
    {
        val arg = Bundle()
        arg.putBoolean(ARG_LIST_REALTY_ORIGIN, false)
        findNavController().setGraph(findNavController().graph, arg)
        findNavController().navigateUp()
    }

    private fun showDateDialogFragment(tag: Int)
    {
        val dialogFragment = DatePickerDialogFragment(WeakReference(this), tag, Calendar.getInstance())
        dialogFragment.show(fragmentManager!!, DATE_DIALOG_TAG)
    }

    // =================================
    //            Callbacks
    // =================================

    override fun onDateSet(calendar: Calendar, tag: Int)
    {
        val date = SimpleDateFormat.getDateInstance()
        val timeStr = date.format(calendar.time)
        when (tag)
        {
            CREATION_DATE_SEARCH_TAG ->
            {
                creationDateSearch = calendar
                searchFragmentDateCreatedEditText.setText(timeStr)
            }
            SOLD_DATE_SEARCH_TAG ->
            {
                soldDateSearch = calendar
                searchFragmentDateSoldEditText.setText(timeStr)
            }
        }
    }
}