# StackLinearLayout-Android
Smart linear layout for android

~~~~ 
Make custom linear layout for android.
Suport two mode : 
  * Stack layout.
  * Wheel layout.
~~~~

Using :
  * Import module SmartLayoutManager to project.
  * Using constructor create mode :
    - Stack :
     val smartLayoutStack = SmartLayoutManager(orientation = SmartLayoutManager.VERTICAL, itemVisible = 3, isStackLayout = true, offset = 10)
     
    - Wheel :
     val smartLayout = SmartLayoutManager(orientation = SmartLayoutManager.HORIZONTAL, itemVisible = 3, isCircleLayout = true, offset = 10)
     
    With : 
      - orientation = HORIZONTAL | VERTICAL
      - itemVisible : Items show on top or bottom with one center item. Ex : 2 have show 2 items in top, 1 center, 2 items on bottom.
      - isStackLayout : Using stack layout.
      - isCircleLayout : Using circle layout.
      - offset : Adj margin of item.
   * Create new class and extent class ZoomPostLayoutListener or using ZoomPostLayoutListener for implement zoom, alpha to view.
   
   
   Full code : 
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
