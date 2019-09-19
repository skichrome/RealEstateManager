package com.skichrome.realestatemanager.view

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.FragmentRealtyListBinding
import com.skichrome.realestatemanager.viewmodel.Injection
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel

class RealtyListFragment : Fragment()
{
    private lateinit var binding: FragmentRealtyListBinding
    private lateinit var viewModel: RealtyViewModel
    private val adapter = RealtyListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_realty_list, container, false)
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val vmFactory = Injection.provideViewModelFactory(context!!)
        viewModel = ViewModelProviders.of(this, vmFactory).get(RealtyViewModel::class.java)
        binding.realtyViewModel = viewModel

        val displayMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val density = displayMetrics.density
        val width = displayMetrics.widthPixels
        val spanCount = if (width / density <= 600) 2 else 4

        val layoutManager = GridLayoutManager(context, spanCount, RecyclerView.VERTICAL, false)
        binding.realtyListFragmentRecyclerView.setHasFixedSize(true)
        binding.realtyListFragmentRecyclerView.layoutManager = layoutManager
        binding.realtyListFragmentRecyclerView.adapter = adapter

        val largePadding = resources.getDimension(R.dimen.preview_card_spacing).toInt()
        val smallPadding = resources.getDimension(R.dimen.preview_card_spacing_small).toInt()
        binding.realtyListFragmentRecyclerView.addItemDecoration(RvItemDecoration(largePadding, smallPadding))

        viewModel.realEstates.observe(this, Observer { it?.let { list -> adapter.replaceRealtyList(list) } })
    }

    override fun onResume()
    {
        super.onResume()
        viewModel.getAllRealty()
    }
}