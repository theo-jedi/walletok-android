package com.theost.walletok.presentation.wallet_details.category

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.theost.walletok.R
import com.theost.walletok.data.models.CategoryCreationModel
import com.theost.walletok.data.models.TransactionCategoryType
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.databinding.FragmentCategoryEditBinding
import com.theost.walletok.delegates.*
import com.theost.walletok.presentation.base.BaseAdapter
import com.theost.walletok.presentation.base.ErrorMessageHelper
import com.theost.walletok.utils.addTo
import com.theost.walletok.widgets.CategoryListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class CategoryEditFragment : Fragment() {

    companion object {
        private const val CATEGORY_NEW_KEY = "category_new"

        fun newFragment(category: CategoryCreationModel?): Fragment {
            val fragment = CategoryEditFragment()
            val bundle = Bundle()
            bundle.putParcelable(CATEGORY_NEW_KEY, category)
            fragment.arguments = bundle
            return fragment
        }

        private const val ICON_COLUMN_COUNT = 6
    }

    private lateinit var binding: FragmentCategoryEditBinding
    private lateinit var iconDelegateAdapter: IconAdapterDelegate
    private lateinit var preferencesList: MutableList<Any>

    private var category: CategoryCreationModel? = null

    private val adapter = BaseAdapter()
    private val compositeDisposable = CompositeDisposable()
    private val viewModel: CategoryCreationViewModel by viewModels()

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

        binding.submitButton.setOnClickListener { createCategory() }

        iconDelegateAdapter = IconAdapterDelegate { iconRes ->
            viewModel.setIcon(iconRes)
        }

        adapter.apply {
            addDelegate(PreferenceAdapterDelegate { onPreferenceClicked(it) })
            addDelegate(iconDelegateAdapter)
        }

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

        viewModel.allData.observe(viewLifecycleOwner) { categoryModel ->
            category = categoryModel
            preferencesList = getPreferencesList()

            updateIcon()
            updateIconColor()

            binding.submitButton.isEnabled = category!!.isFilled()

            adapter.setData(preferencesList)
        }

        viewModel.loadData(category)

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

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun onPreferenceClicked(preferenceName: String) {
        when (preferenceName) {
            PreferenceType.NAME.uiName -> (activity as CategoryListener).onCategoryNameEdit()
            PreferenceType.TYPE.uiName -> (activity as CategoryListener).onCategoryTypeEdit()
            PreferenceType.ICON.uiName -> showColorPicker()
        }
    }

    private fun showColorPicker() {
        ColorPickerDialog.Builder(requireContext())
            .setPositiveButton(getString(R.string.submit), object : ColorEnvelopeListener {
                override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                    val color = envelope?.color
                    if (fromUser && color != null) updateIconColor(color)
                }
            })
            .setNegativeButton(getString(R.string.cancel), null)
            .attachAlphaSlideBar(false)
            .setBottomSpace(12)
            .show()
    }

    private fun createCategory() {
        CategoriesRepository.addCategory(
            category!!.name!!,
            category!!.iconRes!!,
            TransactionCategoryType.values().find { it.uiName == category!!.type!!}!!
        ).subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                (activity as CategoryListener).onCategoryCreated()
            }, {
                ErrorMessageHelper.setUpErrorMessage(binding.errorWidget) {
                    createCategory()
                }
            }).addTo(compositeDisposable)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateIconColor(color: Int? = null) {
        val green = ContextCompat.getColor(requireContext(), R.color.green)
        val purple = ContextCompat.getColor(requireContext(), R.color.purple)
        if (color != null) {
            viewModel.setColor(color)
            category!!.color = color
        } else if (category!!.type == TransactionCategoryType.INCOME.uiName) {
            viewModel.setColor(green)
            category!!.color = green
        } else if (category!!.color == null || category!!.color == green) {
            viewModel.setColor(purple)
            category!!.color = purple
        }
        iconDelegateAdapter.setBackgroundColor(category!!.color!!)
        adapter.notifyDataSetChanged()
    }

    private fun updateIcon() {
        if (category!!.iconRes != null) {
            iconDelegateAdapter.setSelectedIcon(category!!.iconRes!!)
        }
    }

    private fun getPreferencesList(): MutableList<Any> {
        val list = mutableListOf(
            TransactionPreference(
                PreferenceType.NAME,
                category?.name ?: getString(R.string.unset),true
            ),
            TransactionPreference(
                PreferenceType.TYPE,
                category?.type ?: getString(R.string.unset),true
            ),
            TransactionPreference(
                PreferenceType.ICON, getString(R.string.choose_color),true
            ),
            ListIcon(R.drawable.ic_category_plane),
            ListIcon(R.drawable.ic_category_plane),
            ListIcon(R.drawable.ic_category_plane),
            ListIcon(R.drawable.ic_category_plane),
            ListIcon(R.drawable.ic_category_gas),
            ListIcon(R.drawable.ic_category_food),
        )
        if (category!!.type != TransactionCategoryType.EXPENSE.uiName) {
            list.remove(list.find {
                it is TransactionPreference && it.type == PreferenceType.ICON
            })
        }

        return list
    }

}