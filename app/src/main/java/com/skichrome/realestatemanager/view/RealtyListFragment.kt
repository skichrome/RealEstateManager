package com.skichrome.realestatemanager.view

import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.FragmentRealtyListBinding
import com.skichrome.realestatemanager.view.base.BaseFragment
import com.skichrome.realestatemanager.view.ui.RealtyListAdapter
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel
import kotlinx.android.synthetic.main.toolbar.*
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
        configureTabletNavController()
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
        binding.realtyListFragmentRecyclerView.setHasFixedSize(true)
        binding.realtyListFragmentRecyclerView.adapter = adapter
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
            val bundle = bundleOf("realtyId" to id)
            navigate(R.id.detailsRealtyFragmentTablet, bundle)
        }
            ?: findNavController().apply {
                val options = RealtyListFragmentDirections.actionRealtyListFragmentToDetailsRealtyFragment(id)
                navigate(options)
            }
    }
}