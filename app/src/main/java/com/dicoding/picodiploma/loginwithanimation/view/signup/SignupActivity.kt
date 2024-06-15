package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.view.AuthViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var viewModel: SignupViewModel
    private val handler = Handler(Looper.getMainLooper())
    private var errorRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()

        val viewModelFactory = AuthViewModelFactory(Injection.provideUserRepository(this))
        viewModel = ViewModelProvider(this, viewModelFactory)[SignupViewModel::class.java]

        viewModel.isDataValid.observe(this) { isValid ->
            binding.signupButton.isEnabled = isValid
        }

        viewModel.passwordError.observe(this) { error ->
            binding.edRegisterPassword.error = error
            errorRunnable?.let { handler.removeCallbacks(it) }
            if (error != null) {
                errorRunnable = Runnable {
                    binding.edRegisterPassword.error = null
                }
                handler.postDelayed(errorRunnable!!, 1000)
            }
            updateLoginButtonState()
        }

        viewModel.emailError.observe(this) { error ->
            binding.edRegisterEmail.error = error
            errorRunnable?.let { handler.removeCallbacks(it) }
            if (error != null) {
                errorRunnable = Runnable {
                    binding.edRegisterEmail.error = null
                }
                handler.postDelayed(errorRunnable!!, 1000)
            }
            updateLoginButtonState()
        }

        viewModel.registerResponse.observe(this) { response ->
            binding.ProgressBar.visibility = View.GONE
            binding.Overlay.visibility = View.GONE

            response?.let {
                val message = response.message ?: "Your account has been created successfully. Let's login to start the future!"
                AlertDialog.Builder(this).apply {
                    setTitle("Yeah!")
                    setMessage(message)
                    setPositiveButton("Continue") { _, _ ->
                        finish()
                    }
                    create()
                    show()
                }
            }
        }

        viewModel.error.observe(this) { error ->
            binding.ProgressBar.visibility = View.GONE
            binding.Overlay.visibility = View.GONE

            error?.let {
                Toast.makeText(this, "Pendaftaran gagal: $error", Toast.LENGTH_LONG).show()
            }
        }

        binding.edRegisterPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.validatePassword(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.validateEmail(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

//        binding.showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
//            showPassword(isChecked)
//        }
    }

    private fun showPassword(show: Boolean) {
        if (show) {
            binding.edRegisterPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.edRegisterPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        // Move cursor to the end of the text
        binding.edRegisterPassword.setSelection(binding.edRegisterPassword.text?.length ?: 0)
    }

    private fun updateLoginButtonState() {
        val isEmailValid = viewModel.emailError.value == null
        val isPasswordValid = viewModel.passwordError.value == null
        binding.signupButton.isEnabled = isEmailValid && isPasswordValid
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString().trim()

            Log.d("SignupActivity", "Register data: Name=$name, Email=$email, Password=$password")

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.ProgressBar.visibility = View.VISIBLE
            binding.Overlay.visibility = View.VISIBLE

            viewModel.register(name, email, password)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
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

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)
//        val showPasswordCheckBox =
//            ObjectAnimator.ofFloat(binding.showPasswordCheckBox, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
//                showPasswordCheckBox,

                signup
            )
            startDelay = 100
        }.start()
    }
}