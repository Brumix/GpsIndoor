package bruno.p.pereira.gpsindoorf.ui.share

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.databinding.FragmentQrScannerBinding
import bruno.p.pereira.gpsindoorf.services.HttpRequest
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode

const val CAMERA_PERMITIONS = 123

class QrScannerFragment : Fragment() {

    private var _binding: FragmentQrScannerBinding? = null
    private val binding get() = _binding!!
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentQrScannerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        root.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.nav_qrCode_from_scanner)
        }

        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        )
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMITIONS
            )
        else
            startScanning()


        return root
    }

    private fun startScanning() {
        val scannerView: CodeScannerView = binding.scanner
        codeScanner = CodeScanner(this.requireContext(), scannerView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                activity?.runOnUiThread {
                    Toast.makeText(
                        activity!!.applicationContext,
                        "Scan Result : ${it.text} ",
                        Toast.LENGTH_SHORT
                    ).show()
                    val contextHttp = this@QrScannerFragment.requireContext()
                    HttpRequest.startActionGETSync(contextHttp,it.text)
                    Navigation.findNavController(binding.root).navigate(R.id.nav_qrCode_from_scanner)
                    HttpRequest.startActionGETUser(contextHttp)
                    HttpRequest.startActionGETBeacons(contextHttp)
                    HttpRequest.startActionGETLocation(contextHttp)
                    HttpRequest.startActionGETEdge(contextHttp)
                }
            }

            errorCallback = ErrorCallback {
                activity?.runOnUiThread {
                    Toast.makeText(
                        activity!!.applicationContext,
                        "ERROR : ${it.message} ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        codeScanner.startPreview()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMITIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this.requireContext(),
                    "Permission Granted ",
                    Toast.LENGTH_SHORT
                ).show()
                startScanning()
            } else {
                Toast.makeText(
                    this.requireContext(),
                    "Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }


    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized)
            codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        if (::codeScanner.isInitialized)
            codeScanner.releaseResources()
    }

}