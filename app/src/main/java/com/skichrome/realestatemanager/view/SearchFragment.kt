package com.skichrome.realestatemanager.view

import android.os.Bundle
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.utils.ARG_LIST_REALTY_ORIGIN
import com.skichrome.realestatemanager.utils.SEARCH_MAX_VALUE
import com.skichrome.realestatemanager.utils.SEARCH_MIN_VALUE
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
    private var minPrice = SEARCH_MIN_VALUE
    private var maxPrice = SEARCH_MAX_VALUE

    // =======================================
    //           Superclass Methods
    // =======================================

    override fun getFragmentLayout(): Int = R.layout.fragment_search
    override fun getViewModelClass(): Class<RealtyViewModel> = RealtyViewModel::class.java

    override fun configureFragment()
    {
        configureFeedbackFields()
        configureSeekBars()

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
    private fun configureFeedbackFields()
    {
        searchFragmentMinPriceValue.text = getString(R.string.search_fragment_min_price_value, SEARCH_MIN_VALUE)
        searchFragmentMaxPriceValue.text = getString(R.string.search_fragment_max_price_value, SEARCH_MAX_VALUE)
    }

    private fun configureSeekBars()
    {
        searchFragmentMinPriceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener
        {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean)
            {
                searchFragmentMinPriceValue.text = getString(R.string.search_fragment_min_price_value, progress)
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
                searchFragmentMaxPriceValue.text = getString(R.string.search_fragment_max_price_value, progress)
                maxPrice = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
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
            if (minPrice == SEARCH_MIN_VALUE) null else minPrice,
            if (maxPrice == SEARCH_MAX_VALUE) null else maxPrice,
            if (adapter.getSelectedId().isEmpty()) null else adapter.getSelectedId()
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