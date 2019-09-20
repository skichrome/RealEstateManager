package com.skichrome.realestatemanager.view

import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.FragmentRealtyListBinding
import com.skichrome.realestatemanager.view.base.BaseFragment
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel
import java.lang.ref.WeakReference

class RealtyListFragment :
    BaseFragment<FragmentRealtyListBinding, RealtyViewModel>(),
    RealtyListAdapter.RealtyItemListener
{
    private val adapter = RealtyListAdapter(callback = WeakReference(this))

    override fun getFragmentLayout(): Int = R.layout.fragment_realty_list

    override fun getViewModelClass(): Class<RealtyViewModel> = RealtyViewModel::class.java

    override fun configureFragment()
    {
        configureViewModel()
        configureRecyclerView()
    }

    override fun onResume()
    {
        super.onResume()
        viewModel.getAllRealty()
    }

    // =================================
    //              Methods
    // =================================

    private fun configureViewModel()
    {
        binding.realtyViewModel = viewModel
        viewModel.realEstates.observe(this, Observer { it?.let { list -> adapter.replaceRealtyList(list) } })
    }

    private fun configureRecyclerView()
    {
        val gridLayoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)

        binding.realtyListFragmentRecyclerView.setHasFixedSize(true)
        binding.realtyListFragmentRecyclerView.layoutManager = gridLayoutManager
        binding.realtyListFragmentRecyclerView.adapter = adapter

        val largePadding = resources.getDimension(R.dimen.preview_card_spacing).toInt()
        val smallPadding = resources.getDimension(R.dimen.preview_card_spacing_small).toInt()
        binding.realtyListFragmentRecyclerView.addItemDecoration(RvItemDecoration(largePadding, smallPadding))
    }

    // =================================
    //            Callbacks
    // =================================

    override fun onClickRealty(id: Long)
    {
        viewModel.getRealty(id)

        val navHostFragmentTablet = childFragmentManager.findFragmentById(R.id.fragmentRealtyListNavHostFragmentTablet)
        navHostFragmentTablet?.findNavController()?.navigate(R.id.detailsRealtyFragmentTablet)
            ?: findNavController().navigate(R.id.detailsRealtyFragment)
    }
}