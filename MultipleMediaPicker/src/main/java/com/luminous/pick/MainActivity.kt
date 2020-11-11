package com.luminous.pick

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.ViewSwitcher
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.luminous.pick.Adapter.ImageListRecyclerAdapter
import com.luminous.pick.utils.Utils
import com.nostra13.universalimageloader.core.ImageLoader
import java.util.*

class MainActivity : BaseActivity() {
    //    //@BindView(R.id.imgSinglePick)
    var imgSinglePick: ImageView? = null

    //    //@BindView(R.id.btnGalleryPick)
    var btnGalleryPick: Button? = null

    //    //@BindView(R.id.btnGalleryPickMul)
    var btnGalleryPickMul: Button? = null

    //    //@BindView(R.id.viewSwitcher)
    var viewSwitcher: ViewSwitcher? = null

    //    //@BindView(R.id.recyclerView)
    var recyclerView: RecyclerView? = null
    var action: String? = null
    var handler: Handler? = null
    var imageListRecyclerAdapter: ImageListRecyclerAdapter? = null

    //	GalleryAdapter adapter;
    private val imagesUri: HashMap<String, CustomGallery>? = null
    var dataT = ArrayList<CustomGallery>()
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.main)
        // ButterKnife.bind(MainActivity.this);
        initImageLoader()
        init()
    }

    override fun initImageLoader() {
        imageLoader = Utils.initImageLoader(activity)
    }

    private fun init() {
        handler = Handler()
        recyclerView = findViewById(R.id.recyclerView)
        imgSinglePick = findViewById(R.id.imgSinglePick)
        btnGalleryPick = findViewById(R.id.btnGalleryPick)
        viewSwitcher = findViewById(R.id.viewSwitcher)
        btnGalleryPickMul = findViewById(R.id.btnGalleryPickMul)
        recyclerView?.setLayoutManager(
            GridLayoutManager(
                applicationContext, 3
            )
        )
        imageListRecyclerAdapter = ImageListRecyclerAdapter(applicationContext)
        imageListRecyclerAdapter!!.setMultiplePick(false)
        recyclerView?.setAdapter(imageListRecyclerAdapter)
        viewSwitcher?.setDisplayedChild(1)
        btnGalleryPick?.setOnClickListener(View.OnClickListener {
            val i = Intent(Action.ACTION_PICK)
            startActivityForResult(i, 100)
        })

//		btnGalleryPickMul = (Button) findViewById(R.id.btnGalleryPickMul);
        btnGalleryPickMul?.setOnClickListener(View.OnClickListener {
            val i = Intent(Action.ACTION_MULTIPLE_PICK)
            startActivityForResult(i, 200)
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            imageListRecyclerAdapter!!.clear()
            viewSwitcher!!.displayedChild = 1
            val single_path = data!!.getStringExtra("single_path")
            imageLoader?.displayImage("file://$single_path", imgSinglePick)
        } else if (requestCode == 200 && resultCode == RESULT_OK) {
            val all_path = data!!.getStringArrayExtra("all_path")
            for (string in all_path) {
                val item = CustomGallery()
                item.sdcardPath = string
                dataT.add(item)
            }
            viewSwitcher!!.displayedChild = 0
            imageListRecyclerAdapter!!.addAll(dataT)
        }
    }

    var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    //        this.doubleBackToExitPressedOnce = true;
    //        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
    //
    //        new Handler().postDelayed(new Runnable() {
    //
    //            @Override
    //            public void run() {
    //                doubleBackToExitPressedOnce = false;
    //            }
    //        }, 2000);
    var itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
        0
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val position_dragged = viewHolder.adapterPosition
            val position_target = target.adapterPosition
            Collections.swap(dataT, position_dragged, position_target)
            imageListRecyclerAdapter!!.notifyItemMoved(position_dragged, position_target)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
    })
}