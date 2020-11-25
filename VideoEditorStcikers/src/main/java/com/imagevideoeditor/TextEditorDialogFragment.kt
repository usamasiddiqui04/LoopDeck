package com.imagevideoeditor

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.imagevideoeditor.FontPickerAdapter.OnFontSelectListner
import java.util.*

/**
 * Created by Burhanuddin Rashid on 1/16/2018.
 */
class TextEditorDialogFragment : DialogFragment() {
    private var mAddTextEditText: EditText? = null
    private var mAddTextDoneTextView: TextView? = null
    private var mInputMethodManager: InputMethodManager? = null
    private var mColorCode = 0
    private var mTextEditor: TextEditor? = null

    interface TextEditor {
        fun onDone(inputText: String?, colorCode: Int, position: Int)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        //Make dialog full screen with transparent background
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_text_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAddTextEditText = view.findViewById(R.id.add_text_edit_text)
        mInputMethodManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mAddTextDoneTextView = view.findViewById(R.id.add_text_done_tv)

        //Setup the color picker for text color
        val addTextColorPickerRecyclerView =
            view.findViewById<View>(R.id.add_text_color_picker_recyclerview) as RecyclerView
        val reyFonts = view.findViewById<View>(R.id.reyFonts) as RecyclerView
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerFonts = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        addTextColorPickerRecyclerView.layoutManager = layoutManager
        reyFonts.layoutManager = layoutManagerFonts
        addTextColorPickerRecyclerView.setHasFixedSize(true)
        reyFonts.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(
            activity!!
        )
        //This listener will change the text color when clicked on any color from picker
        colorPickerAdapter.setOnColorPickerClickListener(object :
            ColorPickerAdapter.OnColorPickerClickListener {
            override fun onColorPickerClickListener(colorCode: Int) {
                mColorCode = colorCode
                mAddTextEditText?.setTextColor(colorCode)
            }
        })
        addTextColorPickerRecyclerView.adapter = colorPickerAdapter
        position = arguments!!.getInt(SELECTED_POSITION)
        val fontPickerAdapter = FontPickerAdapter(
            activity!!, position, getDefaultFonts(
                activity
            )!!, getDefaultFontIds(activity)!!
        )
        fontPickerAdapter.setOnFontSelectListener(object : OnFontSelectListner {
            override fun onFontSelcetion(position: Int) {
                Companion.position = position
                val typeface = Objects.requireNonNull(
                    context
                )?.let {
                    ResourcesCompat.getFont(
                        it, fontIds!![position]
                    )
                }
                mAddTextEditText?.setTypeface(typeface)
            }
        })
        reyFonts.adapter = fontPickerAdapter
        mAddTextEditText?.setText(arguments!!.getString(EXTRA_INPUT_TEXT))
        mColorCode = arguments!!.getInt(EXTRA_COLOR_CODE)
        mAddTextEditText?.setTextColor(mColorCode)
        val typeface = ResourcesCompat.getFont(
            activity!!, fontIds!![position]
        )
        mAddTextEditText?.setTypeface(typeface)
        mInputMethodManager!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        //Make a callback on activity when user is done with text editing
        mAddTextDoneTextView?.setOnClickListener(View.OnClickListener { view ->
            mInputMethodManager!!.hideSoftInputFromWindow(view.windowToken, 0)
            dismiss()
            val inputText = mAddTextEditText?.getText().toString()
            if (!TextUtils.isEmpty(inputText) && mTextEditor != null) {
                mTextEditor!!.onDone(inputText, mColorCode, fontPickerAdapter.selecetedPosition)
            }
        })
    }

    //Callback to listener if user is done with text editing
    fun setOnTextEditorListener(textEditor: TextEditor) {
        mTextEditor = textEditor
    }

    companion object {
        val TAG = TextEditorDialogFragment::class.java.simpleName
        const val EXTRA_INPUT_TEXT = "extra_input_text"
        const val EXTRA_COLOR_CODE = "extra_color_code"
        const val SELECTED_POSITION = "extra_selected_position"
        private var fontNames: MutableList<String>? = null
        private var fontIds: MutableList<Int>? = null
        private var position = 0

        //Show dialog with provide text and text color
        fun show(
            appCompatActivity: AppCompatActivity,
            inputText: String,
            @ColorInt colorCode: Int, position: Int
        ): TextEditorDialogFragment {
            val args = Bundle()
            args.putString(EXTRA_INPUT_TEXT, inputText)
            args.putInt(EXTRA_COLOR_CODE, colorCode)
            args.putInt(SELECTED_POSITION, position)
            val fragment = TextEditorDialogFragment()
            fragment.arguments = args
            fragment.show(appCompatActivity.supportFragmentManager, TAG)
            return fragment
        }

        //Show dialog with default text input as empty and text color white
        fun show(appCompatActivity: AppCompatActivity, position: Int): TextEditorDialogFragment {
            return show(
                appCompatActivity,
                "", ContextCompat.getColor(appCompatActivity, R.color.white), position
            )
        }

        fun getDefaultFontIds(context: Context?): List<Int>? {
            fontIds = ArrayList()
            fontIds?.add(R.font.wonderland)
            fontIds?.add(R.font.cinzel)
            fontIds?.add(R.font.emojione)
            fontIds?.add(R.font.josefinsans)
            fontIds?.add(R.font.merriweather)
            fontIds?.add(R.font.raleway)
            fontIds?.add(R.font.roboto)
            return fontIds
        }

        fun getDefaultFonts(context: Context?): List<String>? {
            fontNames = ArrayList()
            fontNames?.add("Wonderland")
            fontNames?.add("Cinzel")
            fontNames?.add("Emojione")
            fontNames?.add("Josefinsans")
            fontNames?.add("Merriweather")
            fontNames?.add("Raleway")
            fontNames?.add("Roboto")
            return fontNames
        }
    }
}