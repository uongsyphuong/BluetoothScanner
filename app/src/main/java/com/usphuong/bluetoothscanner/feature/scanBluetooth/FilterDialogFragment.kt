package com.usphuong.bluetoothscanner.feature.scanBluetooth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.usphuong.bluetoothscanner.R
import com.usphuong.bluetoothscanner.viewModel.DeviceViewModel
import com.usphuong.bluetoothscanner.viewModel.ViewModelFactory
import dagger.android.support.DaggerDialogFragment
import kotlinx.android.synthetic.main.fragment_filter.btnApply
import kotlinx.android.synthetic.main.fragment_filter.cbName
import kotlinx.android.synthetic.main.fragment_filter.rsFilter
import kotlinx.android.synthetic.main.fragment_filter.tvEnd
import kotlinx.android.synthetic.main.fragment_filter.tvStart
import javax.inject.Inject

class FilterDialogFragment : DaggerDialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val deviceViewModel by activityViewModels<DeviceViewModel> { viewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog?.window?.setLayout(width, height)
        }
    }

    private fun setupView() {
        tvStart.text = getString(R.string.dBm, deviceViewModel.startRssi)
        tvEnd.text = getString(R.string.dBm, deviceViewModel.endRssi)

        cbName.isChecked = deviceViewModel.isFilterName

        rsFilter.setValues(
            deviceViewModel.startRssi.toFloat(),
            deviceViewModel.endRssi.toFloat()
        )

        rsFilter.addOnChangeListener { rangeSlider, _, _ ->
            tvStart.text = getString(R.string.dBm, rangeSlider.values[0].toInt())
            tvEnd.text = getString(R.string.dBm, rangeSlider.values[1].toInt())
        }

        btnApply.setOnClickListener {
            deviceViewModel.filterRSSI(
                rsFilter.values[0].toInt(),
                rsFilter.values[1].toInt(),
                cbName.isChecked
            )
            dismiss()
        }
    }

}
