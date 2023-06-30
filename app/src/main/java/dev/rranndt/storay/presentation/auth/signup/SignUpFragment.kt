package dev.rranndt.storay.presentation.auth.signup

import android.animation.AnimatorSet
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
import dev.rranndt.storay.databinding.FragmentSignUpBinding
import dev.rranndt.storay.presentation.base.BaseFragment
import dev.rranndt.storay.util.Helper.doAnimation
import dev.rranndt.storay.util.Helper.hideKeyboard
import dev.rranndt.storay.util.Helper.showShortSnackBar
import dev.rranndt.storay.util.Result
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding, SignUpViewModel>() {

    override val viewModel: SignUpViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSignUpBinding =
        FragmentSignUpBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playAnimation()
        setUpView()
        setButtonToEnable()
        subscribeToSignUpEvents()
    }

    private fun setUpView() {
        binding?.apply {
            btnSignUp.setOnClickListener {
                val name = etName.text.toString()
                val email = etEmail.text.toString()
                val password = etPassword.text.toString()
                viewModel.onEvent(SignUpEvent.SignUp(name, email, password))
                hideKeyboard()
            }

            tvToSignIn.setOnClickListener {
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
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

    private fun subscribeToSignUpEvents() = lifecycleScope.launch {
        viewModel.signUpState.collect { result ->
            when (result.signUpState) {
                is Result.Success -> {
                    showLoading(false)
                    binding?.root?.showShortSnackBar(result.signUpState.data?.message)
                    findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
                }

                is Result.Error -> {
                    showLoading(false)
                    binding?.root?.showShortSnackBar(result.signUpState.message)
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
            val name = etName.isValidName(etName.text)
            val email = etEmail.isValidEmail(etEmail.text)
            val password = etPassword.isValidPassword(etPassword.text)
            btnSignUp.isEnabled = name and email and password
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding?.apply {
            if (isLoading) progressBar.isVisible = true
            else progressBar.isInvisible = true
        }
    }

    private fun playAnimation() {
        binding?.apply {
            val title = tvTitleHeader.doAnimation()
            val nameLayout = etName.doAnimation()
            val emailLayout = etEmail.doAnimation()
            val passwordLayout = etPassword.doAnimation()
            val btnSignUp = btnSignUp.doAnimation()
            val toSignIn = tvToSignIn.doAnimation()

            val layout = AnimatorSet().apply {
                playTogether(nameLayout, emailLayout, passwordLayout)
            }

            val button = AnimatorSet().apply {
                playTogether(btnSignUp, toSignIn)
            }

            AnimatorSet().apply {
                playSequentially(title, layout, button)
            }.start()

        }
    }

}