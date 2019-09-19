package com.skichrome.realestatemanager.view

import android.util.DisplayMetrics
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.FragmentRealtyListBinding
import com.skichrome.realestatemanager.viewmodel.Injection
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel
import com.skichrome.realestatemanager.viewmodel.ViewModelFactory
import java.lang.ref.WeakReference

class RealtyListFragment : BaseFragment<FragmentRealtyListBinding, RealtyViewModel, ViewModelFactory>(), RealtyListAdapter.RealtyItemListener
{
    private val adapter = RealtyListAdapter(callback = WeakReference(this))

    override fun getFragmentLayout(): Int = R.layout.fragment_realty_list

    override fun getViewModelClass(): Class<RealtyViewModel> = RealtyViewModel::class.java

    override fun getInjection(): ViewModelFactory = Injection.provideViewModelFactory(context!!)

    override fun configureFragment()
    {
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

    // =================================
    //            Callbacks
    // =================================

    override fun onClickRealty(id: Long)
    {
        val navOptions = RealtyListFragmentDirections.actionRealtyListFragmentToDetailsRealtyFragment(id)
        findNavController().navigate(navOptions)
    }
}