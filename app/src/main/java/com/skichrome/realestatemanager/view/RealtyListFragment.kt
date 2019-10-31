package com.skichrome.realestatemanager.view

import android.app.AlertDialog
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputEditText
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.FragmentRealtyListBinding
import com.skichrome.realestatemanager.model.database.Agent
import com.skichrome.realestatemanager.view.base.BaseFragment
import com.skichrome.realestatemanager.viewmodel.RealtyViewModel
import java.lang.ref.WeakReference

class RealtyListFragment :
    BaseFragment<FragmentRealtyListBinding, RealtyViewModel>(),
    RealtyListAdapter.RealtyItemListener
{
    private val adapter = RealtyListAdapter(callback = WeakReference(this))
    private var currentAgent: Long = -1L

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
        viewModel.agent.observe(this, Observer {
            it?.let { list ->
                if (getCurrentUser() == -1L)
                    askToSetAgent(list)
            }
        })
    }

    private fun configureRecyclerView()
    {
        binding.realtyListFragmentRecyclerView.setHasFixedSize(true)
        binding.realtyListFragmentRecyclerView.adapter = adapter
    }

    private fun getCurrentUser(): Long
    {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPrefs.getLong(getString(R.string.settings_fragment_username_key), -1L)
    }

    private fun saveCurrentUser()
    {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPrefs.edit().putLong(getString(R.string.settings_fragment_username_key), currentAgent).apply()
    }

    private fun askToSetAgent(availableAgents: List<Agent>)
    {
        AlertDialog.Builder(context).apply {
            val alertInflater = View.inflate(context, R.layout.alert_dialog_new_agent, null)
            setView(alertInflater)
            alertInflater.findViewById<TextInputEditText>(R.id.alertDialogNewAgentEditText)

            val arrayAdapter = ArrayAdapter<String>(context!!, android.R.layout.select_dialog_singlechoice)
            availableAgents.forEach {
                arrayAdapter.add(it.name)
            }

            setTitle(getString(R.string.current_user_actions_alert_dialog_title))

            setAdapter(arrayAdapter) { _, which ->
                currentAgent = which.toLong() + 1
                saveCurrentUser()
            }

            setPositiveButton(getString(R.string.current_user_actions_alert_dialog_positive_btn)) { _, _ ->
                val alertEditText = alertInflater.findViewById<EditText>(R.id.alertDialogNewAgentEditText)
                val selectedText = alertEditText.text?.toString() ?: ""
                if (selectedText != "")
                {
                    if (selectedText != "")
                    {
                        currentAgent = availableAgents.size.toLong() + 1
                        val agent = Agent(currentAgent, selectedText)
                        viewModel.createNewAgent(agent)
                        saveCurrentUser()
                    }
                }
            }
        }.show()
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