package com.example.flight_tracker.pages.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController

/**
 * @author by Idricealy on 08/11/2023
 *
 */

class DialogFragmentCustom(
    private val message: String,
    private val negativeBtnMessage: String,
    private val listener: CustomDialogListener
) : DialogFragment() {
    interface CustomDialogListener {
        fun onNegativeButtonClickDialogFragment()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(message)
            .setNegativeButton(negativeBtnMessage) { _, _ ->
                listener.onNegativeButtonClickDialogFragment()
            }

        return builder.create()
    }
}

