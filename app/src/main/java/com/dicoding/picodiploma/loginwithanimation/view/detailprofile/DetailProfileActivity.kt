package com.dicoding.picodiploma.loginwithanimation.view.detailprofile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.lokal.entity.FavoriteBuilding
import com.dicoding.picodiploma.loginwithanimation.data.response.ListBuildingItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailProfileBinding
import com.dicoding.picodiploma.loginwithanimation.view.BuildingViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.FavoriteViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.addproperty.AddPropertyActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class DetailProfileActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var viewModel: DetailProfileViewModel
    private lateinit var binding: ActivityDetailProfileBinding
    private lateinit var adapter: FavAdapter
    private lateinit var progressBar5: ProgressBar
    private lateinit var favoriteViewModel: FavProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar5 = binding.progressBar5
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
        binding.bottomNavigationView.selectedItemId = R.id.btn_profile

        adapter = FavAdapter()
        binding.userBuildrecycleView.layoutManager = LinearLayoutManager(this)
        binding.userBuildrecycleView.adapter = adapter
        setupAction()

        val factory = BuildingViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[DetailProfileViewModel::class.java]

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar5.isVisible = isLoading
        }

        viewModel.getSession().observe(this) { user ->
            if (user == null || !user.isLogin) {
                Log.d("DetailProfileActivity", "No user session or user is not logged in")
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                val token = user.accessToken
                if (token.isNotEmpty()) {
                    Log.d("DetailProfileActivity", "Fetching profile with token: $token")
                    viewModel = ViewModelProvider(this, factory)[DetailProfileViewModel::class.java]
                    viewModel.fetchUserProfile()
                } else {
                    Log.e("DetailProfileActivity", "Token is null or empty")
                }
            }
        }

        viewModel.profileResponse.observe(this) { response ->
            response?.let {
                binding.profileName.text = it.data?.name
                binding.profileCreatedAt.text = it.data?.createdAt
                binding.profileEmail.text = it.data?.email
                binding.profileRole.text = it.data?.role
                val imageUrl = "https://play-lh.googleusercontent.com/HnzbI7urJlB6V26dtKiawYoBrH4iR5DAAk4KqNZzIa0NRWQukskR6aX7IrV55AULKIgA=w240-h480-rw"
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

        val favFactory = FavoriteViewModelFactory(application)
        favoriteViewModel = ViewModelProvider(this, favFactory)[FavProfileViewModel::class.java]

        favoriteViewModel.isLoading.observe(this) { isLoading ->
            progressBar5.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        favoriteViewModel.builds.observe(this) { builds ->
            try {
                if (builds != null) {
                    val items = builds.map { it.toListBuildingItem() }
                    adapter.submitList(items)
                } else {
                    Log.e("DetailProfileActivity", "No buildings found")
                }
            } catch (e: Exception) {
                Log.e("DetailProfileActivity", "Error processing buildings: ${e.message}", e)
            }
        }

        favoriteViewModel.getAllBuilding()
        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                Log.e("DetailProfileActivity", "Error: $errorMessage")
            }
        }
    }

    private fun FavoriteBuilding.toListBuildingItem(): ListBuildingItem {
        return ListBuildingItem(
            propertyImage = listOfNotNull(photoBuild),
            propertyName = propertyName,
            location = location,
            bedroom = bedRoom,
            bathroom = bathRoom,
            landArea = surfaceArea,
            buildingArea = buildingArea,
            id = id
        )
    }

    private fun setupAction() {
        binding.profileSignOut.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_home -> {
                startActivity(Intent(this, MainActivity::class.java))
                return true
            }
            R.id.btn_add -> {
                startActivity(Intent(this, AddPropertyActivity::class.java))
                return true
            }
            R.id.btn_profile -> {
                return true  // Already on profile page
            }
        }
        return false
    }
}