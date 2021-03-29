package com.example.gms.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gms.BinActivity
import com.example.gms.BinViewActivity
import com.example.gms.R
import com.example.gms.models.Bin

class BinAdapter(private val context : BinActivity, private val items : List<Bin>) : RecyclerView.Adapter<BinAdapter.BinViewHolder>(){
    class BinViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val binName = view.findViewById<TextView>(R.id.tvBinName)
        val binDesc = view.findViewById<TextView>(R.id.tvBinDesc)
        val btnDeleteBin = view.findViewById<ImageButton>(R.id.btnDeleteBin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.bin_layout, parent, false)
        return BinViewHolder(view)
    }

    override fun onBindViewHolder(holder: BinViewHolder, position: Int) {
        val bin = items[position]
        holder.binName.text = bin.name
        holder.binDesc.text = bin.desc
        holder.itemView.setOnClickListener {
            val binView = Intent(context, BinViewActivity::class.java)
            binView.putExtra("bin", bin)
            context.startActivity(binView)
        }
        holder.btnDeleteBin.setOnClickListener {
            context.deleteBin(bin)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}