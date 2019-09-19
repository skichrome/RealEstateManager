package com.skichrome.realestatemanager.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

abstract class BaseFragment<T : ViewDataBinding, V : ViewModel, F : ViewModelProvider.Factory> : Fragment()
{
    // =================================
    //              Fields
    // =================================

    private lateinit var _binding: T
    protected val binding: T
        get() = _binding

    private lateinit var _viewModel: V
    protected val viewModel: V
        get() = _viewModel

    abstract fun getFragmentLayout(): Int
    abstract fun getViewModelClass(): Class<V>
    abstract fun getInjection(): F
    abstract fun configureFragment()

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        _binding = DataBindingUtil.inflate(layoutInflater, getFragmentLayout(), container, false)
        _binding.executePendingBindings()
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        configureViewModel()
        configureFragment()
    }

    private fun configureViewModel()
    {
        _viewModel = ViewModelProviders.of(this, getInjection()).get(getViewModelClass())
    }
}