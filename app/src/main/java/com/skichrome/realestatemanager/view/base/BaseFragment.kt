package com.skichrome.realestatemanager.view.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.skichrome.realestatemanager.viewmodel.Injection

abstract class BaseFragment<T : ViewDataBinding, V : ViewModel> : Fragment()
{
    // =================================
    //              Fields
    // =================================

    private var _binding: T? = null
    protected val binding: T?
        get() = _binding

    private lateinit var _sharedRealtyViewModel: V
    protected val viewModel: V
        get() = _sharedRealtyViewModel

    abstract fun getFragmentLayout(): Int
    abstract fun getViewModelClass(): Class<V>
    abstract fun configureFragment()

    // =================================
    //        Superclass Methods
    // =================================

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        _binding = DataBindingUtil.inflate(layoutInflater, getFragmentLayout(), container, false)
        _binding?.executePendingBindings()
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        configureViewModel()
        configureFragment()
    }

    private fun configureViewModel()
    {
        _sharedRealtyViewModel = activity?.run {
            ViewModelProviders.of(this, Injection.provideViewModelFactory(context!!)).get(getViewModelClass())
        } ?: throw Exception("Invalid activity")
    }

    override fun onDestroy()
    {
        binding?.unbind()
        super.onDestroy()
    }
}