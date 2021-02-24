package com.imagevideoeditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.imagevideoeditor.photoeditor.PhotoEditor

class PropertiesBSFragment : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener {
    private var mProperties: Properties? = null

    interface Properties {
        fun onColorChanged(colorCode: Int)
        fun onOpacityChanged(opacity: Int)
        fun onBrushSizeChanged(brushSize: Int)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_properties_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvColor: RecyclerView = view.findViewById(R.id.rvColors)
        val sbOpacity = view.findViewById<SeekBar>(R.id.sbOpacity)
        val sbBrushSize = view.findViewById<SeekBar>(R.id.sbSize)
        sbOpacity.setOnSeekBarChangeListener(this)
        sbBrushSize.setOnSeekBarChangeListener(this)
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        rvColor.layoutManager = layoutManager
        rvColor.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(
            activity!!
        )
        colorPickerAdapter.setOnColorPickerClickListener(object :
            ColorPickerAdapter.OnColorPickerClickListener {
            override fun onColorPickerClicked(colorCode: Int) {
                if (mProperties != null) {
                    dismiss()
                    mProperties!!.onColorChanged(colorCode)
                }
            }
        })
        rvColor.adapter = colorPickerAdapter
    }

    fun setPropertiesChangeListener(properties: Properties?) {
        mProperties = properties
    }

    override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
        val id = seekBar.id
        if (id == R.id.sbOpacity) {
            if (mProperties != null) {
                mProperties!!.onOpacityChanged(i)
            }
        } else if (id == R.id.sbSize) {
            if (mProperties != null) {
                mProperties!!.onBrushSizeChanged(i)
            }
        }
    }

//        switch (seekBar.getId()) {
//            case R.id.sbOpacity:
//                if (mProperties != null) {
//                    mProperties.onOpacityChanged(i);
//                }
//                break;
//            case R.id.sbSize:
//                if (mProperties != null) {
//                    mProperties.onBrushSizeChanged(i);
//                }
//                break;
//        }


    override fun onStartTrackingTouch(seekBar: SeekBar) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {}
}