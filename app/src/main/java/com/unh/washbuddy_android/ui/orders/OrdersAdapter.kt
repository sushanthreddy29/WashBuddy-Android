package com.unh.washbuddy_android.ui.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.unh.washbuddy_android.R

class OrdersAdapter(
    private val mExampleList: ArrayList<OrdersCard>,
    private val listener: OnOrderButtonClickListener
) : RecyclerView.Adapter<OrdersAdapter.ExampleViewHolder>() {

    class ExampleViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val mDateandTime: TextView = itemview.findViewById(R.id.text_dateandtime)
        val mTextView1: TextView = itemview.findViewById(R.id.text_view_1)
        val mTextView2: TextView = itemview.findViewById(R.id.text_view_2)
        val mTextView3: TextView = itemview.findViewById(R.id.text_view_3)
        val mAmount: TextView = itemview.findViewById(R.id.text_amount)
        val mStatus: TextView = itemview.findViewById(R.id.status)
        val vieworder: Button = itemview.findViewById(R.id.button_view_order)
        val reorder: Button = itemview.findViewById(R.id.button_reorder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_orders_layout, parent, false)

        return ExampleViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mExampleList.size
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val(date, time , address, laundry, delivery, amount, status) = mExampleList[position]
        val order = mExampleList[position]
        holder.mDateandTime.text = "$date, $time"
        holder.mTextView1.text = address
        holder.mTextView2.text = laundry
        holder.mTextView3.text = delivery
        holder.mAmount.text = amount
        holder.mStatus.text = status

        holder.itemView.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Item Position $position clicked", Toast.LENGTH_SHORT).show()
        }

        // Detect 'vieworder' button click
        holder.vieworder.setOnClickListener {
            listener.onViewOrderClick(order)
            Toast.makeText(holder.itemView.context, "View Order button clicked for position $position", Toast.LENGTH_SHORT).show()
            // Handle view order action here
        }

        // Detect 'reorder' button click
        holder.reorder.setOnClickListener {
            listener.onReorderClick(order)
            Toast.makeText(holder.itemView.context, "Reorder button clicked for position $position", Toast.LENGTH_SHORT).show()
            // Handle reorder action here
        }
    }
}