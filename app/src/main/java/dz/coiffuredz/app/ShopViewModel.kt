package dz.coiffuredz.app

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class ShopViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = (app as CoiffureDzApp).db.shop()
    var lang by mutableStateOf(Lang.FR)
    var day = MutableStateFlow(LocalDate.now().toEpochDay())

    val services = dao.services()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val clients = dao.clients()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Observe via function in UI with collectAsState on dao flow keyed by day.
    fun appointmentsFor(d: Long) = dao.dayAppointments(d)
    fun historyFor(clientId: Long) = dao.clientHistory(clientId)

    fun addAppointment(
        clientName: String, phone: String, service: Service?,
        customPrice: Int, timeMin: Int, chair: Int, d: Long
    ) {
        viewModelScope.launch {
            val s = service
            val price = s?.priceDzd ?: customPrice
            val sName = s?.name ?: "Coupe"
            val sId = s?.id ?: 0L
            val clean = clientName.trim()
            val existing = clients.value.firstOrNull { it.name.equals(clean, true) }
            val cid = existing?.id ?: dao.upsertClient(Client(name = clean, phone = phone.trim()))
            dao.insertAppointment(
                Appointment(
                    clientId = cid, clientName = clean,
                    serviceId = sId, serviceName = sName, priceDzd = price,
                    dateEpochDay = d, timeMin = timeMin, chair = chair
                )
            )
        }
    }

    fun setStatus(id: Long, status: String) {
        viewModelScope.launch { dao.setStatus(id, status) }
    }

    fun delete(id: Long) {
        viewModelScope.launch { dao.deleteAppointment(id) }
    }
}
