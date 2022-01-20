package bruno.p.pereira.gpsindoorf.ui.share

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import bruno.p.pereira.gpsindoorf.R
import bruno.p.pereira.gpsindoorf.databinding.FragmentQrCodeBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder


class QrCodeFragment : Fragment() {

    private var _binding: FragmentQrCodeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentQrCodeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val encoder = BarcodeEncoder()
        val bitmap = encoder.encodeBitmap(Build.ID, BarcodeFormat.QR_CODE, 500, 500)
        binding.ivQrCode.setImageBitmap(bitmap)

        root.setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.nav_Scanner_from_qrCode)
        }

        return root
    }


}