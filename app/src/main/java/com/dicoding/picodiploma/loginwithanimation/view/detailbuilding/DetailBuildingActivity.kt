package com.dicoding.picodiploma.loginwithanimation.view.detailbuilding

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.lokal.entity.FavoriteBuilding
import com.dicoding.picodiploma.loginwithanimation.data.response.ListBuildingItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBuildingBinding
import com.dicoding.picodiploma.loginwithanimation.view.BuildingViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class DetailBuildingActivity : AppCompatActivity() {
    private val viewModel by viewModels<DetailBuildingViewModel> {
        BuildingViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailBuildingBinding
    private lateinit var progressBar3: ProgressBar
    private lateinit var buildViewModel: BuildAddUpdateViewModel
    private var isFavorite: Boolean = false
    private lateinit var currentBuilding: ListBuildingItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBuildingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar3 = binding.progressBar3
        viewModel.getSession().observe(this) { user ->
            if (user == null || !user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                val token = user.accessToken
                if (token.isNotEmpty()) {
                    Log.d("DetailBuildingActivity", "Fetching stories with token: $token")
                } else {
                    Log.e("DetailBuildingActivity", "Token is null or empty")
                }
            }
        }
        setupView()
        setupAction()

        //
        displayUserDetail(currentBuilding)
        checkIsFavorite(currentBuilding.id.toString())
        buildViewModel.getFavoriteUserById(currentBuilding.id.toString()).observe(this) { favoriteUser ->
            isFavorite = favoriteUser != null
            updateFavoriteButtonIcon()
        }
    }

    private fun addToFavorites(building: ListBuildingItem) {
        val favoriteBuilding = FavoriteBuilding(
            id = building.id!!,

            location = building.location,
            bedRoom = building.bedRoom,
            bathRoom = building.bathRoom,
            surfaceArea = building.surfaceArea,
            buildingArea = building.buildingArea
        )
        buildViewModel.insert(favoriteBuilding)
        Snackbar.make(binding.root, "Building added to favorites", Snackbar.LENGTH_SHORT).show()
        isFavorite = true
        updateFavoriteButtonIcon()
    }

    private fun removeFromFavorites(building: ListBuildingItem) {
        val favoriteBuilding = FavoriteBuilding(
            id = building.id!!,
            location = building.location,
            bedRoom = building.bedRoom,
            bathRoom = building.bathRoom,
            surfaceArea = building.surfaceArea,
            buildingArea = building.buildingArea
        )
        buildViewModel.delete(favoriteBuilding)
        Snackbar.make(binding.root, "Building removed from favorites", Snackbar.LENGTH_SHORT).show()
        isFavorite = false
        updateFavoriteButtonIcon()
    }

    private fun toggleFavoriteStatus(building: ListBuildingItem) {
        lifecycleScope.launch {
            if (isFavorite) {
                removeFromFavorites(building)
            } else {
                addToFavorites(building)
            }
        }
    }

    private fun checkIsFavorite(id: String) {
        buildViewModel.getFavoriteUserById(id).observe(this) { favoriteBuilding ->
            isFavorite = favoriteBuilding != null
            updateFavoriteButtonIcon()
        }
    }

    private fun updateFavoriteButtonIcon() {
        if (isFavorite) {
            binding.btnFav.setImageResource(R.drawable.ic_favorite)
        } else {
            binding.btnFav.setImageResource(R.drawable.ic_favorite_border)
        }
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
        binding.btnFav.setOnClickListener {
            toggleFavoriteStatus(currentBuilding)
        }
        binding.btnPredict.setOnClickListener {
        }
    }

    private fun displayUserDetail(buildingResponse: ListBuildingItem) {
        val imageUrls = buildingResponse.photoBuilding ?: emptyList()
        val adapter = ImageSliderAdapter(imageUrls)
        binding.viewPager.adapter = adapter
        Picasso.get().load(buildingResponse.photoContact).into(binding.imageKontakReal)
        binding.specAddress.text = buildingResponse.location ?: ""
        binding.specBathroom.text = buildingResponse.bathRoom ?: ""
        binding.specBedroom.text = buildingResponse.bedRoom ?:""
        binding.specBuildingArea.text = buildingResponse.bedRoom ?:""
        binding.specSurfaceArea.text = buildingResponse.bedRoom ?:""
        binding.textHomeName.text = buildingResponse.bedRoom ?:""
        binding.textDescription.text = buildingResponse.bedRoom ?:""
        binding.textPrice.text = buildingResponse.bedRoom ?:""
        binding.textNameKontak.text = buildingResponse.bedRoom ?:""
    }
}