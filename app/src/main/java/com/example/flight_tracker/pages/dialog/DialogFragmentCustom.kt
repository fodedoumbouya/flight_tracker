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
class DialogFragmentCustom(private val fromHome : Boolean = false,
                           private val message : String,
                           private val negativBtnMessage : String ) : DialogFragment() {

    /**
     * TODO: Need to check why popBackStack can crash application
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setNegativeButton(negativBtnMessage) { _,_ ->
                // popBackStack only if is not from homeFragment otherwise currentDestination will be null
                // https://developer.android.com/guide/navigation/backstack?hl=fr
                if(!fromHome) {
                    findNavController().popBackStack()
                }
            }
            .create()

}