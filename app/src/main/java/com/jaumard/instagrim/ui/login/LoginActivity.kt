package com.jaumard.instagrim.ui.login

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Window
import com.jaumard.instagrim.R
import com.jaumard.instagrim.databinding.ActivityLoginBinding
import com.jaumard.instagrim.di.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class LoginActivity : DaggerAppCompatActivity() {
    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            supportRequestWindowFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        }
        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        val loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
        binding.viewModel = loginViewModel

        val data: Uri? = intent.data
        if (data == null) {
            loginViewModel.checkUserConnexion()
        } else {
            val code = data.getQueryParameter("code")
            val error = data.getQueryParameter("error")
            val errorMessage = data.getQueryParameter("error_description")

            loginViewModel.manageInstagramCode(code, error, errorMessage)
        }
    }

}
