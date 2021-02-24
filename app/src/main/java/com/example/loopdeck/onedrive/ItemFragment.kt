// ------------------------------------------------------------------------------
// Copyright (c) 2015 Microsoft Corporation
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
// ------------------------------------------------------------------------------
package com.example.loopdeck.onedrive

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.*
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.loopdeck.BaseApplication
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaRepository
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.utils.extensions.activityViewModelProvider
import com.example.loopdeck.utils.extensions.toast
import com.onedrive.sdk.authentication.AccountType
import com.onedrive.sdk.concurrency.AsyncMonitor
import com.onedrive.sdk.concurrency.ICallback
import com.onedrive.sdk.concurrency.IProgressCallback
import com.onedrive.sdk.core.ClientException
import com.onedrive.sdk.core.OneDriveErrorCodes
import com.onedrive.sdk.extensions.*
import com.onedrive.sdk.options.Option
import com.onedrive.sdk.options.QueryOption
import com.xorbix.loopdeck.cameraapp.BitmapUtils.ROOT_DIRECTORY_NAME
import kotlinx.android.synthetic.main.activity_api_explorer.*
import org.json.JSONObject
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Handles interacting with Items on OneDrive
 */
class ItemFragment : Fragment(), AdapterView.OnItemClickListener {
    private var viewModel: CollectionViewModel? = null

    /**
     * The item id for this item
     */
    private var mItemId: String? = null

    /**
     * The backing item representation
     */
    private var mItem: Item? = null

    /**
     * The listener for interacting with this fragment
     */
    private var mListener: OnFragmentInteractionListener? = null

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private var mAdapter: DisplayItemAdapter? = null

    /**
     * If the current fragment should prioritize the empty view over the visualization
     */
    private val mEmpty = AtomicBoolean(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activityViewModelProvider()
        mAdapter = DisplayItemAdapter(activity)
        val app = activity?.application as BaseApplication
        if (app.goToWifiSettingsIfDisconnected()) {
            return
        }
        if (arguments != null) {
            mItemId = arguments?.getString(ARG_ITEM_ID)
        }
        if (mItem != null) {
            (activity?.findViewById<View>(R.id.fragment_label) as TextView).text =
                mItem!!.parentReference.path
        } else {
            (activity?.findViewById<View>(R.id.fragment_label) as TextView).text = null
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_folder, container, false)
        val mListView = view.findViewById<View>(android.R.id.list) as AbsListView
        mListView.adapter = mAdapter
        mListView.onItemClickListener = this
        (view.findViewById<View>(android.R.id.button1) as RadioButton).setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setFocus(ItemFocus.Visualization, getView())
            }
        }
        (view.findViewById<View>(android.R.id.button2) as RadioButton).setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setFocus(ItemFocus.Json, getView())
            }
        }
        (view.findViewById<View>(R.id.json) as TextView).movementMethod = ScrollingMovementMethod()
        (view.findViewById<View>(R.id.close) as ImageView).setOnClickListener {
            requireActivity().finish()
        }
        refresh()
        return view
    }

    // onAttach(Context) never gets called on API22 and earlier devices
    override fun onAttach(context: Activity) {
        super.onAttach(context)
        mListener = context as OnFragmentInteractionListener
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (mItem != null) {
            // Add menu options
            inflater.inflate(R.menu.menu_item_fragment, menu)

            // Assume we are a folder first
            menu.findItem(R.id.action_download).isVisible = false
            menu.findItem(R.id.action_copy).isVisible = false
            configureSetCopyDestinationMenuItem(menu.findItem(R.id.action_set_copy_destination))


            // Make sure that the root folder has certain options unavailable
            if ("root".equals(mItemId, ignoreCase = true)) {
                menu.findItem(R.id.action_rename).isVisible = false
                menu.findItem(R.id.action_delete).isVisible = false
            }

            // Make sure that if it is a file, we don't let you perform actions that don't make sense for files
            if (mItem!!.file != null) {
                menu.findItem(R.id.action_create_folder).isVisible = false
                menu.findItem(R.id.action_upload_file).isVisible = false
                menu.findItem(R.id.action_download).isVisible = true
                menu.findItem(R.id.action_copy).isVisible = true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mItem == null) {
            return false
        }
        val itemId = item.itemId
        if (itemId == R.id.action_copy) {
            copy(mItem!!)
            return true
        } else if (itemId == R.id.action_set_copy_destination) {
            setCopyDestination(mItem!!)
            return true
        } else if (itemId == R.id.action_upload_file) {
            upload(REQUEST_CODE_SIMPLE_UPLOAD)
            return true
        } else if (itemId == R.id.action_refresh) {
            refresh()
            return true
        } else if (itemId == R.id.action_create_folder) {
            createFolder(mItem!!)
            return true
        } else if (itemId == R.id.action_rename) {
            renameItem(mItem!!)
            return true
        } else if (itemId == R.id.action_delete) {
            deleteItem(mItem!!)
            return true
        } else if (itemId == R.id.action_download) {
            download(mItem!!)
            return true
        } else if (itemId == R.id.action_create_link) {
//            createLink(mItem!!)
            return true
        } else if (itemId == R.id.action_view_delta) {
            viewDelta(mItem!!)
            return true
        } else if (itemId == R.id.action_navigate_by_path) {
            navigateByPath(mItem!!)
            return true
        }
        return false
    }

    /**
     * Sets the copy destination within the preferences
     *
     * @param item The item to mark as the destination
     */
    private fun setCopyDestination(item: Item) {
        copyPrefs.edit().putString(COPY_DESTINATION_PREF_KEY, item.id).commit()
        activity?.invalidateOptionsMenu()
    }

    /**
     * Copies an item onto the current destination in the copy preferences
     *
     * @param item The item to copy
     */
    private fun copy(item: Item) {
        val app = activity?.application as BaseApplication
        val oneDriveClient = app.oneDriveClient
        val parentReference = ItemReference()
        parentReference.id = copyPrefs.getString(COPY_DESTINATION_PREF_KEY, null)
        val dialog = ProgressDialog(activity, ProgressDialog.STYLE_HORIZONTAL)
        dialog.setTitle("Copying item")
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dialog.setMessage("Waiting for copy to complete")
        val progressCallback: IProgressCallback<Item?> = object : IProgressCallback<Item?> {
            override fun progress(current: Long, max: Long) {
                dialog.max = current.toInt()
                dialog.max = max.toInt()
            }

            override fun success(result: Item?) {
                dialog.dismiss()
                val string = getString(
                    R.string.copy_success_message,
                    item.name,
                    item.parentReference.path
                )
                Toast.makeText(activity, string, Toast.LENGTH_LONG).show()
            }

            override fun failure(error: ClientException) {
                dialog.dismiss()
                AlertDialog.Builder(activity)
                    .setTitle(R.string.error_title)
                    .setMessage(error.message)
                    .setNegativeButton(R.string.close) { dialog, which -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }
        val callback: DefaultCallback<AsyncMonitor<Item?>?> =
            object : DefaultCallback<AsyncMonitor<Item?>?>(
                activity
            ) {
                override fun success(result: AsyncMonitor<Item?>?) {
                    val millisBetweenPoll = 1000
                    result!!.pollForResult(millisBetweenPoll.toLong(), progressCallback)
                }
            }
        oneDriveClient
            .drive
            .getItems(item.id)
            .getCopy(item.name, parentReference)
            .buildRequest()
            .create(callback)
        dialog.show()
    }

    override fun onItemClick(
        parent: AdapterView<*>?,
        view: View, position: Int,
        id: Long
    ) {
        if (null != mListener) {
//            mListener!!.onFragmentInteraction(mAdapter!!.getItem(position) as DisplayItem)
            download(mAdapter!!.getItem(position)!!.item)
        }
    }

    override fun onPause() {
        super.onPause()
        mAdapter!!.stopDownloadingThumbnails()
    }

    /**
     * Creates a callback for drilling into an item
     *
     * @param context The application context to display messages
     * @return The callback to refresh this item with
     */
    private fun getItemCallback(context: BaseApplication): DefaultCallback<Item?> {
        return object : DefaultCallback<Item?>(context) {
            override fun success(result: Item?) {
                mItem = result
                if (view != null) {
                    val mListView = view!!.findViewById<View>(android.R.id.list) as AbsListView
                    val adapter = mListView.adapter as DisplayItemAdapter
                    adapter.clear()
                    var text: String? = null
                    try {
                        val rawString = result!!.rawObject.toString()
                        val `object` = JSONObject(rawString)
                        val intentSize = 3
                        text = `object`.toString(intentSize)
                    } catch (e: Exception) {
                        Log.e(javaClass.name, "Unable to parse the response body to json")
                    }
                    if (text != null) {
                        (view!!.findViewById<View>(R.id.json) as TextView).text = text
                    }
                    val fragmentLabel: String
                    fragmentLabel = if (mItem!!.parentReference != null) {
                        (mItem!!.parentReference.path
                                + context.getString(R.string.item_path_separator)
                                + mItem!!.name)
                    } else {
                        DRIVE_PREFIX + mItem!!.name
                    }
                    (activity!!.findViewById<View>(R.id.fragment_label) as TextView).text =
                        fragmentLabel
                    mEmpty.set(result!!.children == null || result.children.currentPage.isEmpty())
                    if (result.children == null || result.children.currentPage.isEmpty()) {
                        val emptyText = view!!.findViewById<View>(android.R.id.empty) as TextView
                        if (result.folder != null) {
                            emptyText.setText(R.string.empty_list)
                        } else {
                            emptyText.setText(R.string.empty_file)
                        }
                        setFocus(ItemFocus.Empty, view)
                    } else {
                        for (childItem in result.children.currentPage) {
                            adapter.add(
                                DisplayItem(
                                    adapter,
                                    childItem,
                                    childItem.id,
                                    context.imageCache
                                )
                            )
                        }
                        setFocus(ItemFocus.Visualization, view)
                    }
                    activity!!.invalidateOptionsMenu()
                }
            }

            override fun failure(error: ClientException) {
                if (view != null) {
                    val view = view!!.findViewById<View>(android.R.id.empty) as TextView
                    view.text = context.getString(R.string.item_fragment_item_lookup_error, mItemId)
                    setFocus(ItemFocus.Empty, getView())
                }
            }
        }
    }

    /**
     * Refreshes the data for this fragment
     */
    private fun refresh() {
        if (view != null) {
            setFocus(ItemFocus.Progress, view)
        }
        mItem = null
        val app = activity?.application as BaseApplication
        val oneDriveClient = app.oneDriveClient
        val itemCallback = getItemCallback(app)
        val itemId: String?
        itemId = if (mItemId == "root") {
            "root"
        } else {
            mItemId
        }
        oneDriveClient
            .drive
            .getItems(itemId)
            .buildRequest()
            .expand(getExpansionOptions(oneDriveClient))[itemCallback]
    }

    /**
     * Gets the expansion options for requests on items
     *
     * @param oneDriveClient the OneDrive client
     * @return The string for expand options
     * @see {https://github.com/OneDrive/onedrive-api-docs/issues/203}
     */
    private fun getExpansionOptions(oneDriveClient: IOneDriveClient): String {
        val expansionOption: String
        expansionOption = when (oneDriveClient.authenticator.accountInfo.accountType) {
            AccountType.MicrosoftAccount -> EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS
            else -> EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS_LIMITED
        }
        return expansionOption
    }

    /**
     * Deletes the item represented by this fragment
     *
     * @param item The item to delete
     */
    private fun deleteItem(item: Item) {
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(R.string.delete)
            .setIcon(android.R.drawable.ic_delete)
            .setMessage(requireContext().getString(R.string.confirm_delete_action, mItem!!.name))
            .setPositiveButton(R.string.delete) { dialog, which ->
                val application = requireActivity()
                    .getApplication() as BaseApplication
                application.oneDriveClient
                    .drive
                    .getItems(item.id)
                    .buildRequest()
                    .delete(object : DefaultCallback<Void?>(application) {
                        override fun success(result: Void?) {
                            Toast.makeText(
                                activity,
                                application.getString(
                                    R.string.deleted_this_item,
                                    item.name
                                ),
                                Toast.LENGTH_LONG
                            ).show()
                            activity!!.onBackPressed()
                        }
                    })
            }
            .setNegativeButton(R.string.cancel) { dialog, which -> dialog.cancel() }
            .create()
        alertDialog.show()
    }

    /**
     * Creates a link on this item
     *
     * @param item The item to delete
     */
//    private fun createLink(item: Item) {
//        val items = arrayOf<CharSequence>("view", "edit")
//        val nothingSelected = -1
//        val selection = AtomicInteger(nothingSelected)
//        val alertDialog = AlertDialog.Builder(activity)
//            .setTitle(R.string.create_link)
//            .setIcon(android.R.drawable.ic_menu_share)
//            .setPositiveButton(
//                R.string.create_link,
//                DialogInterface.OnClickListener { dialog, which ->
//                    if (selection.get() == nothingSelected) {
//                        return@OnClickListener
//                    }
//                    val application = requireActivity()
//                        .getApplication() as BaseApplication
//                    application.oneDriveClient
//                        .drive
//                        .getItems(item.id)
//                        .getCreateLink(items[selection.get()].toString())
//                        .buildRequest()
//                        .create(object : DefaultCallback<Permission?>(activity) {
//                            override fun success(result: Permission?) {
//                                var cm = requireActivity()
//                                    .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                                var data = ClipData.newPlainText("Link Url", result!!.link.webUrl)
//                                cm.primaryClip = data
//                                Toast.makeText(
//                                    activity,
//                                    application.getString(R.string.created_link),
//                                    Toast.LENGTH_LONG
//                                ).show()
//                                activity!!.onBackPressed()
//                            }
//                        })
//                })
//            .setSingleChoiceItems(items, 0) { dialog, which -> selection.set(which) }
//            .setNegativeButton(R.string.cancel) { dialog, which -> dialog.cancel() }
//            .create()
//        alertDialog.show()
//    }

    /**
     * Renames a sourceItem
     *
     * @param sourceItem The sourceItem to rename
     */
    private fun renameItem(sourceItem: Item) {
        val activity: Activity? = activity
        val newName = EditText(activity)
        newName.inputType = InputType.TYPE_CLASS_TEXT
        newName.hint = sourceItem.name
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(R.string.rename)
            .setIcon(android.R.drawable.ic_menu_edit)
            .setView(newName)
            .setPositiveButton(R.string.rename) { dialog, which ->
                val callback: DefaultCallback<Item?> =
                    object : DefaultCallback<Item?>(getActivity()) {
                        override fun success(result: Item?) {
                            Toast.makeText(
                                activity,
                                activity!!
                                    .getString(
                                        R.string.renamed_item, sourceItem.name,
                                        result!!.name
                                    ),
                                Toast.LENGTH_LONG
                            ).show()
                            refresh()
                            dialog.dismiss()
                        }

                        override fun failure(error: ClientException) {
                            Toast.makeText(
                                activity,
                                activity!!.getString(
                                    R.string.rename_error,
                                    sourceItem.name
                                ),
                                Toast.LENGTH_LONG
                            ).show()
                            dialog.dismiss()
                        }
                    }
                val updatedItem = Item()
                updatedItem.id = sourceItem.id
                updatedItem.name = newName.text.toString()
                (requireActivity().application as BaseApplication)
                    .oneDriveClient
                    .drive
                    .getItems(updatedItem.id)
                    .buildRequest()
                    .update(updatedItem, callback)
            }
            .setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
            .create()
        alertDialog.show()
    }

    /**
     * Creates a folder
     *
     * @param item The parent of the folder to create
     */
    private fun createFolder(item: Item) {
        val activity: Activity? = activity
        val newName = EditText(activity)
        newName.inputType = InputType.TYPE_CLASS_TEXT
        newName.hint = requireActivity().getString(R.string.new_folder_hint)
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(R.string.create_folder)
            .setView(newName)
            .setIcon(android.R.drawable.ic_menu_add)
            .setPositiveButton(R.string.create_folder) { dialog, which ->
                val callback: DefaultCallback<Item?> = object : DefaultCallback<Item?>(activity) {
                    override fun success(result: Item?) {
                        Toast.makeText(
                            activity,
                            activity!!.getString(
                                R.string.created_folder,
                                result!!.name,
                                item.name
                            ),
                            Toast.LENGTH_LONG
                        )
                            .show()
                        refresh()
                        dialog.dismiss()
                    }

                    override fun failure(error: ClientException) {
                        super.failure(error)
                        Toast.makeText(
                            activity,
                            activity!!.getString(
                                R.string.new_folder_error,
                                item.name
                            ),
                            Toast.LENGTH_LONG
                        )
                            .show()
                        dialog.dismiss()
                    }
                }
                val newItem = Item()
                newItem.name = newName.text.toString()
                newItem.folder = Folder()
                (requireActivity().application as BaseApplication)
                    .oneDriveClient
                    .drive
                    .getItems(mItemId)
                    .children
                    .buildRequest()
                    .create(newItem, callback)
            }
            .setNegativeButton(R.string.cancel) { dialog, which -> dialog.cancel() }
            .create()
        alertDialog.show()
    }

    /**
     * Starts the uploading experience
     *
     * @param requestCode The request code that will be used to choose simple/chunked uploading
     */
    private fun upload(requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.type = ACCEPTED_UPLOAD_MIME_TYPES
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val application = requireActivity().application as BaseApplication
        val oneDriveClient = application.oneDriveClient
        if (requestCode == REQUEST_CODE_SIMPLE_UPLOAD && data != null && data.data != null && data.data!!.scheme.equals(
                SCHEME_CONTENT, ignoreCase = true
            )
        ) {
            val dialog = ProgressDialog(activity)
            dialog.setTitle(R.string.upload_in_progress_title)
            dialog.setMessage(getString(R.string.upload_in_progress_message))
            dialog.isIndeterminate = false
            dialog.setCancelable(false)
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            dialog.setProgressNumberFormat(getString(R.string.upload_in_progress_number_format))
            dialog.show()
            @SuppressLint("StaticFieldLeak") val uploadFile: AsyncTask<Void?, Void?, Void?> =
                object : AsyncTask<Void?, Void?, Void?>() {
                    override fun doInBackground(vararg p0: Void?): Void? {
                        try {
                            val contentResolver = activity!!.contentResolver
                            val contentProvider: ContentProviderClient? = contentResolver
                                .acquireContentProviderClient(data.data!!)
                            val fileInMemory = FileContent.getFileBytes(contentProvider, data.data)
                            contentProvider!!.release()

                            // Fix up the file name (needed for camera roll photos, etc)
                            val filename = FileContent.getValidFileName(contentResolver, data.data)
                            val option: Option = QueryOption("@name.conflictBehavior", "fail")
                            oneDriveClient
                                .drive
                                .getItems(mItemId)
                                .children
                                .byId(filename)
                                .content
                                .buildRequest(listOf(option))
                                .put(fileInMemory,
                                    object : IProgressCallback<Item?> {
                                        override fun success(result: Item?) {
                                            dialog.dismiss()
                                            Toast.makeText(
                                                activity,
                                                application
                                                    .getString(
                                                        R.string.upload_complete,
                                                        result!!.name
                                                    ),
                                                Toast.LENGTH_LONG
                                            ).show()
                                            refresh()
                                        }

                                        override fun failure(error: ClientException) {
                                            dialog.dismiss()
                                            if (error.isError(OneDriveErrorCodes.NameAlreadyExists)) {
                                                Toast.makeText(
                                                    activity,
                                                    R.string.upload_failed_name_conflict,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } else {
                                                Toast.makeText(
                                                    activity,
                                                    application
                                                        .getString(
                                                            R.string.upload_failed,
                                                            filename
                                                        ),
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }

                                        override fun progress(current: Long, max: Long) {
                                            dialog.progress = current.toInt()
                                            dialog.max = max.toInt()
                                        }
                                    })
                        } catch (e: Exception) {
                            e.message?.let { Log.e(javaClass.simpleName, it) }
                            Log.e(javaClass.simpleName, e.toString())
                        }
                        return null
                    }
                }
            uploadFile.execute()
        }
    }

    /**
     * Downloads this item
     *
     * @param item The item to download
     */
    fun download(item: Item) {
        val storageDir = File(
            requireContext().getExternalFilesDir(null)!!.absolutePath,
            ROOT_DIRECTORY_NAME
        )
        val activity: Activity? = activity
        val downloadManager =
            requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUrl = item.rawObject["@content.downloadUrl"].asString
        val request = DownloadManager.Request(Uri.parse(downloadUrl))
        request.setTitle(item.name)
        request.setDescription(requireActivity().getString(R.string.file_from_onedrive))
        request.allowScanningByMediaScanner()
        request.setDestinationInExternalFilesDir(
            requireContext(), ROOT_DIRECTORY_NAME,
            item.name
        )
        val file = File(storageDir.absolutePath, item.name)
        toast(file.toString())
        viewModel!!.createPlaylist(file)
        if (item.file != null) {
            request.setMimeType(item.file.mimeType)
        }
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadManager.enqueue(request)
        Toast.makeText(
            activity, requireActivity().getString(R.string.starting_download_message),
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Starts up a new View Delta viewer
     *
     * @param item The item to delta over
     */
    private fun viewDelta(item: Item) {
        val fragment = DeltaFragment.newInstance(item)
        navigateToFragment(fragment)
    }

    /**
     * Navigate to a new fragment
     *
     * @param fragment the fragment to navigate into
     */
    private fun navigateToFragment(fragment: Fragment) {
        mAdapter!!.stopDownloadingThumbnails()
        childFragmentManager
            .beginTransaction()
            .add(R.id.fragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * Navigates to an item by path
     *
     * @param item the source item
     */
    private fun navigateByPath(item: Item) {
        val application = requireActivity().application as BaseApplication
        val oneDriveClient = application.oneDriveClient
        val activity: Activity? = activity
        val itemPath = EditText(activity)
        itemPath.inputType = InputType.TYPE_CLASS_TEXT
        val itemCallback: DefaultCallback<Item?> = object : DefaultCallback<Item?>(activity) {
            override fun success(result: Item?) {
                val fragment = newInstance(result!!.id)
                navigateToFragment(fragment)
            }
        }
        AlertDialog.Builder(activity)
            .setIcon(android.R.drawable.ic_dialog_map)
            .setTitle(R.string.navigate_by_path)
            .setView(itemPath)
            .setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
            .setPositiveButton(R.string.navigate) { dialog, which ->
                oneDriveClient
                    .drive
                    .getItems(item.id)
                    .getItemWithPath(itemPath.text.toString())
                    .buildRequest()
                    .expand(getExpansionOptions(oneDriveClient))[itemCallback]
            }
            .create()
            .show()
    }

    /**
     * Sets the focus on one of the primary fixtures of this fragment
     *
     * @param focus The focus to appear
     * @param view  the root of the fragment
     */
    private fun setFocus(focus: ItemFocus, view: View?) {
        var actualFocus = focus
        if (focus == ItemFocus.Visualization && mEmpty.get()) {
            actualFocus = ItemFocus.Empty
        }
        for (focusable in ItemFocus.values()) {
            if (focusable == actualFocus) {
                requireView().findViewById<View>(focusable.mId).visibility = View.VISIBLE
            } else {
                requireView().findViewById<View>(focusable.mId).visibility = View.GONE
            }
        }
    }

    /**
     * Configure the SetCopyDestination menu item
     *
     * @param item The menu item for SetCopyDestination
     */
    private fun configureSetCopyDestinationMenuItem(item: MenuItem) {
        if (mItem!!.file != null) {
            item.isVisible = false
        } else {
            item.isVisible = true
            item.isChecked = false
            if (copyPrefs.getString(COPY_DESTINATION_PREF_KEY, null) != null) {
                item.isChecked = true
            }
        }
    }

    /**
     * Get the copy preferences
     *
     * @return The copy preferences
     */
    private val copyPrefs: SharedPreferences
        private get() = requireActivity().getSharedPreferences("copy", Context.MODE_PRIVATE)

    /**
     * The available fixtures to get focus
     */
    private enum class ItemFocus
    /**
     * The default constructor
     *
     * @param id the resource id for this item
     */(
        /**
         * The resource id for the item
         */
        val mId: Int
    ) {
        /**
         * The visualization pane
         */
        Visualization(android.R.id.list),

        /**
         * The json response pane
         */
        Json(R.id.json),

        /**
         * The 'empty view' pane
         */
        Empty(android.R.id.empty),

        /**
         * The in progress pane
         */
        Progress(android.R.id.progress);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        /**
         * Action when fragments are interacted with
         *
         * @param item The item that was interacted with
         */
        fun onFragmentInteraction(item: DisplayItem?)
    }

    companion object {
        /**
         * The item id argument string
         */
        private const val ARG_ITEM_ID = "itemId"

        /**
         * The request code for simple upload
         */
        private const val REQUEST_CODE_SIMPLE_UPLOAD = 6767

        /**
         * The scheme to get content from a content resolver
         */
        private const val SCHEME_CONTENT = "content"

        /**
         * The prefix for the item breadcrumb when the parent reference is unavailable
         */
        private const val DRIVE_PREFIX = "/drive/"

        /**
         * Expansion options to get all children, thumbnails of children, and thumbnails
         */
        private const val EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS =
            "children(expand=thumbnails),thumbnails"

        /**
         * Expansion options to get all children, thumbnails of children, and thumbnails when limited
         */
        private const val EXPAND_OPTIONS_FOR_CHILDREN_AND_THUMBNAILS_LIMITED = "children,thumbnails"

        /**
         * The accepted file mime types for uploading to OneDrive
         */
        private const val ACCEPTED_UPLOAD_MIME_TYPES = "*/*"

        /**
         * The copy destination preference key
         */
        private const val COPY_DESTINATION_PREF_KEY = "copy_destination"

        /**
         * Create a new instance of ItemFragment
         *
         * @param itemId The item id to create it for
         * @return The fragment
         */
        @JvmStatic
        fun newInstance(itemId: String?): ItemFragment {
            val fragment = ItemFragment()
            val args = Bundle()
            args.putString(ARG_ITEM_ID, itemId)
            fragment.arguments = args
            return fragment
        }
    }
}