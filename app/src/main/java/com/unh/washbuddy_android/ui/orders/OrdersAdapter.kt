package com.unh.washbuddy_android.ui.orders

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.unh.washbuddy_android.R
import org.w3c.dom.Text

class OrdersAdapter(
    private val mExampleList: ArrayList<OrdersCard>,
    private val context: OrdersFragment
) : RecyclerView.Adapter<OrdersAdapter.ExampleViewHolder>() {



    class ExampleViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val mDateandTime: TextView = itemview.findViewById(R.id.text_dateandtime)
        val mTextView1: TextView = itemview.findViewById(R.id.text_view_1)
        val mTextView2: TextView = itemview.findViewById(R.id.text_view_2)
        val mTextView3: TextView = itemview.findViewById(R.id.text_view_3)
        val mAmount: TextView = itemview.findViewById(R.id.text_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_orders_layout, parent, false)

        return ExampleViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mExampleList.size
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val(datetime ,text1, text2, text3, amount) = mExampleList[position]
        holder.mDateandTime.text = datetime
        holder.mTextView1.text = text1
        holder.mTextView2.text = text2
        holder.mTextView3.text = text3
        holder.mAmount.text = amount

        holder.itemView.setOnClickListener {
            Log.d("MYTEST", "Position $position")
        }
    }
}