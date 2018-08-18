package com.forcetower.unes.feature.login

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity.CENTER
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.forcetower.sagres.impl.SagresNavigatorImpl
import com.forcetower.sagres.operation.Callback
import com.forcetower.sagres.operation.login.LoginCallback
import com.forcetower.sagres.operation.Status
import com.forcetower.unes.GlideApp
import com.forcetower.unes.R
import com.forcetower.unes.core.injection.Injectable
import com.forcetower.unes.core.model.Profile
import com.forcetower.unes.core.vm.LoginViewModel
import com.forcetower.unes.core.vm.UViewModelFactory
import com.forcetower.unes.databinding.FragmentSigningInBinding
import com.forcetower.unes.feature.home.HomeActivity
import com.forcetower.unes.feature.shared.UFragment
import com.forcetower.unes.feature.shared.fadeIn
import com.forcetower.unes.feature.shared.provideViewModel
import timber.log.Timber
import javax.inject.Inject

class SigningInFragment : UFragment(), Injectable {
    @Inject
    lateinit var factory: UViewModelFactory

    private lateinit var binding: FragmentSigningInBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var messages: Array<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentSigningInBinding.inflate(inflater, container, false).also {
            binding = it
            prepareSwitcher()
            prepareMessages()
        }.root
    }

    private fun prepareMessages() {
        messages = resources.getStringArray(R.array.login_random_messages)
    }

    private fun prepareSwitcher() {
        binding.textStatus.setFactory {
            val textView = TextView(requireContext())
            textView.textSize = 16f
            textView.gravity = CENTER
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary_dark))
            textView
        }

        binding.textTips.setFactory {
            val textView = TextView(requireContext())
            textView.textSize = 12f
            textView.gravity = CENTER
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary_dark))
            textView
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = provideViewModel(factory)
        viewModel.getLogin().observe(this, Observer<Callback>(this::onLoginProgress))
        viewModel.getProfile().observe(this, Observer<Profile>(this::onProfileUpdate))
        doLogin()
    }

    override fun onStart() {
        super.onStart()
        displayRandomText()
    }

    private fun displayRandomText() {
        val position = (Math.random() * (messages.size - 1)).toInt()
        val message = messages[position]
        binding.textStatus.setText(message)
        Handler(Looper.getMainLooper()).postDelayed({
            if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
                displayRandomText()
        }, 3000)
    }

    private fun doLogin() {
        val username = arguments?.getString("username")
        val password = arguments?.getString("password")

        if (username.isNullOrBlank() || password.isNullOrBlank()) {
            showSnack(getString(R.string.error_invalid_credentials))
            view?.findNavController()?.popBackStack()
        } else {
            viewModel.login(username!!, password!!, true)
        }
    }

    private fun onLoginProgress(callback: Callback) {
        when(callback.status) {
            Status.STARTED -> Timber.d("Status: Started")
            Status.LOADING -> Timber.d("Status: Loading")
            Status.INVALID_LOGIN -> snackAndBack(getString(R.string.error_invalid_credentials))
            Status.APPROVING -> Timber.d("Status: Approving")
            Status.NETWORK_ERROR -> snackAndBack(getString(R.string.error_network_error))
            Status.RESPONSE_FAILED -> snackAndBack(getString(R.string.error_unexpected_response))
            Status.SUCCESS -> completeLogin()
            Status.APPROVAL_ERROR -> snackAndBack(getString(R.string.error_network_error))
        }
    }

    private fun onProfileUpdate(profile: Profile?) {
        if (profile != null) {
            binding.textHelloUser.text = getString(R.string.login_hello_user, profile.name)
            binding.textHelloUser.fadeIn()
        }

        GlideApp.with(this)
                .load(profile?.imageUrl)
                .fallback(R.mipmap.ic_unes_large_image_512)
                .placeholder(R.mipmap.ic_unes_large_image_512)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.imageCenter)
    }

    private fun completeLogin() {
        if (viewModel.isConnected()) return
        viewModel.setConnected()
        val intent = Intent(requireContext(), HomeActivity::class.java)
        val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), binding.imageCenter, getString(R.string.user_image_transition))
                .toBundle()

        startActivity(intent, bundle)
        activity?.finish()
    }

    private fun snackAndBack(string: String) {
        showSnack(string)
        view?.findNavController()?.popBackStack()
    }
}