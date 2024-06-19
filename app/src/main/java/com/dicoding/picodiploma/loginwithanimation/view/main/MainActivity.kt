package com.dicoding.picodiploma.loginwithanimation.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.BuildingViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.addproperty.AddPropertyActivity
import com.dicoding.picodiploma.loginwithanimation.view.detailprofile.DetailProfileActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private val viewModel by viewModels<MainViewModel> {
        BuildingViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BuildingsAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var aiAdapter: AiAdapter
    private lateinit var labelAdapter: LabelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.background
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
        binding.bottomNavigationView.selectedItemId = R.id.btn_home

        progressBar = binding.progressBar2

        viewModel.getSession().observe(this) { user ->
            if (user == null || !user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                val token = user.accessToken
                if (token.isNotEmpty()) {
                    Log.d("MainActivity", "Fetching buildings with token: $token")
                    viewModel.fetchBuildings()
                    val imageUrl = "https://en.kpop-star.net/wp-content/uploads/2023/02/hanni%E3%80%80profile4.jpg.webp"
                    Picasso.get()
                        .load(imageUrl)
                        .placeholder(R.drawable.app_widget_background)
                        .error(R.drawable.app_widget_background)
                        .into(binding.actionProfile, object : Callback {
                            override fun onSuccess() {
                                Log.d("Picasso", "Image loaded successfully")
                                binding.progressBar2.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                Log.e("Picasso", "Error loading image: ${e?.message}")
                            }
                        })
                    setupLabelAdapter()
                    viewModel.searchBuildings("Jakarta Selatan")
                } else {
                    Log.e("MainActivity", "Token is null or empty")
                    Toast.makeText(this, "Terjadi kesalahan. Silakan login lagi.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    finish()
                }
            }
        }

        setupView()
        setupRecyclerView()
        setupAction()
        playAnimation()

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(this) { errorMessage ->
            Log.e("MainActivity", "Error: $errorMessage")
            if (errorMessage.contains("500")) {
                Toast.makeText(this, "Terjadi kesalahan. Silakan login lagi.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        viewModel.buildingsPagingData.observe(this) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
        }

        viewModel.searchResults.observe(this) { searchResults ->
            aiAdapter.submitList(searchResults)
            aiAdapter.notifyDataSetChanged()
        }

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }

            if (loadState.refresh is LoadState.Error) {
                val error = (loadState.refresh as LoadState.Error).error
                Log.e("MainActivity", "Error loading data: ${error.message}")
            }
        }

        viewModel.isDarkMode.observe(this) { isDarkMode ->
            setDarkMode(isDarkMode)
        }

        window.sharedElementEnterTransition = TransitionInflater.from(this).inflateTransition(android.R.transition.move)
        window.sharedElementExitTransition = TransitionInflater.from(this).inflateTransition(android.R.transition.move)
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

    private fun setupRecyclerView() {
        adapter = BuildingsAdapter()
        binding.usersRecycleView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.usersRecycleView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
    }

    private fun setupLabelAdapter() {
        aiAdapter = AiAdapter()
        binding.PredictRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.PredictRecyclerView.adapter = aiAdapter
        val locations = listOf(
            "Bandung", "Bekasi", "Bogor", "Depok",
            "JAKSEL", "Jakarta Barat", "Jakarta Pusat",
            "Jakarta Selatan", "Jakarta Timur", "Jakarta Utara"
        )
        labelAdapter = LabelAdapter(locations) { location ->
            viewModel.searchBuildings(location)
        }
        binding.labelRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.labelRecyclerView.adapter = labelAdapter
    }

    private fun setupAction() {
        binding.actionProfile.setOnClickListener {
            startActivity(Intent(this, DetailProfileActivity::class.java))
            finish()
        }
        binding.actionDarkmode.setOnClickListener {
            viewModel.setDarkMode(true)
        }
        binding.actionOffdarkmode.setOnClickListener {
            viewModel.setDarkMode(false)
        }
    }

    private fun playAnimation() {
        val name = ObjectAnimator.ofFloat(binding.terraVision, View.ALPHA, 1f).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding.frameLayout, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(name, message)
            startDelay = 500
        }.start()
    }

    private fun setDarkMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            binding.actionDarkmode.visibility = View.GONE
            binding.actionOffdarkmode.visibility = View.VISIBLE
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            binding.actionDarkmode.visibility = View.VISIBLE
            binding.actionOffdarkmode.visibility = View.GONE
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_home -> {
                return true
            }
            R.id.btn_add -> {
                startActivity(Intent(this, AddPropertyActivity::class.java))
                return true
            }
            R.id.btn_profile -> {
                startActivity(Intent(this, DetailProfileActivity::class.java))
                return true
            }
        }
        return false
    }
}
