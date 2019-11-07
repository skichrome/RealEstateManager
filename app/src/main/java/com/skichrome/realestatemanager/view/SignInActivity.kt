package com.skichrome.realestatemanager.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.databinding.ActivitySigninBinding
import com.skichrome.realestatemanager.model.database.Agent
import com.skichrome.realestatemanager.view.ui.LoginUsersAdapter
import com.skichrome.realestatemanager.viewmodel.Injection
import com.skichrome.realestatemanager.viewmodel.SignInViewModel
import java.lang.ref.WeakReference

class SignInActivity : AppCompatActivity(), LoginUsersAdapter.LoginSelectedListener
{
    // =======================================
    //                  Fields
    // =======================================

    private lateinit var binding: ActivitySigninBinding
    private lateinit var viewModel: SignInViewModel
    private val adapter = LoginUsersAdapter(emptyList(), WeakReference(this))

    // =======================================
    //           Superclass Methods
    // =======================================

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        configureBinding()
    }

    // =======================================
    //          Configuration Methods
    // =======================================

    private fun configureBinding()
    {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signin)
        configureBtnListener()
        configureRecyclerView()
        configureSyncViewModel()
        binding.viewModel = viewModel
        binding.executePendingBindings()
    }

    private fun configureBtnListener()
    {
        binding.activitySignInLogin.setOnClickListener {
            val newName = binding.activitySignInUsername.text?.toString() ?: ""

            if (newName != "")
                viewModel.createAgent(newName)
            else
                Toast.makeText(this, getString(R.string.activity_sign_in_toast_bad_username), Toast.LENGTH_SHORT).show()
        }
    }

    private fun configureRecyclerView()
    {
        binding.activitySignInRecyclerView.setHasFixedSize(true)
        binding.activitySignInRecyclerView.adapter = adapter
    }

    private fun configureSyncViewModel()
    {
        viewModel = ViewModelProviders.of(this, Injection.provideSignInViewModelFactory(this)).get(SignInViewModel::class.java)
        viewModel.availableUsers.observe(this, Observer { it?.let { list -> adapter.replaceAgentList(list) } })
        viewModel.newAgentInserted.observe(this, Observer {
            it?.let { agentInserted ->
                saveCurrentUser(agentInserted)
                finishPositive()
            }
        })
    }

    // =======================================
    //                 Methods
    // =======================================

    override fun onUserSelected(user: Agent)
    {
        saveCurrentUser(user)
        finishPositive()
    }

    private fun saveCurrentUser(agent: Agent)
    {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPrefs.edit().putLong(getString(R.string.settings_fragment_username_key), agent.agentId).apply()
    }

    private fun finishPositive()
    {
        setResult(RESULT_OK)
        finish()
    }
}
