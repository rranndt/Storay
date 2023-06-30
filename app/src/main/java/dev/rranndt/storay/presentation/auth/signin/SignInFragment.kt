package dev.rranndt.storay.presentation.auth.signin

import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.rranndt.storay.R
import dev.rranndt.storay.databinding.FragmentSignInBinding
import dev.rranndt.storay.presentation.base.BaseFragment
import dev.rranndt.storay.presentation.main.MainActivity
import dev.rranndt.storay.util.Helper.doAnimation
import dev.rranndt.storay.util.Helper.hideKeyboard
import dev.rranndt.storay.util.Helper.showShortSnackBar
import dev.rranndt.storay.util.Result
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignInFragment : BaseFragment<FragmentSignInBinding, SignInViewModel>() {

    override val viewModel: SignInViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSignInBinding =
        FragmentSignInBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playAnimation()
        isAlreadySignIn()
        setUpView()
        setButtonToEnable()
        subscribeToSignInEvents()
    }

    private fun setUpView() {
        binding?.apply {
            btnSignIn.setOnClickListener {
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                viewModel.onEvent(SignInEvent.SignIn(email, password))
                hideKeyboard()
            }

            tvToSignUp.setOnClickListener {
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }
        }
    }

    private fun setButtonToEnable() {
        binding?.apply {
            etEmail.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                isFormValid()
            })
            etPassword.addTextChangedListener(onTextChanged = { _, _, _, _ ->
                isFormValid()
            })
        }
    }

    private fun subscribeToSignInEvents() = lifecycleScope.launch {
        viewModel.signInState.collect { result ->
            when (result.signInState) {
                is Result.Success -> {
                    showLoading(false)
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().apply {
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                }

                is Result.Error -> {
                    showLoading(false)
                    binding?.root?.showShortSnackBar(result.signInState.message)
                }

                is Result.Loading -> {
                    showLoading(true)
                }

                else -> {}
            }
        }
    }

    private fun isFormValid() {
        binding?.apply {
            val email = etEmail.isValidEmail(etEmail.text)
            val password = etPassword.isValidPassword(etPassword.text)
            btnSignIn.isEnabled = email and password
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            if (isLoading) progressBar.isVisible = true
            else progressBar.isInvisible = true
        }
    }

    private fun isAlreadySignIn() = lifecycleScope.launch {
        viewModel.onEvent(SignInEvent.GetSignInStatus)
    }

    private fun playAnimation() {
        binding?.apply {
            val title = tvTitleHeader.doAnimation()
            val emailLayout = etEmail.doAnimation()
            val passwordLayout = etPassword.doAnimation()
            val btnSignIn = btnSignIn.doAnimation()
            val toSignUp = tvToSignUp.doAnimation()

            val layout = AnimatorSet().apply {
                playTogether(emailLayout, passwordLayout)
            }

            val button = AnimatorSet().apply {
                playTogether(btnSignIn, toSignUp)
            }

            AnimatorSet().apply {
                playSequentially(title, layout, button)
            }.start()

        }
    }
}