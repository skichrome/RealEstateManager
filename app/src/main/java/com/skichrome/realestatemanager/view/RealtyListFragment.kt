package com.skichrome.realestatemanager.view

import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.FragmentRealtyListBinding
import com.skichrome.realestatemanager.model.database.Realty
import com.skichrome.realestatemanager.model.database.minimalobj.RealtyPreviewExtras
import com.skichrome.realestatemanager.utils.ARG_DETAILS_REALTY_NAME
import com.skichrome.realestatemanager.utils.ARG_LIST_REALTY_ORIGIN
import com.skichrome.realestatemanager.utils.AutoClearedValue
import com.skichrome.realestatemanager.view.base.BaseFragment
import com.skichrome.realestatemanager.view.ui.RealtyListAdapter
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel
import kotlinx.android.synthetic.main.fragment_realty_list.*
import kotlinx.android.synthetic.main.toolbar.*
import java.lang.ref.WeakReference

class RealtyListFragment :
    BaseFragment<FragmentRealtyListBinding, RealtyViewModel>(),
    RealtyListAdapter.RealtyItemListener
{
    private var adapter by AutoClearedValue<RealtyListAdapter>()

    override fun getFragmentLayout(): Int = R.layout.fragment_realty_list
    override fun getViewModelClass(): Class<RealtyViewModel> = RealtyViewModel::class.java

    override fun configureFragment()
    {
        configureTabletNavController()
        configureRecyclerView()
        configureViewModel()
        configureSwipeRefreshLayout()
        configureFabAddRealty()
    }

    override fun onResume()
    {
        super.onResume()
        getArgumentsFromBundle()
    }

    // =================================
    //              Methods
    // =================================

    private fun getArgumentsFromBundle()
    {
        arguments?.let {
            val origin = it.getBoolean(ARG_LIST_REALTY_ORIGIN, false)
            if (origin)
                viewModel.getAllRealty()
        }
    }

    private fun configureViewModel()
    {
        binding.realtyViewModel = viewModel
        viewModel.realEstates.observe(this, Observer { nullableRealtyList ->
            nullableRealtyList?.let { realtyList ->
                viewModel.realtyPreviewExtras.observe(this, Observer { nullableMediaRefList ->
                    nullableMediaRefList?.let { mediaRefList ->

                        val pairList = mutableListOf<Pair<Realty, RealtyPreviewExtras?>>()

                        realtyList.forEachIndexed { index, realty ->
                            val linkedMediaRef = mediaRefList[index]
                            pairList.add(Pair(realty, linkedMediaRef))
                        }
                        adapter.replaceRealtyList(pairList)
                    }
                })

            }
        })
    }

    private fun configureRecyclerView()
    {
        adapter = RealtyListAdapter(callback = WeakReference(this))
        binding.realtyListFragmentRecyclerView.setHasFixedSize(true)
        binding.realtyListFragmentRecyclerView.adapter = adapter
    }

    private fun configureSwipeRefreshLayout()
    {
        realtyListFragmentSwipeRefresh?.setOnRefreshListener { viewModel.getAllRealty() }
    }

    private fun configureFabAddRealty()
    {
        realtyListFragmentFab.setOnClickListener {
            findNavController().navigate(R.id.action_realtyListFragment_to_addRealtyFragment)
        }
    }

    private fun configureTabletNavController()
    {
        childFragmentManager.findFragmentById(R.id.fragmentRealtyListNavHostFragmentTablet)
            ?.findNavController()
            ?.apply {
                addOnDestinationChangedListener { _, destination, _ ->

                    activity?.toolbar?.menu
                        ?.findItem(R.id.action_detailsRealtyFragment_to_addRealtyFragment)
                        ?.isVisible = destination.id != R.id.emptyFragment
                }

                activity?.toolbar?.menu?.findItem(R.id.action_detailsRealtyFragment_to_addRealtyFragment)
                    ?.setOnMenuItemClickListener {
                        val options = RealtyListFragmentDirections.actionRealtyListFragmentToAddRealtyFragment(true)
                        activity?.findNavController(R.id.activityMainMainNavHostFragment)
                            ?.navigate(options)
                        return@setOnMenuItemClickListener true
                    }
            }

    }

    // =================================
    //            Callbacks
    // =================================

    override fun onClickRealty(id: Long)
    {
        val navHostFragmentTablet = childFragmentManager.findFragmentById(R.id.fragmentRealtyListNavHostFragmentTablet)
        navHostFragmentTablet?.findNavController()?.apply {
            val bundle = bundleOf(ARG_DETAILS_REALTY_NAME to id)
            navigate(R.id.detailsRealtyFragmentTablet, bundle)
        }
            ?: findNavController().apply {
                val options = RealtyListFragmentDirections.actionRealtyListFragmentToDetailsRealtyFragment(id)
                navigate(options)
            }
    }
}