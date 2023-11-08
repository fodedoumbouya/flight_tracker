package com.example.flight_tracker.pages.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.flight_tracker.databinding.FragmentDetailsBinding
import com.example.flight_tracker.network.RequestListener
import com.example.flight_tracker.pages.dialog.DialogFragmentCustom
import com.example.flight_tracker.viewModel.DetailsViewModel
import kotlin.properties.Delegates

/**
 * @author by Idricealy on 08/11/2023
 */
class DetailsFragment : Fragment() {
    companion object {
        const val ICAO = "icao"
        const val END_DATE = "endDate"
        const val START_DATE = "startDate"
        const val IS_CHECKED = "isChecked"
    }

    private var TAG = DetailsFragment::class.java.simpleName

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DetailsViewModel
    private lateinit var stringData : TextView
    private lateinit var progressBar : ProgressBar
    private lateinit var icao : String
    private var startDate by Delegates.notNull<Int>()
    private var endDate : Int = 0
    private var isChecked by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            icao = it.getString(ICAO).toString()
            startDate = it.getInt(START_DATE)
            endDate = it.getInt(END_DATE)
            isChecked = it.getBoolean(IS_CHECKED)
        }
    }
    private fun init() {
        viewModel = ViewModelProvider(requireActivity()).get(DetailsViewModel::class.java)
        stringData = binding.stringData
        progressBar = binding.progressBarDetails
        viewModel.searchUiState().value = RequestListener.Loading("Loading...")

        viewModel.doRequest(icao, startDate, endDate, isChecked, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        init()

        viewModel.searchUiState().observe(viewLifecycleOwner) {
            when(it) {
                is RequestListener.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    stringData.visibility = View.GONE
                }
                is RequestListener.Success -> {
                    progressBar.visibility = View.GONE
                    stringData.apply {
                        visibility = View.VISIBLE
                        text = it.data
                    }
                }
                is RequestListener.Error -> {
                    progressBar.visibility = View.GONE
                    DialogFragmentCustom("Failed during the request, check your connection or the API is not available now.", "Try again").show(
                        parentFragmentManager, "DialogFragmentError"
                    )
                }
                is RequestListener.Failed -> {
                    progressBar.visibility = View.GONE
                    DialogFragmentCustom(it.message, "Cancel").show(
                        parentFragmentManager, "DialogFragmentFailed"
                    )
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailsBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}