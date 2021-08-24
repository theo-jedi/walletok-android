package com.theost.walletok.presentation.wallet_details

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class DeleteTransactionDialogFragment : DialogFragment() {

    private var onDeleteClick: (() -> Unit)? = null

    fun setOnDeleteClick(onDelete: () -> Unit) {
        onDeleteClick = onDelete
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity)
            .setTitle("Уверены, что хотите удалить запись?")
            .setNegativeButton("Отменить") { _, _ -> }
            .setPositiveButton("Удалить") { _, _ ->
                onDeleteClick?.invoke()
            }
            .setCancelable(true)
            .create()
    }

    companion object {
        fun newInstance(onDelete: () -> Unit): DeleteTransactionDialogFragment {
            val fragment = DeleteTransactionDialogFragment()
            fragment.setOnDeleteClick(onDelete)
            return fragment
        }
    }
}