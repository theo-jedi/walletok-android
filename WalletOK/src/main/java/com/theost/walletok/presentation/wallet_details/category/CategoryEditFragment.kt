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
import com.theost.walletok.R
import com.theost.walletok.data.models.CategoryCreationModel
import com.theost.walletok.data.models.TransactionCategoryType
import com.theost.walletok.databinding.FragmentCategoryEditBinding
import com.theost.walletok.delegates.*
import com.theost.walletok.presentation.base.BaseAdapter
import com.theost.walletok.presentation.wallet_details.transaction.widgets.CategoryListener
import io.reactivex.disposables.CompositeDisposable

class CategoryEditFragment : Fragment() {

    companion object {
        private const val CATEGORY_NEW_KEY = "category_new"

        fun newFragment(category: CategoryCreationModel? = null): Fragment {
            val fragment = CategoryEditFragment()
            val bundle = Bundle()
            bundle.putParcelable(CATEGORY_NEW_KEY, category)
            fragment.arguments = bundle
            return fragment
        }

        private const val ICON_COLUMN_COUNT = 6
    }

    private var _binding: FragmentCategoryEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var iconDelegateAdapter: IconAdapterDelegate
    private lateinit var preferencesList: MutableList<Any>

    private var category: CategoryCreationModel? = null
    private var isNeedColorUpdate: Boolean = true

    private val adapter = BaseAdapter()
    private val compositeDisposable = CompositeDisposable()
    private val viewModel: CategoryCreationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryEditBinding.inflate(inflater, container, false)
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
            if (isNeedColorUpdate) {
                isNeedColorUpdate = false
                updateIconColor()
            }

            preferencesList = getPreferencesList()
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
        _binding = null
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
//        ColorPickerDialog.Builder(requireContext())
//            .setPositiveButton(getString(R.string.submit), object : ColorEnvelopeListener {
//                override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
//                    val color = envelope?.color
//                    if (fromUser && color != null) updateIconColor(color)
//                }
//            })
//            .setNegativeButton(getString(R.string.cancel), null)
//            .attachAlphaSlideBar(false)
//            .setBottomSpace(12)
//            .show()
    }

    private fun createCategory() {
//        CategoriesRepository.addCategory(
//            category = CategoryPostDto(
//                name = category!!.name!!,
//                iconColor = category!!.color!!,
//                iconLink = category!!.iconRes!!,
//                income = category!!.type == TransactionCategoryType.INCOME.uiName
//            )
//        ).subscribeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                (activity as CategoryListener).onCategoryCreated()
//            }, {
//                ErrorMessageHelper.setUpErrorMessage(binding.errorWidget) {
//                    createCategory()
//                }
//            }).addTo(compositeDisposable)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateIconColor(savedColor: Int? = null) {
        val green = ContextCompat.getColor(requireContext(), R.color.green)
        val color = savedColor
            ?: if (category!!.type == TransactionCategoryType.INCOME.uiName) {
                green
            } else if (category!!.color == null || category!!.color == green) {
                ContextCompat.getColor(requireContext(), R.color.purple)
            } else {
                category!!.color!!
            }
        viewModel.setColor(color)
    }

    private fun getPreferencesList(): MutableList<Any> {
        val list = mutableListOf(
            TransactionPreference(
                PreferenceType.NAME,
                category?.name ?: getString(R.string.unset), true
            ),
            TransactionPreference(
                PreferenceType.TYPE,
                category?.type ?: getString(R.string.unset), true
            ),
            TransactionPreference(
                PreferenceType.ICON, getString(R.string.choose_color), true
            ),
            ListIcon(
                R.drawable.ic_category_plane,
                category?.color!!,
                category?.iconRes == R.drawable.ic_category_plane
            ),
            ListIcon(
                R.drawable.ic_category_plane,
                category?.color!!,
                category?.iconRes == R.drawable.ic_category_plane
            ),
            ListIcon(
                R.drawable.ic_category_plane,
                category?.color!!,
                category?.iconRes == R.drawable.ic_category_plane
            ),
            ListIcon(
                R.drawable.ic_category_plane,
                category?.color!!,
                category?.iconRes == R.drawable.ic_category_plane
            ),
            ListIcon(
                R.drawable.ic_category_gas,
                category?.color!!,
                category?.iconRes == R.drawable.ic_category_gas
            ),
            ListIcon(
                R.drawable.ic_category_food,
                category?.color!!,
                category?.iconRes == R.drawable.ic_category_food
            ),
        )
        if (category!!.type != TransactionCategoryType.EXPENSE.uiName) {
            list.remove(list.find {
                it is TransactionPreference && it.type == PreferenceType.ICON
            })
        }

        return list
    }

}