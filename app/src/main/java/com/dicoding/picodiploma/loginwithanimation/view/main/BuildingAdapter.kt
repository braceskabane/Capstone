package com.dicoding.picodiploma.loginwithanimation.view.main

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import androidx.core.util.Pair
import com.dicoding.picodiploma.loginwithanimation.data.response.ListBuildingItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemBuildingBinding
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class BuildingAdapter : RecyclerView.Adapter<BuildingAdapter.MyViewHolder>() {

    private var items: List<Any> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBuildingBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        when (val item = items[position]) {
            is ListBuildingItem -> {
                holder.bind(item)
                Log.d("StoryAdapter", "Binding item at position $position with data: ${item.name}")
            }
            else -> throw IllegalArgumentException("Unsupported item type")
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<ListBuildingItem>) {
        Log.d("StoryAdapter", "Submitting new list with size: ${newItems.size}")
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

    inner class MyViewHolder(private val binding: ItemBuildingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListBuildingItem) {
//            Picasso.get().load(item.photoBuilding).into(binding.ivItemPhoto)
            binding.tvItemName.text = item.name
            binding.tvItemCreatedAt.text = item.createdAt
            binding.root.setOnClickListener {
                val intentDetail = Intent(binding.root.context, WelcomeActivity::class.java)
                intentDetail.putExtra("id", item.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
//                        Pair(binding.ivItemPhoto, "profile"),
                        Pair(binding.tvItemCreatedAt,"createdAt"),
                        Pair(binding.tvItemName, "name"),
                        Pair(binding.tvItemDescription, "description"),
                    )
                itemView.context.startActivity(intentDetail, optionsCompat.toBundle())
                binding.root.context.startActivity(intentDetail)
            }
        }
    }
}
