package com.dicoding.picodiploma.loginwithanimation.view.detailprofile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailProfileBinding
import com.dicoding.picodiploma.loginwithanimation.view.AuthViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.BuildingViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class DetailProfileActivity : AppCompatActivity() {
    private val viewModel by viewModels<DetailProfileViewModel> {
        BuildingViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAction()

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar5.isVisible = isLoading
        }

        viewModel.getSession().observe(this) { user ->
            if (user == null || !user.isLogin) {
                Log.d("DetailProfileActivity", "No user session or user is not logged in")
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                Log.d(
                    "DetailProfileActivity",
                    "User session: name=${user.name}, id=${user.id}, email=${user.email}, status=${user.status}, picture=${user.picture}"
                )

                binding.profileName.text = user.name
                binding.profileId.text = user.id
                binding.profileEmail.text = user.email
                val imageUrl = user.picture
                Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_capture)
                    .error(R.drawable.logo)
                    .into(binding.imageProfile, object : Callback {
                        override fun onSuccess() {
                            Log.d("Picasso", "Image loaded successfully")
                            binding.progressBar5.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            Log.e("Picasso", "Error loading image: ${e?.message}")
                        }
                    })
            }
        }
    }

    private fun setupAction() {
        binding.profileSignOut.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }
}