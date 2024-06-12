package com.dicoding.picodiploma.loginwithanimation.view.detailprofile

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.data.response.ListBuildingItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemSavebuildingBinding
import com.dicoding.picodiploma.loginwithanimation.view.detailbuilding.DetailBuildingActivity
import com.squareup.picasso.Picasso

class FavAdapter : RecyclerView.Adapter<FavAdapter.MyViewHolder>() {

    private var items: List<Any> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSavebuildingBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListBuildingItem -> holder.bindFavorite(item)
            else -> throw IllegalArgumentException("Unsupported item type")
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<Any>) {
        Log.d("FavAdapter", "New items submitted: $newItems")
        val diffResult = DiffUtil.calculateDiff(ReviewDiffCallback(items, newItems))
        this.items = newItems
        diffResult.dispatchUpdatesTo(this)
    }

    private class ReviewDiffCallback(
        private val oldList: List<Any>,
        private val newList: List<Any>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return when {
                oldItem is ListBuildingItem && newItem is ListBuildingItem -> oldItem.id == newItem.id
                else -> false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem == newItem
        }
    }

    inner class MyViewHolder(private val binding: ItemSavebuildingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindFavorite(item: ListBuildingItem){
            val imageUrl = item.propertyImage?.firstOrNull()
            imageUrl?.let {
                Picasso.get().load(it).into(binding.ivItemPhoto)
            }
            binding.tvPropertyName.text = item.propertyName
            binding.tvLocation.text = item.location
            binding.specBedroom.text = item.bedroom
            binding.specBathroom.text = item.bathroom
            binding.specBuildingArea.text = item.buildingArea
            binding.specSurfaceArea.text = item.landArea
            binding.root.setOnClickListener {
                val intentDetail = Intent(binding.root.context, DetailBuildingActivity::class.java)
                intentDetail.putExtra("id", item.id)
                binding.root.context.startActivity(intentDetail)
            }
        }
    }
}