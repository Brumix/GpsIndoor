package bruno.p.pereira.gpsindoorf.ui.beacons



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.enums.BundleEnum
import bruno.p.pereira.gpsindoorf.models.Beacon


const val LIST_DATABASE= "LIST_DATABASE"

class BeaconsAdpter(private val db: SQLiteHelper) : RecyclerView.Adapter<BeaconsAdpter.ViewHolder>() {


    // TODO made this to live data or find a way to make this persistent
    private var listBeacons: MutableList<Beacon> = this.db.getAllBeacons()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_detail_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentBeacon = this.listBeacons[position]
        val id = currentBeacon.id
        val name = currentBeacon.name
        " ID: $id".also { holder.idDevice.text = it }
        "NAME: $name".also { holder.nameDevice.text = it }


        holder.itemView.setOnClickListener {

            val bundle: Bundle = bundleOf(
                BundleEnum.BEACON_ID.name to id,
                BundleEnum.BEACON_NAME.name to name,
                BundleEnum.BEACON_MAC.name to currentBeacon.mac,
                BundleEnum.BEACON_RSSI.name to currentBeacon.rssi,
                BundleEnum.ORIGIN.name to LIST_DATABASE
            )
            Navigation.findNavController(holder.itemView)
                .navigate(R.id.navigation_details, bundle)

        }
    }

    override fun getItemCount(): Int = this.listBeacons.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var idDevice: TextView = itemView.findViewById(R.id.tvItemId)
        var nameDevice: TextView = itemView.findViewById(R.id.tvItemName)


    }


}
