package com.luminous.pick

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.luminous.pick.Adapter.ImageListRecyclerAdapter
import com.luminous.pick.utils.Utils
import com.nostra13.universalimageloader.core.ImageLoader
import java.util.*

class CustomGalleryActivity : BaseActivity() {
    //    //@BindView(R.id.gridGallery)
    //    GridView gridGallery;
    //@BindView(R.id.recyclerView)
    var recyclerView: RecyclerView? = null

    //@BindView(R.id.imgNoMedia)
    var imgNoMedia: ImageView? = null

    //@BindView(R.id.btnGalleryOk)
    var btnGalleryOk: Button? = null
    var action: String? = null
    var handler: Handler? = null

    //    GalleryAdapter adapter;
    var imageListRecyclerAdapter: ImageListRecyclerAdapter? = null
    override var imageLoader: ImageLoader? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.gallery)
        //        ButterKnife.bind(this);
        action = intent.action
        if (action == null) {
            finish()
        }
        initImageLoader()
        init()
    }

    override fun initImageLoader() {
        imageLoader = Utils.initImageLoader(activity)
    }

    private fun init() {
        handler = Handler()
        recyclerView = findViewById(R.id.recyclerView)
        imgNoMedia = findViewById(R.id.imgNoMedia)
        btnGalleryOk = findViewById(R.id.btnGalleryOk)
        imgNoMedia?.setVisibility(View.GONE)
        recyclerView?.setLayoutManager(
            GridLayoutManager(
                applicationContext, 3
            )
        )
        imageListRecyclerAdapter = ImageListRecyclerAdapter(applicationContext)
        recyclerView?.setAdapter(imageListRecyclerAdapter)
        if (action.equals(Action.ACTION_MULTIPLE_PICK, ignoreCase = true)) {
            findViewById<View>(R.id.llBottomContainer).visibility = View.VISIBLE
            imageListRecyclerAdapter!!.setMultiplePick(true)
        } else findViewById<View>(R.id.llBottomContainer).visibility = View.GONE

        imageListRecyclerAdapter!!.setEventListner(object :
            ImageListRecyclerAdapter.EventListener {

            override fun onItemClickListener(
                position: Int, v: ImageListRecyclerAdapter.VerticalItemHolder
            ) {
                if (imageListRecyclerAdapter!!.isMultiSelected) {
                    imageListRecyclerAdapter!!.changeSelection(
                        v,
                        position
                    )
                } else {
                    val customGallery = imageListRecyclerAdapter!!.getItem(position as Int)
                    val intent = Intent()
                    intent.putExtra("single_path", customGallery.sdcardPath)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        })

//		btnGalleryOk = (Button) findViewById(R.id.btnGalleryOk);
        btnGalleryOk?.setOnClickListener(View.OnClickListener {
            val selected = imageListRecyclerAdapter!!.selected
            val allPath = arrayOfNulls<String>(selected.size)
            for (i in allPath.indices) {
                allPath[i] = selected[i].sdcardPath
            }
            val data = Intent().putExtra("all_path", allPath)
            setResult(RESULT_OK, data)
            finish()
        })
        object : Thread() {
            override fun run() {
                Looper.prepare()
                handler!!.post {
                    imageListRecyclerAdapter!!.addAll(galleryPhotos)
                    //checkImageStatus();
                }
                Looper.loop()
            }
        }.start()
    }

    private fun checkImageStatus() {
        if (recyclerView!!.adapter!!.itemCount > 0) {
            imgNoMedia!!.visibility = View.VISIBLE
        } else {
            imgNoMedia!!.visibility = View.GONE
        }
    }

    // show newest photo at beginning of the list
    private val galleryPhotos: ArrayList<CustomGallery>
        private get() {
            val galleryList = ArrayList<CustomGallery>()
            try {
                val columns = arrayOf(
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID
                )
                val orderBy = MediaStore.Images.Media._ID
                val imagecursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy
                )
                if (imagecursor != null && imagecursor.count > 0) {
                    while (imagecursor.moveToNext()) {
                        val item = CustomGallery()
                        val dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA)
                        item.sdcardPath = imagecursor.getString(dataColumnIndex)
                        galleryList.add(item)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // show newest photo at beginning of the list
            galleryList.reverse()
            return galleryList
        }
    var simpleCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
        0
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
    }
}