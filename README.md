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
        
 Ex : 
 
 Stack layout :
 <img width="345" alt="Screenshot 2022-12-27 at 13 32 23" src="https://user-images.githubusercontent.com/40257252/209622189-ef6a0365-30d8-4c23-8ddd-9fff35eb3902.png">
 
 
 Circle Layout : true
 
 <img width="349" alt="Screenshot 2022-12-27 at 13 34 54" src="https://user-images.githubusercontent.com/40257252/209622446-5d6191fa-3c88-404c-9253-24f8caea73cc.png">
 
 <img width="349" alt="Screenshot 2022-12-27 at 13 35 06" src="https://user-images.githubusercontent.com/40257252/209622477-2f51290e-c2f9-4eb7-9612-f4b7f66c140a.png">

 Circle Layout : false
 
<img width="350" alt="Screenshot 2022-12-27 at 13 35 54" src="https://user-images.githubusercontent.com/40257252/209622556-70ee9b55-e0a5-46e1-8446-0933b96cf100.png">


