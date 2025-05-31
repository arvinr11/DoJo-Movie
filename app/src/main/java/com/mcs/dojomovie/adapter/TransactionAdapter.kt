package com.mcs.dojomovie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mcs.dojomovie.R
import com.mcs.dojomovie.model.Transaction

class TransactionAdapter(
    private var transactionList: MutableList<Transaction>
) : RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val filmImage: ImageView = itemView.findViewById(R.id.iv_poster_film)
        private val filmTitle: TextView = itemView.findViewById(R.id.tv_judul_film)
        private val quantityText: TextView = itemView.findViewById(R.id.tv_jumlah_film)
        private val totalPriceText: TextView = itemView.findViewById(R.id.tv_harga_film)

        fun bind(transaction: Transaction) {
            filmTitle.text = transaction.film_title
            quantityText.text = "Quantity: ${transaction.quantity}"

            val totalPrice = transaction.quantity * transaction.film_price

            totalPriceText.text = "Total: Rp ${totalPrice}"

            val context = itemView.context
            val resId = context.resources.getIdentifier(transaction.film_image, "drawable", context.packageName)
            if (resId != 0) {
                filmImage.setImageResource(resId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(transactionList[position])
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    fun updateTransactions(newTransactions: List<Transaction>) {
        transactionList.clear()
        transactionList.addAll(newTransactions)
        notifyDataSetChanged()
    }
}