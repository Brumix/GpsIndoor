package bruno.p.pereira.gpsindoorf.ui.sync

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bruno.p.pereira.gpsindoorf.TAG
import bruno.p.pereira.gpsindoorf.models.Beacon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SyncViewModel : ViewModel() {

    private val _beacons: MutableList<Beacon> = mutableListOf()

    fun getBeacons(): MutableList<Beacon> {
        return _beacons
    }

    fun addBeacons(b: Beacon) = viewModelScope.launch(Dispatchers.IO) {
        for (i in _beacons) {
            if (i.mac == b.mac)
                return@launch

        }
        _beacons.add(b)
        Log.v(TAG, "[VIEWMODEL] new Beacon added ${b.id}")
    }

    fun clearAllBeacons() = viewModelScope.launch(Dispatchers.IO) {
        _beacons.removeAll(_beacons)
    }

}