package com.skichrome.realestatemanager.view

import android.os.Bundle
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.utils.*
import com.skichrome.realestatemanager.view.base.BaseClassicFragment
import com.skichrome.realestatemanager.view.ui.CheckboxAdapter
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseClassicFragment<RealtyViewModel>()
{
    // =======================================
    //                  Fields
    // =======================================

    private lateinit var adapter: CheckboxAdapter
    private var minPrice = PRICE_MIN_VALUE
    private var maxPrice = PRICE_MAX_VALUE
    private var minSurface = SURFACE_MIN_VALUE
    private var maxSurface = SURFACE_MAX_VALUE

    // =======================================
    //           Superclass Methods
    // =======================================

    override fun getFragmentLayout(): Int = R.layout.fragment_search
    override fun getViewModelClass(): Class<RealtyViewModel> = RealtyViewModel::class.java

    override fun configureFragment()
    {
        configureFeedbackFields()
        configurePriceSeekBars()
        configureSurfaceSeekBars()

        configureRecyclerView()
        configureViewModel()

        searchFragmentSearchFab.setOnClickListener { fetchQueryParameters() }
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

    private fun configureFeedbackFields()
    {
        searchFragmentMinPriceValue.text = getString(R.string.search_fragment_price_value, PRICE_MIN_VALUE)
        searchFragmentMaxPriceValue.text = getString(R.string.search_fragment_price_value, PRICE_MAX_VALUE)
        searchFragmentMinSurfaceValue.text = getString(R.string.search_fragment_surface_value, SURFACE_MIN_VALUE)
        searchFragmentMaxSurfaceValue.text = getString(R.string.search_fragment_surface_value, SURFACE_MAX_VALUE)
    }

    private fun configurePriceSeekBars()
    {
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
        searchFragmentMaxPriceSeekBar.max = PRICE_MAX_VALUE
        searchFragmentMaxPriceSeekBar.progress = PRICE_MAX_VALUE
    }

    private fun configureSurfaceSeekBars()
    {
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
        searchFragmentMaxSurfaceSeekBar.max = SURFACE_MAX_VALUE
        searchFragmentMaxSurfaceSeekBar.progress = SURFACE_MAX_VALUE
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
            if (minPrice == PRICE_MIN_VALUE) null else minPrice,
            if (maxPrice == PRICE_MAX_VALUE) null else maxPrice,
            if (adapter.getSelectedId().isEmpty()) null else adapter.getSelectedId(),
            if (minSurface == SURFACE_MIN_VALUE) null else minSurface,
            if (maxSurface == SURFACE_MAX_VALUE) null else maxSurface

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
}