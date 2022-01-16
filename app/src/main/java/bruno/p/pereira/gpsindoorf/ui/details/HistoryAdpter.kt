package bruno.p.pereira.gpsindoorf.ui.details

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.TAG
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.models.DtoLocation

class HistoryAdpter(db: SQLiteHelper, mac: String) :
    RecyclerView.Adapter<HistoryAdpter.ViewHolder>() {


    private var listLoc: MutableList<DtoLocation> = db.getAllLocationbyMac(mac)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_history_loc,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentloc = this.listLoc[position]
        holder.apply {
            division.text = currentloc.division.trim()
            place.text = currentloc.place.trim()
            locTime.text = currentloc.loc_time?.trim()
        }
    }

    override fun getItemCount(): Int = this.listLoc.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var place: TextView = itemView.findViewById(R.id.tvItemPlace)
        var division: TextView = itemView.findViewById(R.id.tvItemDivision)
        var locTime: TextView = itemView.findViewById(R.id.tvItemTime)
    }


}
