package com.xorbix.loopdeck.editor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xorbix.loopdeck.R
import kotlinx.android.synthetic.main.text_dialog.*
import kotlinx.android.synthetic.main.textlayout.add_text_color_picker_recyclerview
import kotlinx.android.synthetic.main.textlayout.reyFonts
import java.util.*


/**
 * Created by Burhanuddin Rashid on 1/16/2018.
 */
class TextEditorDialogFragment : DialogFragment() {
    private var mAddTextEditText: EditText? = null
    private var mAddTextDoneTextView: ImageView? = null
    private var close: ImageView? = null
    private var mInputMethodManager: InputMethodManager? = null
    private var mColorCode = 0
    private var mTextEditor: TextEditor? = null
    private var position: Int = 0
    private var check: Boolean = false

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
            dialog.window?.setLayout(width, height)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.text_dialog, container, false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAddTextEditText = view.findViewById(R.id.add_text_edit_text)
        close = view.findViewById(R.id.close)
        mInputMethodManager =
            activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mAddTextDoneTextView = view.findViewById(R.id.add_text_done_tv)
        mAddTextEditText!!.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm =
                    v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                setUI()
                true
            } else false
        })


//        mAddTextEditText!!.setOnClickListener {
//            if (mAddTextEditText!!.text.isNotEmpty()) {
//                add_text_color_picker_relative_layout.visibility = View.VISIBLE
//                add_text_color_picker_recyclerview.visibility = View.GONE
//                mAddTextEditText!!.inputType = InputType.TYPE_NULL
//            }
//        }

        text.setOnClickListener {
            add_text_color_picker_recyclerview.visibility = View.GONE
            reyFonts.visibility = View.VISIBLE
        }

        color.setOnClickListener {
            add_text_color_picker_recyclerview.visibility = View.VISIBLE
            reyFonts.visibility = View.GONE
            check = false
        }

        font.setOnClickListener {
            add_text_color_picker_recyclerview.visibility = View.VISIBLE
            reyFonts.visibility = View.GONE
            check = true
        }

        close!!.setOnClickListener {
            dismiss()
        }


        //Setup the color picker for text color
        val addTextColorPickerRecyclerView = add_text_color_picker_recyclerview

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val layoutManagerFonts =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)

        addTextColorPickerRecyclerView?.layoutManager = layoutManager
        reyFonts?.layoutManager = layoutManagerFonts
        addTextColorPickerRecyclerView?.setHasFixedSize(true)
        reyFonts?.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(activity!!)
        //This listener will change the text color when clicked on any color from picker

        colorPickerAdapter.setOnColorPickerClickListener(object :
            ColorPickerAdapter.OnColorPickerClickListener {
            override fun onColorPickerClicked(colorCode: Int) {
                mColorCode = colorCode
                mAddTextEditText?.setTextColor(colorCode)
            }
        })

        addTextColorPickerRecyclerView?.adapter = colorPickerAdapter
        position = arguments!!.getInt(SELECTED_POSITION)
        val fontPickerAdapter = getDefaultFontIds(activity)?.let {
            FontPickerAdapter(
                activity!!, position, getDefaultFonts(activity)!!,
                it
            )
        }
        fontPickerAdapter?.setOnFontSelectListener(object : FontPickerAdapter.OnFontSelectListner {
            override fun onFontSelcetion(position: Int) {
                this@TextEditorDialogFragment.position = position
                val typeface = ResourcesCompat.getFont(activity!!, fontIds!![position])
                mAddTextEditText?.setTypeface(typeface)
            }

        })

        reyFonts?.adapter = fontPickerAdapter
        mAddTextEditText?.setText(arguments!!.getString(EXTRA_INPUT_TEXT))
        mColorCode = arguments!!.getInt(EXTRA_COLOR_CODE)
        mAddTextEditText?.setTextColor(mColorCode)
        val typeface = ResourcesCompat.getFont(activity!!, fontIds!![position])
        mAddTextEditText?.setTypeface(typeface)
        mInputMethodManager!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        //Make a callback on activity when user is done with text editing
        mAddTextDoneTextView?.setOnClickListener(View.OnClickListener { view ->
            mInputMethodManager!!.hideSoftInputFromWindow(view.windowToken, 0)
            dismiss()
            val inputText = mAddTextEditText?.getText().toString()
            if (!TextUtils.isEmpty(inputText) && mTextEditor != null && fontPickerAdapter != null) {
                mTextEditor!!.onDone(inputText, mColorCode, fontPickerAdapter.selecetedPosition)
            }
        })
    }

    fun setUI() {
        add_text_color_picker_relative_layout.visibility = View.VISIBLE
        add_text_color_picker_recyclerview.visibility = View.GONE
        mAddTextEditText!!.inputType = InputType.TYPE_NULL
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
        @JvmStatic
        fun show(appCompatActivity: AppCompatActivity, position: Int): TextEditorDialogFragment {
            return show(
                appCompatActivity,
                "", ContextCompat.getColor(appCompatActivity, R.color.white), position
            )
        }

        @JvmStatic
        fun getDefaultFontIds(context: Context?): List<Int>? {
            fontIds = ArrayList()
            fontIds?.add(R.font.wonderland)
            fontIds?.add(R.font.cinzel)
            fontIds?.add(R.font.emojione)
            fontIds?.add(R.font.josefinsans)
            fontIds?.add(R.font.merriweather)
            fontIds?.add(R.font.raleway)
            fontIds?.add(R.font.roboto)
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
            fontNames?.add("Aa")
            fontNames?.add("Aa")
            fontNames?.add("Aa")
            fontNames?.add("Aa")
            fontNames?.add("Aa")
            fontNames?.add("Aa")
            fontNames?.add("Aa")
            fontNames?.add("Aa")
            fontNames?.add("Aa")
            fontNames?.add("Aa")
            fontNames?.add("Aa")
            fontNames?.add("Aa")
            fontNames?.add("Aa")
            fontNames?.add("Aa")
            return fontNames
        }
    }
}