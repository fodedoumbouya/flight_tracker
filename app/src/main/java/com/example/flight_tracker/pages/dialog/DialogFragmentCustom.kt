package com.example.flight_tracker.pages.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DialogFragmentCustom : DialogFragment() {

    private lateinit var message: String
    private lateinit var negativeBtnMessage: String
    private lateinit var listener: CustomDialogListener

    interface CustomDialogListener {
        fun onNegativeButtonClickDialogFragment()
    }

    companion object {
        private const val ARG_MESSAGE = "arg_message"
        private const val ARG_NEGATIVE_BTN_MESSAGE = "arg_negative_btn_message"

        fun newInstance(
            message: String,
            negativeBtnMessage: String,
            listener: CustomDialogListener
        ): DialogFragmentCustom {
            val fragment = DialogFragmentCustom()
            val args = Bundle().apply {
                putString(ARG_MESSAGE, message)
                putString(ARG_NEGATIVE_BTN_MESSAGE, negativeBtnMessage)
            }
            fragment.arguments = args
            fragment.listener = listener
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = arguments?.getString(ARG_MESSAGE) ?: ""
        val negativeBtnMessage = arguments?.getString(ARG_NEGATIVE_BTN_MESSAGE) ?: ""

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(message)
            .setNegativeButton(negativeBtnMessage) { _, _ ->
                listener?.onNegativeButtonClickDialogFragment()
            }

        return builder.create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        retainInstance = true
    }
}
