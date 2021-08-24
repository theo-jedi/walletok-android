package com.theost.walletok

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.theost.walletok.base.BaseAdapter
import com.theost.walletok.data.models.CategoryCreationModel
import com.theost.walletok.databinding.FragmentCategoryEditBinding
import com.theost.walletok.delegates.*
import com.theost.walletok.widgets.CategoryIconListener
import com.theost.walletok.widgets.CategoryListener

class CategoryEditFragment : Fragment() {

    companion object {
        private const val CATEGORY_NEW_KEY = "category_new"
        private const val CATEGORY_ICON_UNSET = -1

        fun newFragment(category: CategoryCreationModel): Fragment {
            val fragment = CategoryEditFragment()
            val bundle = Bundle()
            bundle.putParcelable(CATEGORY_NEW_KEY, category)
            fragment.arguments = bundle
            return fragment
        }

        private const val ICON_COLUMN_COUNT = 6
    }

    private lateinit var categoryListener: CategoryListener
    private lateinit var categoryIconListener: CategoryIconListener
    private lateinit var binding: FragmentCategoryEditBinding
    private lateinit var iconDelegateAdapter: IconAdapterDelegate
    private lateinit var preferencesList: List<Any>

    private var category: CategoryCreationModel? = null
    private var lastSelected = CATEGORY_ICON_UNSET

    private val adapter = BaseAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryEditBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        categoryListener = activity as CategoryListener
        categoryIconListener = activity as CategoryIconListener

        if (category?.color == null) onSetColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.purple
            )
        )

        iconDelegateAdapter = IconAdapterDelegate(category!!.color!!) { position, iconRes ->
            onSetIcon(position, iconRes)
        }

        adapter.apply {
            addDelegate(PreferenceAdapterDelegate { onPreferenceClicked(it) })
            addDelegate(iconDelegateAdapter)
        }

        preferencesList = getPreferencesList()
        if (category?.iconRes != null) {
            lastSelected = preferencesList.indexOfFirst { it is ListIcon && it.iconRes == category?.iconRes }
            (preferencesList[lastSelected] as ListIcon).isSelected = true
        }

        adapter.setData(preferencesList)

        binding.listPreferences.setHasFixedSize(true)
        binding.listPreferences.adapter = adapter

        binding.listPreferences.layoutManager =
            GridLayoutManager(requireContext(), ICON_COLUMN_COUNT).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (adapter.getDelegateClassByPos(position) == IconAdapterDelegate::class) {
                            1
                        } else {
                            ICON_COLUMN_COUNT
                        }
                    }
                }
            }

        binding.submitButton.isEnabled = category!!.isFilled()
        binding.submitButton.setOnClickListener { createCategory() }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        category = if (savedInstanceState != null) {
            savedInstanceState.getParcelable(CATEGORY_NEW_KEY)
        } else {
            arguments?.getParcelable(CATEGORY_NEW_KEY)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(CATEGORY_NEW_KEY, category)
        super.onSaveInstanceState(outState)
    }

    private fun onPreferenceClicked(preferenceName: String) {
        when (preferenceName) {
            PreferenceType.NAME.uiName -> categoryListener.onCategoryNameEdit()
            PreferenceType.TYPE.uiName -> categoryListener.onCategoryTypeEdit()
            PreferenceType.ICON.uiName -> showColorPicker()
        }
    }

    private fun showColorPicker() {
        ColorPickerDialog.Builder(requireContext())
            .setPositiveButton(getString(R.string.submit), object : ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                    val color = envelope?.color
                    if (fromUser && color != null) onColorPicked(color)
                }
            })
            .setNegativeButton(getString(R.string.cancel), null)
            .attachAlphaSlideBar(false)
            .setBottomSpace(12)
            .show()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onColorPicked(color: Int) {
        iconDelegateAdapter.setBackgroundColor(color)
        adapter.notifyDataSetChanged()

        onSetColor(color)
    }

    private fun onSetColor(color: Int) {
        categoryIconListener.onCategoryColorSubmitted(color)
    }

    private fun onSetIcon(position: Int, iconRes: Int) {
        if (lastSelected != CATEGORY_ICON_UNSET) (preferencesList[lastSelected] as ListIcon).isSelected = false
        (preferencesList[position] as ListIcon).isSelected = true
        lastSelected = position

        categoryIconListener.onCategoryIconSubmitted(iconRes)
        category?.iconRes = iconRes
        adapter.setData(preferencesList)

        binding.submitButton.isEnabled = category!!.isFilled()
    }

    private fun createCategory() {
        /* todo createCategory
            if created: categoryListener.onCategoryCreated(), setResult(RESULT_OK) and finish()
            else: showErrorWidget
         */
    }

    private fun getPreferencesList(): List<Any> {
        return listOf(
            TransactionPreference(
                PreferenceType.NAME,
                category?.name ?: getString(R.string.new_category),
                true
            ),
            TransactionPreference(
                PreferenceType.TYPE,
                category?.type ?: getString(R.string.unset),
                true
            ),
            TransactionPreference(
                PreferenceType.ICON, getString(R.string.choose_color),
                true
            ),
            ListIcon(R.drawable.ic_category_plane, false),
            ListIcon(R.drawable.ic_category_plane, false),
            ListIcon(R.drawable.ic_category_plane, false),
            ListIcon(R.drawable.ic_category_plane, false),
            ListIcon(R.drawable.ic_category_plane, false),
            ListIcon(R.drawable.ic_category_plane, false),
        )
    }

}