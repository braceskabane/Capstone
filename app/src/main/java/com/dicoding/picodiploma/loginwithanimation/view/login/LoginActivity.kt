package com.dicoding.picodiploma.loginwithanimation.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.edit
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.AuthViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        AuthViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private val handler = Handler(Looper.getMainLooper())
    private var errorRunnable: Runnable? = null

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        viewModel.isDataValid.observe(this) { isValid ->
            binding.loginButton.isEnabled = isValid
        }

        viewModel.passwordError.observe(this) { error ->
            binding.edLoginPassword.error = error
            errorRunnable?.let { handler.removeCallbacks(it) }
            if (error != null) {
                errorRunnable = Runnable {
                    binding.edLoginPassword.error = null
                }
                handler.postDelayed(errorRunnable!!, 1000)
            }
            updateLoginButtonState()
        }
        viewModel.emailError.observe(this) { error ->
            binding.edLoginEmail.error = error
            errorRunnable?.let { handler.removeCallbacks(it) }
            if (error != null) {
                errorRunnable = Runnable {
                    binding.edLoginEmail.error = null
                }
                handler.postDelayed(errorRunnable!!, 1000)
            }
            updateLoginButtonState()
        }
        viewModel.loginResponse.observe(this) { response ->
            if (response != null) {
                if (response.error != null) {
                    val errorMessage = response.message ?: "Unknown error"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                } else {
                    val loginResult = response.user
                    if (loginResult != null) {
                        val email = loginResult.email.toString()
                        val name = loginResult.name.toString()
                        val token = loginResult.accessToken.toString()
                        val id = loginResult.id.toString()
                        val picture = loginResult.picture.toString()
                        val status = loginResult.status.toString()

                        Log.d("LoginActivity", "Received user data: $email, $name, $token, $id, $picture")
                        viewModel.saveSession(UserModel(id, email, name, token, picture, status))
                        Log.d("LoginActivity", "Token saved: $token")
                        AlertDialog.Builder(this).apply {
                            binding.ProgressBar1.visibility = View.GONE
                            binding.Overlay1.visibility = View.GONE
                            setTitle("Yeah!")
                            setMessage(getString(R.string.login_message))
                            setPositiveButton("Lanjut") { _, _ ->
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                                Log.d("LoginActivity", "Navigating to MainActivity")
                            }
                            create()
                            show()
                        }
                    }
                }
            }
        }

        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.ProgressBar1.visibility = View.GONE
                binding.Overlay1.visibility = View.GONE
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.isLoading.observe(this) { isLoading ->
            binding.ProgressBar1.isVisible = isLoading
        }

        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.validatePassword(s.toString())
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        binding.edLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.validateEmail(s.toString())
            }

            override fun afterTextChanged(s: Editable) {

            }
        })
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            binding.ProgressBar1.visibility = View.VISIBLE
            binding.Overlay1.visibility = View.VISIBLE

            viewModel.login(email, password)
        }
    }

    private fun updateLoginButtonState() {
        val isEmailValid = viewModel.emailError.value == null
        val isPasswordValid = viewModel.passwordError.value == null
        binding.loginButton.isEnabled = isEmailValid && isPasswordValid
    }


    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }
}