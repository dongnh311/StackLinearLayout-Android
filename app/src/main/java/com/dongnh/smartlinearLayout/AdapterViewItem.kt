package com.dongnh.smartlinearLayout

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.dongnh.smartlinearLayout.databinding.ItemViewBinding

/**
 * Project : SmartlinearLayout
 * Created by DongNH on 26/12/2022.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class AdapterViewItem(): RecyclerView.Adapter< AdapterViewItem.ViewHolderItem>() {

    val dataList = mutableListOf<ItemModel>()

    inner class ViewHolderItem(private val binding: ItemViewBinding) : ViewHolder(binding.root) {
        fun binding(itemModel: ItemModel) {
            binding.viewItem.text = itemModel.title
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        val binding: ItemViewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_view,
            parent,
            false
        )
        return ViewHolderItem(binding)
    }

    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        holder.binding(dataList[position])
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}