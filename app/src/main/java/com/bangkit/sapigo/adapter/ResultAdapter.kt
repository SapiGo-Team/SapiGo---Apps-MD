package com.bangkit.sapigo.adapter

import android.security.identity.ResultData
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.sapigo.R

class ResultsAdapter(private val dataList: List<com.bangkit.sapigo.data.ResultData>) :
    RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val textViewResponse1: TextView = itemView.findViewById(R.id.tv_item_name1)
        val textViewResponse2: TextView = itemView.findViewById(R.id.tv_item_name2)
        val textViewResponse3: TextView = itemView.findViewById(R.id.tv_item_name3)
        val textViewResponse4: TextView = itemView.findViewById(R.id.tv_item_name4)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resultData = dataList[position]
        val imageUriWithCacheBusting = resultData.imageUri.buildUpon()
            .appendQueryParameter("cacheBuster", System.currentTimeMillis().toString())
            .build()

        holder.imageViewPhoto.setImageURI(imageUriWithCacheBusting)
        holder.textViewResponse1.text = resultData.responses[0]
        holder.textViewResponse2.text = resultData.responses[1]
        holder.textViewResponse3.text = resultData.responses[2]
        holder.textViewResponse4.text = resultData.responses[3]
    }


    override fun getItemCount(): Int {
        return dataList.size
    }
}
