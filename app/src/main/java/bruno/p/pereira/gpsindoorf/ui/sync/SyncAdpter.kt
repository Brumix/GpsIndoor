package bruno.p.pereira.gpsindoorf.ui.sync

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.enums.BundleEnum
import bruno.p.pereira.gpsindoorf.models.Beacon
import com.clj.fastble.BleManager
import com.clj.fastble.data.BleScanState

const val LIST_ITEMS = "LIST_ITEMS"


class SyncAdpter(private val db: SQLiteHelper, beaconViewModel: SyncViewModel) :
    RecyclerView.Adapter<SyncAdpter.ViewHolder>() {


    private val listBeacons = beaconViewModel.getBeacons()


    fun notifyChanges(beacon: Beacon?) {
        if (beacon == null) {
            idGlobal = 0
            notifyDataSetChanged()
        } else {
            if (db.getFirstBeaconbyMac(beacon.mac) == null) {
                val oldId = beacon.id
                beacon.id = db.getAllBeacons().size + 1
                this.db.insertBeacon(beacon)
                beacon.id = oldId
            }
            notifyDataSetChanged()
        }
    }


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

        holder.idBeacon.text = currentBeacon.id.toString()
        holder.nameBeacon.text = currentBeacon.name
        holder.macBeacon.text = currentBeacon.mac
        holder.rssiBeacon.text = currentBeacon.rssi.toString()


        holder.itemView.setOnClickListener {
            if (BleManager.getInstance().scanSate == BleScanState.STATE_SCANNING)
                BleManager.getInstance().cancelScan()

            val bundle: Bundle = bundleOf(
                BundleEnum.BEACON_ID.name to currentBeacon.id,
                BundleEnum.BEACON_NAME.name to currentBeacon.name,
                BundleEnum.BEACON_MAC.name to currentBeacon.mac,
                BundleEnum.BEACON_RSSI.name to currentBeacon.rssi,
                BundleEnum.ORIGIN.name to LIST_ITEMS
            )
            Navigation.findNavController(holder.itemView)
                .navigate(R.id.navigation_details, bundle)
        }

        val loc = db.getFirstLocationbyMac(currentBeacon.mac)

        if (loc == null) {
            holder.addLocation.setColorFilter(Color.RED)
        } else {
            if (loc.place.isNotEmpty() && loc.latitude != "-1")
                holder.addLocation.setColorFilter(Color.rgb(29, 175, 43))
            else
                holder.addLocation.setColorFilter(Color.YELLOW)
        }

        holder.addLocation.setOnClickListener {
            if (BleManager.getInstance().scanSate == BleScanState.STATE_SCANNING)
                BleManager.getInstance().cancelScan()

            val bundle = bundleOf(
                "mac" to currentBeacon.mac
            )
            Navigation.findNavController(holder.itemView)
                .navigate(R.id.navigation_add_location, bundle)
        }
    }

    override fun getItemCount(): Int = this.listBeacons.size


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var idBeacon: TextView = itemView.findViewById(R.id.tvItemId)
        var nameBeacon: TextView = itemView.findViewById(R.id.tvItemName)
        var macBeacon: TextView = itemView.findViewById(R.id.tvItemMac)
        var rssiBeacon: TextView = itemView.findViewById(R.id.tvItemRssi)
        var addLocation: ImageView = itemView.findViewById(R.id.ivAddLoc)

    }

    companion object {
        private var idGlobal = 0

        fun getId(): Int {
            return idGlobal++
        }


    }

}