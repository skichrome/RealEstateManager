package com.skichrome.realestatemanager.view

import androidx.lifecycle.Observer
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.FragmentRealtyDetailsBinding
import com.skichrome.realestatemanager.viewmodel.Injection
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel
import com.skichrome.realestatemanager.viewmodel.ViewModelFactory
import java.lang.ref.WeakReference

class DetailsRealtyFragment : BaseFragment<FragmentRealtyDetailsBinding, RealtyViewModel, ViewModelFactory>(),
    RealtyPhotoAdapter.OnClickPictureListener
{
    // =================================
    //              Fields
    // =================================

    private val photoAdapter = RealtyPhotoAdapter(list = mutableListOf(), callback = WeakReference(this))

    // =================================
    //        Superclass Methods
    // =================================

    override fun getFragmentLayout(): Int = R.layout.fragment_realty_details
    override fun getViewModelClass(): Class<RealtyViewModel> = RealtyViewModel::class.java
    override fun getInjection(): ViewModelFactory = Injection.provideViewModelFactory(context!!)

    override fun configureFragment()
    {
        configureViewModel()
        configureRecyclerView()
        getNavArguments()
    }

    // =================================
    //              Methods
    // =================================

    private fun getNavArguments()
    {
        arguments?.let {
            val realtyId = DetailsRealtyFragmentArgs.fromBundle(it).realtyId
            viewModel.getRealty(realtyId)
        }
    }

    // --------------- UI --------------

    private fun configureViewModel()
    {
        binding.realtyViewModel = viewModel
    }

    private fun configureRecyclerView()
    {
        binding.realtyDetailsFragmentRecyclerView.adapter = photoAdapter
        viewModel.realtyDetailedPhotos.observe(this, Observer { it?.let { list -> photoAdapter.replacePhotoList(list) } })
    }

    // =================================
    //            Callbacks
    // =================================

    override fun onClickAddPicture() = Unit
    override fun onLongClickPicture(position: Int) = Unit
}