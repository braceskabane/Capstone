@file:Suppress("DEPRECATION")

package com.dicoding.picodiploma.loginwithanimation.view.addproperty

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.ResultState
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityAddPropertyBinding
import com.dicoding.picodiploma.loginwithanimation.view.BuildingViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.detailprofile.DetailProfileActivity
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class AddPropertyActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {
    private val viewModel by viewModels<AddPropertyViewModel> {
        BuildingViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityAddPropertyBinding
    private val selectedImages = mutableListOf<Uri>()
    private lateinit var imageAdapter: ImageAdapter
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Permission request granted")
            } else {
                showToast("Permission request denied")
            }
        }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPropertyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        if (savedInstanceState != null) {
            savedInstanceState.getParcelableArrayList<Uri>(IMAGE_URI_KEY)?.let {
                selectedImages.addAll(it)
                imageAdapter.notifyDataSetChanged()
            }
        }

        imageAdapter = ImageAdapter(selectedImages)


        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this)
        binding.bottomNavigationView.selectedItemId = R.id.btn_add

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.imageBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        binding.buttonAdd.setOnClickListener {
            uploadImages()
        }
    }

    private fun uploadImages() {
        val imageFiles = selectedImages.map { uri ->
            uriToFile(uri, this).reduceFileImage()
        }

        val description = binding.edAddDescription.text.toString().trim()
        val propertyName = binding.edAddPropertyName.text.toString().trim()
        val year = binding.specYear.text.toString().trim()
        val landArea = binding.specSurfaceArea.text.toString().trim()
        val buildingArea = binding.specBuildingArea.text.toString().trim()
        val location = binding.edAddLocation.text.toString().trim()
        val floor = binding.specFloor.text.toString().trim()
        val bedroom = binding.specBedroom.text.toString().trim()
        val bathroom = binding.specBathroom.text.toString().trim()

        viewModel.uploadBuilding(
            imageFiles,
            propertyName,
            year,
            landArea,
            buildingArea,
            location,
            floor,
            bedroom,
            bathroom,
            description
        ).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }
                    is ResultState.Success -> {
                        result.data.message?.let { showToast(it) }
                        showLoading(false)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }

                    is ResultState.Error -> {
                        showToast(result.error)
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar4.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
    }

    @SuppressLint("NotifyDataSetChanged")
    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri>? ->
        if (!uris.isNullOrEmpty()) {
            selectedImages.addAll(uris)
            imageAdapter.notifyDataSetChanged()
            updatePreviewImageView()
            uris.forEach { uri ->
                Log.d("Gallery", "Image added: $uri")
            }
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    @SuppressLint("NotifyDataSetChanged")
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            currentImageUri?.let {
                selectedImages.add(it)
                imageAdapter.notifyDataSetChanged()
                updatePreviewImageView()
                Log.d("Camera", "Image added: $it")
            }
        }
    }

    private fun updatePreviewImageView() {
        if (selectedImages.isNotEmpty()) {
            binding.previewImageView.setImageURI(selectedImages.last())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(IMAGE_URI_KEY, ArrayList(selectedImages))
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        private const val IMAGE_URI_KEY = "IMAGE_URI_KEY"
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_home -> {
                startActivity(Intent(this, MainActivity::class.java))
                return true
            }
            R.id.btn_add -> {
                return true  // Already on add property page
            }
            R.id.btn_profile -> {
                startActivity(Intent(this, DetailProfileActivity::class.java))
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}