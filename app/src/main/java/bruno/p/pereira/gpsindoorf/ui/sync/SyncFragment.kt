package bruno.p.pereira.gpsindoorf.ui.sync

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import bruno.p.pereira.gpsindoorf.TAG
import bruno.p.pereira.gpsindoorf.database.SQLiteHelper
import bruno.p.pereira.gpsindoorf.databinding.FragmentSyncBinding
import bruno.p.pereira.gpsindoorf.models.Beacon
import com.clj.fastble.BleManager
import com.clj.fastble.callback.BleScanCallback
import com.clj.fastble.data.BleDevice
import com.clj.fastble.data.BleScanState
import java.util.ArrayList


private const val REQUEST_CODE_OPEN_GPS = 1
private const val REQUEST_CODE_PERMISSION_LOCATION = 2
private const val START_SCAN = "Scan"
private const val STOP_SCAN = "Stop"
private const val SCANNING = "Scanning"

class SyncFragment : Fragment() {

    private val syncViewModel: SyncViewModel by activityViewModels()
    private var _binding: FragmentSyncBinding? = null
    private val binding get() = _binding!!
    private val bd: SQLiteHelper by lazy {
        SQLiteHelper(this.requireContext())
    }
    private val _syncAdapt: SyncAdpter by lazy {
        SyncAdpter(bd, syncViewModel)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BleManager.getInstance().init(requireActivity().application)
        BleManager.getInstance()
            .enableLog(true)
            .setReConnectCount(1, 5000)
            .setConnectOverTime(20000).operateTimeout = 5000
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSyncBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val scanB: Button = binding.btScan
        scanB.setOnClickListener {
            if (scanB.text == START_SCAN) {
                checkPermissions()
            } else if (scanB.text == STOP_SCAN) {
                BleManager.getInstance().cancelScan()
            }
        }

        binding.rvSyncList.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = _syncAdapt
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun checkPermissions() {

        val bluetoothManager =
            context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        if (!bluetoothAdapter.isEnabled) {
            Toast.makeText(activity, "Turn on the Bluetooth", Toast.LENGTH_LONG).show()
            return
        }

        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionDeniedList = ArrayList<String>()
        for (permission in permissions) {
            val permissionCheck = context?.let { ContextCompat.checkSelfPermission(it, permission) }
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission)
            } else {
                permissionDeniedList.add(permission)
            }
        }
        if (permissionDeniedList.isNotEmpty()) {
            val deniedPermissions = permissionDeniedList.toTypedArray()
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    deniedPermissions,
                    REQUEST_CODE_PERMISSION_LOCATION
                )
            }
        }
    }

    private fun onPermissionGranted(permission: String) {
        when (permission) {
            Manifest.permission.ACCESS_FINE_LOCATION ->
                if (Build.VERSION.SDK_INT >=
                    Build.VERSION_CODES.M && !checkGPSIsOpen()
                ) {
                    AlertDialog.Builder(context)
                        .setTitle("Prompt")
                        .setMessage("BLE needs to open the positioning function")
                        .setNegativeButton("cancel",
                            DialogInterface.OnClickListener { _, _ -> activity?.finish() })
                        .setPositiveButton("setting",
                            DialogInterface.OnClickListener { _, _ ->
                                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                startActivityForResult(intent, REQUEST_CODE_OPEN_GPS)

                            })
                        .setCancelable(false)
                        .show()
                } else {
                    startScan()
                }
        }
    }

    private fun startScan() {
        BleManager.getInstance().scan(object : BleScanCallback() {
            override fun onScanStarted(success: Boolean) {
                Log.v(TAG, "[SCANNER]: $START_SCAN")
                Toast.makeText(activity, SCANNING, Toast.LENGTH_LONG).show()
                binding.btScan.text = STOP_SCAN
                syncViewModel.clearAllBeacons()
                _syncAdapt.notifyChanges(null)
            }

            override fun onLeScan(bleDevice: BleDevice?) {
                super.onLeScan(bleDevice)
            }

            override fun onScanning(bleDevice: BleDevice) {
                Log.v(TAG, "[SCANNER]: FOUND-> NAME:${bleDevice.name} MAC: ${bleDevice.mac} ")
                val newBeacon =
                    Beacon(SyncAdpter.getId(), bleDevice.name, bleDevice.mac, bleDevice.rssi)
                syncViewModel.addBeacons(newBeacon)
                Log.v(TAG, "[VIEWMODEL]: ${syncViewModel.getBeacons().size}")
                _syncAdapt.notifyChanges(newBeacon)
            }

            override fun onScanFinished(scanResultList: List<BleDevice>) {
                Log.v(TAG, "[SCANNER]: $STOP_SCAN")
                binding.btScan.text = START_SCAN
            }
        })
    }

    private fun checkGPSIsOpen(): Boolean {
        val locationManager =
            activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}