package com.unh.washbuddy_android

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdminAdapter (
    private val mExampleList: ArrayList<AdminCard>,
    private val context: AdminDashboard
) : RecyclerView.Adapter<AdminAdapter.ExampleViewHolder>() {



    class ExampleViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val mDateandTime: TextView = itemview.findViewById(R.id.text_dateandtime)
        val mTextView1: TextView = itemview.findViewById(R.id.text_view_1)
        val mTextView2: TextView = itemview.findViewById(R.id.text_view_2)
        val mTextView3: TextView = itemview.findViewById(R.id.text_view_3)
        val mAmount: TextView = itemview.findViewById(R.id.text_amount)
        val mSubmitButton: Button = itemview.findViewById(R.id.button_view_order)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cards_admin_layout, parent, false)

        return ExampleViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return mExampleList.size
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val adminCard = mExampleList[position]
        val(date, time, email, address, selectLaundromat, amount) = mExampleList[position]
        holder.mDateandTime.text = "$date, $time"
        holder.mTextView1.text = "User Email: $email"
        holder.mTextView2.text = "Pickup Address: $address"
        holder.mTextView3.text = "Laundromat: $selectLaundromat"
        holder.mAmount.text = amount

        holder.mSubmitButton.setOnClickListener {
            val intent = Intent(context, AdminViewOrder::class.java)
            intent.putExtra("orderDetails", adminCard)
            context.startActivity(intent)
        }


        holder.itemView.setOnClickListener {
            Log.d("MYTEST", "Position $position")
        }
    }
}