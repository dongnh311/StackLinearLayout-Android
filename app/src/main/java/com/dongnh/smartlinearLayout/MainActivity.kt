package com.dongnh.smartlinearLayout

import android.graphics.drawable.GradientDrawable.Orientation
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongnh.smartlayoutmanager.CenterScrollListener
import com.dongnh.smartlayoutmanager.OnCenterItemSelectionListener
import com.dongnh.smartlayoutmanager.SmartLayoutManager
import com.dongnh.smartlayoutmanager.ZoomPostLayoutListener
import com.dongnh.smartlinearLayout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var dataBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(
            this@MainActivity,
            R.layout.activity_main
        )

        this@MainActivity.dataBinding.lifecycleOwner = this@MainActivity

        // Set data for adapter
        val adapterViewItem = AdapterViewItem()
        adapterViewItem.dataList.add(ItemModel(title = "1"))
        adapterViewItem.dataList.add(ItemModel(title = "2"))
        adapterViewItem.dataList.add(ItemModel(title = "3"))
        adapterViewItem.dataList.add(ItemModel(title = "4"))
        adapterViewItem.dataList.add(ItemModel(title = "5"))
        adapterViewItem.dataList.add(ItemModel(title = "6"))
        adapterViewItem.dataList.add(ItemModel(title = "7"))
        adapterViewItem.dataList.add(ItemModel(title = "8"))
        adapterViewItem.dataList.add(ItemModel(title = "9"))
        adapterViewItem.dataList.add(ItemModel(title = "10"))
        adapterViewItem.dataList.add(ItemModel(title = "11"))

        // Stack layout or circle layout
        val smartLayoutStack = SmartLayoutManager(orientation = SmartLayoutManager.VERTICAL, itemVisible = 3, isStackLayout = true, offset = 10)

        // If isCircleLayout = false, it same normal linear layout, but have center item
        val smartLayout = SmartLayoutManager(orientation = SmartLayoutManager.HORIZONTAL, itemVisible = 3, isCircleLayout = true, offset = 10)
        smartLayout.addOnItemSelectionListener(object : OnCenterItemSelectionListener {
            override fun onCenterItemChanged(adapterPosition: Int) {
                val item = adapterViewItem.dataList[adapterPosition]
                Log.e("Main", item.title)
            }
        })

        dataBinding.recyclerView.addOnScrollListener(CenterScrollListener())
        dataBinding.recyclerView.setHasFixedSize(true)
        dataBinding.recyclerView.adapter = adapterViewItem
        dataBinding.recyclerView.layoutManager = smartLayout
        // You can extent class ZoomPostLayoutListener for modify item size and alpha
        smartLayout.configPostListener(ZoomPostLayoutListener(0.08f, transformAlpha = false))

        adapterViewItem.notifyDataSetChanged()
    }
}