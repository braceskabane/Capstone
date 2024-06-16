package com.dicoding.picodiploma.loginwithanimation.view.detailbuilding

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.lokal.entity.FavoriteBuilding
import com.dicoding.picodiploma.loginwithanimation.data.response.DetailBuildingResponse
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBuildingBinding
import com.dicoding.picodiploma.loginwithanimation.helper.PredictionHelper
import com.dicoding.picodiploma.loginwithanimation.view.BuildingViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.dicoding.picodiploma.loginwithanimation.view.predict.PredictActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DetailBuildingActivity : AppCompatActivity() {
    private val detailViewModel by viewModels<DetailBuildingViewModel> {
        BuildingViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailBuildingBinding
    private lateinit var progressBar3: ProgressBar
    private lateinit var textViewNotification: TextView
    private lateinit var textViewNotification2: TextView
    private var isFavorite: Boolean = false
    private lateinit var buildViewModel: BuildAddUpdateViewModel
    private lateinit var predictionHelper: PredictionHelper
    private val handler = Handler(Looper.getMainLooper())
    private var remainingTime = 130 // 2 minutes in seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBuildingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        progressBar3 = binding.progressBar3
        textViewNotification = binding.textViewNotification
        textViewNotification2 =binding.textViewNotification2

        val buildingId = intent.getStringExtra("id") ?: return

        setupView()
        setupAction()
        observeViewModel()

        detailViewModel.getSession().observe(this) { user ->
            if (user == null || !user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                val token = user.accessToken
                if (token.isNotEmpty()) {
                    Log.d("DetailBuildingActivity", "Fetching stories with token: $token")
                    detailViewModel.fetchBuildingDetail(buildingId)
                } else {
                    Log.e("DetailBuildingActivity", "Token is null atau kosong")
                }
            }
        }

        buildViewModel = ViewModelProvider(this)[BuildAddUpdateViewModel::class.java]
        checkIsFavorite(buildingId)
        buildViewModel.getFavoriteUserById(buildingId).observe(this) { favoriteUser ->
            isFavorite = favoriteUser != null
            updateFavoriteButtonIcon()
        }

        predictionHelper = PredictionHelper(
            context = this,
            onResult = { result ->
                val intent = Intent(this, PredictActivity::class.java)
                intent.putExtra("PREDICTION_RESULT", result)
                startActivity(intent)
                showLoading(false)
            },
            onError = { errorMessage ->
                Toast.makeText(this@DetailBuildingActivity, errorMessage, Toast.LENGTH_SHORT).show()
                showLoading(false)
            },
            onInitialized = {
                // Stop the handler and auto click the button once the interpreter is ready
                handler.removeCallbacks(updateNotification)
                textViewNotification.isVisible = false
                textViewNotification2.isVisible = false
                showLoading(false)
                Toast.makeText(this@DetailBuildingActivity, "Model berhasil dimuat, harga siap diprediksi", Toast.LENGTH_SHORT).show()
                Log.d("DetailBuildingActivity", "Model berhasil dimuat, harga siap diprediksi.")
            }
        )
    }

    private fun observeViewModel() {
        detailViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        detailViewModel.userSession.observe(this) {
            // Handle user session changes if needed
        }

        detailViewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_SHORT).show()
            }
        }

        detailViewModel.buildingDetail.observe(this) { buildingDetail ->
            if (buildingDetail != null) {
                displayUserDetail(buildingDetail)
            }
        }
    }

    private fun addToFavorites(building: DetailBuildingResponse) {
        val favoriteBuilding = FavoriteBuilding(
            id = building.id!!,
            propertyName = building.propertyName,
            photoBuild = building.propertyImage?.firstOrNull(),
            location = building.location,
            bedRoom = building.bedroom,
            bathRoom = building.bathroom,
            surfaceArea = building.landArea,
            buildingArea = building.buildingArea
        )
        buildViewModel.insert(favoriteBuilding)
        Log.d("DetailBuildingActivity", "Building added to favorites: $favoriteBuilding")
        Snackbar.make(binding.root, "Building added to favorites", Snackbar.LENGTH_SHORT).show()
        isFavorite = true
        updateFavoriteButtonIcon()
    }

    private fun removeFromFavorites(building: DetailBuildingResponse) {
        val favoriteBuilding = FavoriteBuilding(
            id = building.id!!,
            propertyName = building.propertyName,
            photoBuild = building.propertyImage?.firstOrNull(),
            location = building.location,
            bedRoom = building.bedroom,
            bathRoom = building.bathroom,
            surfaceArea = building.landArea,
            buildingArea = building.buildingArea
        )
        buildViewModel.delete(favoriteBuilding)
        Log.d("DetailBuildingActivity", "Building removed from favorites: $favoriteBuilding")
        Snackbar.make(binding.root, "Building removed from favorites", Snackbar.LENGTH_SHORT).show()
        isFavorite = false
        updateFavoriteButtonIcon()
    }

    private fun toggleFavoriteStatus(building: DetailBuildingResponse) {
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
            detailViewModel.buildingDetail.value?.let { building ->
                toggleFavoriteStatus(building)
            }
        }
        binding.btnPredict.setOnClickListener {
            if (predictionHelper.isInterpreterInitialized()) {
                makePrediction()
            } else {
                showLoading(true)
                predictionHelper.initializeTfLite()
                Toast.makeText(this, "Model belum selesai dimuat, tunggu beberapa saat.", Toast.LENGTH_SHORT).show()
                Log.d("DetailBuildingActivity", "Model belum selesai dimuat, tunggu beberapa saat.")
                startNotificationTimer()
            }
        }
    }

    private fun makePrediction() {
        val buildingResponse = detailViewModel.buildingDetail.value
        if (buildingResponse != null) {
            val bathroom = buildingResponse.bathroom ?: ""
            val bedroom = buildingResponse.bedroom ?: ""
            val buildingArea = buildingResponse.buildingArea ?: ""
            val landArea = buildingResponse.landArea ?: ""
            val location = buildingResponse.location ?: ""
            predictionHelper.predict(bathroom, bedroom, buildingArea, landArea, location)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar3.isVisible = isLoading
        binding.btnPredict.isEnabled = !isLoading
        textViewNotification.isVisible = isLoading
        textViewNotification2.isVisible = isLoading
    }

    private fun startNotificationTimer() {
        textViewNotification.isVisible = true
        textViewNotification2.isVisible = true
        remainingTime = 160 // 2 minutes
        handler.post(updateNotification)
    }

    private val updateNotification = object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            if (remainingTime > 0) {
                textViewNotification2.text = "Don't go back"
                textViewNotification.text = "Wait for ${remainingTime / 60} minute(s) ${remainingTime % 60} second(s) left, it could be faster"
                remainingTime--
                handler.postDelayed(this, 1000)
            } else {
                textViewNotification.text = ""
                textViewNotification2.text = ""
            }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.replaceExtras(Bundle())
        startActivity(intent)
        finish()
    }

    private fun displayUserDetail(buildingResponse: DetailBuildingResponse) {
        val imageUrls = buildingResponse.propertyImage?.filterNotNull() ?: emptyList()
        val adapter = ImageSliderAdapter(imageUrls)
        binding.viewPager2.adapter = adapter
        binding.specBathroom.text = buildingResponse.bathroom ?: ""
        binding.specBedroom.text = buildingResponse.bedroom ?: ""
        binding.specBuildingArea.text = buildingResponse.buildingArea ?: ""
        binding.specFloor.text = buildingResponse.floor ?: ""
        binding.specYear.text = buildingResponse.year ?: ""
        binding.specSurfaceArea.text = buildingResponse.landArea ?: ""
        binding.textHomeName.text = buildingResponse.propertyName ?: ""
        binding.textDescription.text = buildingResponse.description ?: ""
        binding.textLocation.text = buildingResponse.location ?: ""
    }
}
