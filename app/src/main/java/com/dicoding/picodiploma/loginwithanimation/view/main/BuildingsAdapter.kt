package com.dicoding.picodiploma.loginwithanimation.view.main

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.data.response.ListBuildingItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemSavebuildingBinding
import com.dicoding.picodiploma.loginwithanimation.view.detailbuilding.DetailBuildingActivity
import com.squareup.picasso.Picasso

class BuildingsAdapter :
    PagingDataAdapter<ListBuildingItem, BuildingsAdapter.MyViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListBuildingItem>() {
            override fun areItemsTheSame(oldItem: ListBuildingItem, newItem: ListBuildingItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListBuildingItem, newItem: ListBuildingItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSavebuildingBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
            Log.d("BuildingsAdapter", "Binding item at position $position with data: ${item.propertyName}")
        }
    }

    inner class MyViewHolder(private val binding: ItemSavebuildingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListBuildingItem) {
            val firstPhotoUrl = item.propertyImage?.firstOrNull { it != null }
            Picasso.get().load(firstPhotoUrl).into(binding.ivItemPhoto)
            binding.tvPropertyName.text = item.propertyName
            binding.tvLocation.text = item.location
            binding.specBedroom.text = item.bedroom
            binding.specBathroom.text = item.bathroom
            binding.specBuildingArea.text = item.buildingArea
            binding.specSurfaceArea.text = item.landArea
            binding.root.setOnClickListener {
                val intentDetail = Intent(binding.root.context, DetailBuildingActivity::class.java)
                intentDetail.putExtra("id", item.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivItemPhoto, "profile"),
                    )
                itemView.context.startActivity(intentDetail, optionsCompat.toBundle())
                binding.root.context.startActivity(intentDetail)
            }
        }
    }
}
