package com.dicoding.picodiploma.loginwithanimation.view.addproperty

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.BuildingRepository
import java.io.File

class AddPropertyViewModel(private val repository: BuildingRepository) : ViewModel() {
    fun uploadBuilding(files: List<File>,
                       propertyName: String,
                       year: String,
                       landArea: String,
                       buildingArea: String,
                       location:String,
                       floor: String,
                       bedroom: String,
                       bathroom: String,
                       description: String
    ) = repository.uploadBuilding(
        files,
        propertyName,
        year,
        landArea,
        buildingArea,
        location,
        floor,
        bedroom,
        bathroom,
        description
    )
}