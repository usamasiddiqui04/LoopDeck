package com.xorbix.loopdeck.ui.collection.publish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.xorbix.loopdeck.R
import com.xorbix.loopdeck.ui.adapters.PublishAdaptors
import com.xorbix.loopdeck.utils.extensions.activityViewModelProvider
import kotlinx.android.synthetic.main.fragment_publish.*


class PublishFragment : Fragment() {

    private lateinit var viewModel: PublishViewModel

    private val publishAdaptor by lazy {
        PublishAdaptors(
            mList = mutableListOf(),
            requireContext()
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_publish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activityViewModelProvider()

        recyclerviewPublish.adapter = publishAdaptor
        recyclerviewPublish.layoutManager = LinearLayoutManager(context)

        initObservers()

    }

    private fun initObservers() {

        viewModel.publishedLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { list ->
                publishAdaptor.submitList(list.distinctBy { it.name })
            })


    }


}