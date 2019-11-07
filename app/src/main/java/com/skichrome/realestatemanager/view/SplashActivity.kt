package com.skichrome.realestatemanager.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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

    private fun configureSyncViewModel()
    {
        viewModel = ViewModelProviders.of(this, Injection.provideSyncViewModelFactory(this)).get(OnlineSyncViewModel::class.java)
        viewModel.isSyncEnded.observe(this, Observer {
            it?.let { isSyncEnded ->
                if (isSyncEnded)
                    launchMainActivity()
            }
        })
    }

    private fun launchMainActivity()
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}