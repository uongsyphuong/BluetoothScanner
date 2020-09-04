package com.usphuong.bluetoothscanner.feature.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.usphuong.bluetoothscanner.R
import com.usphuong.bluetoothscanner.data.model.Device
import kotlinx.android.synthetic.main.item_device.view.tvLastSeen
import kotlinx.android.synthetic.main.item_device.view.tvMacAddress
import kotlinx.android.synthetic.main.item_device.view.tvName
import kotlinx.android.synthetic.main.item_device.view.tvRSSI

class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val tvName = itemView.tvName
    private val tvMacAddress = itemView.tvMacAddress
    private val tvRSSI = itemView.tvRSSI
    private val tvLastSeen = itemView.tvLastSeen

    fun bind(device: Device, isHistory: Boolean) {
        tvName.text = device.name
        tvMacAddress.text = device.macAddress
        tvRSSI.text = device.rssi.toString()
        tvLastSeen.visibility = if (isHistory) View.VISIBLE else View.GONE
        tvLastSeen.text =
            itemView.context.getString(R.string.last_seen, device.getLastSeen())
    }
}
