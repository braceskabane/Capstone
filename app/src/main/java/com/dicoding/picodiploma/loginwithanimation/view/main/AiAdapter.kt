package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.data.response.DataItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemAiBinding
import com.dicoding.picodiploma.loginwithanimation.view.detailbuilding.DetailBuildingActivity
import com.squareup.picasso.Picasso

class AiAdapter : RecyclerView.Adapter<AiAdapter.MyViewHolder>() {

    private var items: List<Any> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAiBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        when (val item = items[position]) {
            is DataItem -> {
                holder.bind(item)
                Log.d("StoryAdapter", "Binding item at position $position with data: ${item.propertyName}")
            }
            else -> throw IllegalArgumentException("Unsupported item type")
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<DataItem>) {
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
                oldItem is DataItem && newItem is DataItem -> oldItem.id == newItem.id

                else -> false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem == newItem
        }
    }

    inner class MyViewHolder(private val binding: ItemAiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataItem) {
            val firstPhotoUrl = item.propertyImage?.firstOrNull { it != null }
            Picasso.get().load(firstPhotoUrl).into(binding.aiImage)
            binding.aiTitle.text = item.propertyName
            binding.aiLocation.text = item.location
            binding.root.setOnClickListener {
                val intentDetail = Intent(binding.root.context, DetailBuildingActivity::class.java)
                intentDetail.putExtra("id", item.id)

//                val optionsCompat: ActivityOptionsCompat =
//                    ActivityOptionsCompat.makeSceneTransitionAnimation(
//                        itemView.context as Activity,
//                        Pair(binding.aiImage, "profile"),
//                    )
//                itemView.context.startActivity(intentDetail, optionsCompat.toBundle())
                binding.root.context.startActivity(intentDetail)
            }
        }
    }
}