package id.co.psplauncher.ui.history // Sesuaikan package

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.co.psplauncher.data.network.model.ModelHistory // Import ModelHistory UI kamu
import id.co.psplauncher.databinding.ItemHistoryBinding // Import Binding Item

class HistoryAdapter(private val historyList: ArrayList<ModelHistory>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val data = historyList[position]

        holder.binding.tvUsername.text = data.name
        holder.binding.tvDate.text = data.date
        holder.binding.tvTime.text = data.time
        holder.binding.tvStatus.text = data.status
    }

    override fun getItemCount(): Int = historyList.size

    fun setData(data: List<ModelHistory>) {
        historyList.clear()
        historyList.addAll(data)
        notifyDataSetChanged()
    }

    fun setHistoryList(newList: ArrayList<ModelHistory>) {
        this.historyList.clear()
        this.historyList.addAll(newList)
        notifyDataSetChanged()
    }

    private fun ChangeFormatTime(dateOrigin: String?) : String{
        if (dateOrigin.isNullOrEmpty()) return "_"

        val formatInput = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())

        val formatOutput = java.text.SimpleDateFormat("HH:mm", java.util.Locale("id", "ID"))

        return try {
            val date = formatInput.parse(dateOrigin)
            formatOutput.format(date!!)
        } catch (e: Exception) {

            dateOrigin
        }
    }
}

