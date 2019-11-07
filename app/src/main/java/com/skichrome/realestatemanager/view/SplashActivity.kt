package com.skichrome.realestatemanager.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import com.skichrome.realestatemanager.R
import com.skichrome.realestatemanager.utils.SIGN_IN_RC
import com.skichrome.realestatemanager.viewmodel.Injection
import com.skichrome.realestatemanager.viewmodel.OnlineSyncViewModel

class SplashActivity : AppCompatActivity()
{
    // =======================================
    //                  Fields
    // =======================================

    private lateinit var viewModel: OnlineSyncViewModel

    // =======================================
    //                 Methods
    // =======================================

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        configureSyncViewModel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RC)
        {
            if (resultCode == RESULT_OK)
                launchMainActivity()
            else
                finish()
        }
    }

    // =======================================
    //                 Methods
    // =======================================

    private fun configureSyncViewModel()
    {
        viewModel = ViewModelProviders.of(this, Injection.provideSyncViewModelFactory(this)).get(OnlineSyncViewModel::class.java)
        viewModel.isSyncEnded.observe(this, Observer {
            it?.let { isSyncEnded ->
                if (isSyncEnded)
                {
                    if (getCurrentUser() == -1L)
                        startLoginActivity()
                    else
                        launchMainActivity()
                }
            }
        })
    }


    private fun getCurrentUser(): Long
    {
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPrefs.getLong(getString(R.string.settings_fragment_username_key), -1L)
    }

    private fun startLoginActivity()
    {
        val intent = Intent(this, SignInActivity::class.java)
        startActivityForResult(intent, SIGN_IN_RC)
    }

    private fun launchMainActivity()
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}