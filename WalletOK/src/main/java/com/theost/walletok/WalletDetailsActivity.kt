package com.theost.walletok

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.theost.walletok.databinding.ActivityWalletDetailsBinding
import com.theost.walletok.delegates.*
import java.util.*
import com.theost.walletok.utils.dpToPx


class WalletDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletDetailsBinding

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, WalletDetailsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val walletDetailsAdapter = BaseAdapter()
        walletDetailsAdapter.apply {
            addDelegate(WalletDetailsHeaderAdapterDelegate())
            addDelegate(DateAdapterDelegate())
            addDelegate(TransactionAdapterDelegate { /* TODO */ })
            addDelegate(EmptyListAdapterDelegate())
        }
        walletDetailsAdapter.setData(TransactionItemsHelper.getData())
        binding.recycler.apply {
            adapter = walletDetailsAdapter
            layoutManager = LinearLayoutManager(this@WalletDetailsActivity)
            setHasFixedSize(true)
        }
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.addOperationBtn.setOnClickListener {
            Toast.makeText(this, getString(R.string.button_clicked_toast), Toast.LENGTH_SHORT)
                .show()
        }

        val iconMargin = dpToPx(16f, applicationContext)
        val iconSize = dpToPx(40f, applicationContext)

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.ACTION_STATE_IDLE,
            ItemTouchHelper.START
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

            override fun getSwipeDirs(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return if (viewHolder !is TransactionAdapterDelegate.ViewHolder) 0
                else super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val newDx = maxOf(dX, -(3 * iconMargin + 2 * iconSize).toFloat())
                super.onChildDraw(
                    c, recyclerView, viewHolder, newDx, dY, actionState, isCurrentlyActive
                )
            }

        }).attachToRecyclerView(binding.recycler)

        binding.recycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
            val deleteButtonDrawable = VectorDrawableCompat.create(
                applicationContext.resources,
                R.drawable.ic_delete_button,
                null
            )
            val editButtonDrawable = VectorDrawableCompat.create(
                applicationContext.resources,
                R.drawable.ic_edit_button,
                null
            )

            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                parent.children.forEach { item ->
                    val position = parent.getChildLayoutPosition(item)
                    val delegatesIndex = parent.adapter!!.getItemViewType(position)
                    val viewHolderClass =
                        (parent.adapter as WalletDetailsAdapter).delegates[delegatesIndex]::class
                    if (viewHolderClass == TransactionAdapterDelegate::class) {
                        c.save()
                        c.translate(
                            (item.right - iconMargin - iconSize).toFloat(),
                            (item.bottom - iconMargin - iconSize).toFloat()
                        )
                        deleteButtonDrawable?.setBounds(0, 0, iconSize, iconSize)
                        deleteButtonDrawable?.draw(c)
                        c.translate((-iconMargin - iconSize).toFloat(), 0f)
                        editButtonDrawable?.setBounds(0, 0, iconSize, iconSize)
                        editButtonDrawable?.draw(c)
                        c.restore()
                    }
                }
                super.onDraw(c, parent, state)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_wallet_details, menu)
        return true
    }
}