package com.theost.walletok.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.theost.walletok.R
import com.theost.walletok.presentation.wallet_details.delegates.TransactionAdapterDelegate
import com.theost.walletok.utils.dpToPx
import kotlin.math.min


class WalletDetailsSwipeController(
    val context: Context,
    private val buttonsActions: SwipeControllerActions
) : ItemTouchHelper.Callback() {
    private var swipeBack = false
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null
    private var deleteButtonShowedState = ButtonsState.GONE
    private var editButtonShowedState = ButtonsState.GONE
    private val iconMargin = dpToPx(16f, context).toFloat()
    private val iconSize = dpToPx(40f, context).toFloat()
    private val deleteButtonOffset = iconSize + iconMargin
    private val editButtonOffset = 2 * deleteButtonOffset
    private var editButtonRect: Rect? = null
    private var deleteButtonRect: Rect? = null
    private val deleteButtonDrawable = VectorDrawableCompat.create(
        context.resources,
        R.drawable.ic_delete_button,
        null
    )
    private val editButtonDrawable = VectorDrawableCompat.create(
        context.resources,
        R.drawable.ic_edit_button,
        null
    )

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (viewHolder is TransactionAdapterDelegate.ViewHolder)
            makeMovementFlags(0, LEFT)
        else makeMovementFlags(0, 0)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = deleteButtonShowedState != ButtonsState.GONE
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        if (actionState == ACTION_STATE_SWIPE) {
            if (editButtonShowedState == ButtonsState.VISIBLE) {
                val newDx =
                    min(dX, -editButtonOffset)
                super.onChildDraw(
                    c, recyclerView, viewHolder, newDx, dY, actionState, isCurrentlyActive
                )
            } else
                setTouchListener(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
        }
        drawButtons(c, viewHolder)
        if (editButtonShowedState == ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
        currentItemViewHolder = viewHolder
    }

    fun onDraw(c: Canvas?) {
        if (currentItemViewHolder is TransactionAdapterDelegate.ViewHolder) {
            drawButtons(c!!, currentItemViewHolder!!)
        }
    }

    private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val itemView: View = viewHolder.itemView
        deleteButtonDrawable?.setBounds(0, 0, iconSize.toInt(), iconSize.toInt())
        editButtonDrawable?.setBounds(0, 0, iconSize.toInt(), iconSize.toInt())
        c.save()
        c.translate(
            (itemView.right - iconMargin - iconSize),
            (itemView.top + iconMargin)
        )
        deleteButtonDrawable?.draw(c)
        c.translate((-iconMargin - iconSize), 0f)
        editButtonDrawable?.draw(c)
        c.restore()
        editButtonRect = null
        deleteButtonRect = null
        if (deleteButtonShowedState === ButtonsState.VISIBLE)
            deleteButtonRect = Rect(
                (itemView.right - deleteButtonOffset).toInt(),
                (itemView.top + iconMargin).toInt(),
                (itemView.right - iconMargin).toInt(),
                (itemView.bottom - iconMargin).toInt()
            )
        if (editButtonShowedState === ButtonsState.VISIBLE)
            editButtonRect = Rect(
                (itemView.right - editButtonOffset).toInt(),
                (itemView.top + iconMargin).toInt(),
                (itemView.right - iconSize - iconMargin * 2).toInt(),
                (itemView.bottom - iconMargin).toInt()
            )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            swipeBack =
                event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (dX <= -editButtonOffset) editButtonShowedState = ButtonsState.VISIBLE
                if (dX <= -deleteButtonOffset) deleteButtonShowedState = ButtonsState.VISIBLE
                if (deleteButtonShowedState != ButtonsState.GONE) {
                    setTouchDownListener(
                        c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                    )
                    setItemsClickable(recyclerView, false)
                }
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchDownListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchUpListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super.onChildDraw(
                    c, recyclerView, viewHolder, 0f, dY, actionState, isCurrentlyActive
                )
                recyclerView.setOnTouchListener { _, _ -> false }
                setItemsClickable(recyclerView, true)
                swipeBack = false
                if (deleteButtonRect != null && deleteButtonRect!!.contains(
                        event.x.toInt(),
                        event.y.toInt()
                    )
                ) {
                    if (deleteButtonShowedState == ButtonsState.VISIBLE) {
                        buttonsActions.onDeleteClicked(viewHolder);
                    }
                }
                deleteButtonShowedState = ButtonsState.GONE
                if (editButtonRect != null && editButtonRect!!.contains(
                        event.x.toInt(),
                        event.y.toInt()
                    )
                ) {
                    if (editButtonShowedState == ButtonsState.VISIBLE) {
                        buttonsActions.onEditClicked(viewHolder);
                    }
                }
                editButtonShowedState = ButtonsState.GONE
            }
            false
        }
    }

    private fun setItemsClickable(
        recyclerView: RecyclerView,
        isClickable: Boolean
    ) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }
    }
}

enum class ButtonsState {
    GONE, VISIBLE
}

interface SwipeControllerActions {
    fun onDeleteClicked(viewHolder: RecyclerView.ViewHolder)
    fun onEditClicked(viewHolder: RecyclerView.ViewHolder)
}