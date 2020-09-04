package com.usphuong.bluetoothscanner.feature.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.usphuong.bluetoothscanner.R
import com.usphuong.bluetoothscanner.data.model.Device

class DeviceAdapter(private val isHistory: Boolean) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val deviceList = mutableListOf<Device>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_device, viewGroup, false)
        return DeviceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return deviceList.size
    }

    fun addData(device: Device) {
        this.deviceList.add(device)
        notifyItemInserted(deviceList.size)
    }

    fun clearData() {
        this.deviceList.clear()
        notifyDataSetChanged()
    }

    fun setData(listDevice: MutableList<Device>) {
        this.deviceList.clear()
        this.deviceList.addAll(listDevice)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as DeviceViewHolder
        viewHolder.bind(deviceList[position], isHistory)
    }
}


