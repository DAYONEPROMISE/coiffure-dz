package dz.coiffuredz.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    private val vm: ShopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val lang = vm.lang
            val dir = if (lang == Lang.AR) LayoutDirection.Rtl else LayoutDirection.Ltr
            CompositionLocalProvider(LocalLayoutDirection provides dir) {
                MaterialTheme {
                    Surface(Modifier.fillMaxSize()) { MainScreen(vm) }
                }
            }
        }
    }
}

@Composable
fun MainScreen(vm: ShopViewModel) {
    val s = Strings.get(vm.lang)
    var tab by remember { mutableStateOf(0) }
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(s["title"]!!, style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { vm.lang = if (vm.lang == Lang.FR) Lang.AR else Lang.FR }) {
                Text(s["lang"]!!)
            }
        }
        TabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text(s["tab_book"]!!) })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text(s["tab_cash"]!!) })
            Tab(selected = tab == 2, onClick = { tab = 2 }, text = { Text(s["tab_clients"]!!) })
        }
        when (tab) {
            0 -> BookTab(vm, s)
            1 -> CashTab(vm, s)
            2 -> ClientsTab(vm, s)
        }
    }
}

@Composable
fun BookTab(vm: ShopViewModel, s: Map<String, String>) {
    val day by vm.day.collectAsState()
    val list by vm.appointmentsFor(day).collectAsState(initial = emptyList())
    val services by vm.services.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    Column(Modifier.padding(12.dp)) {
        Text(s["today"]!! + " " + LocalDate.ofEpochDay(day).toString(), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Button(onClick = { showAdd = true }) { Text(s["add"]!!) }
        Spacer(Modifier.height(8.dp))
        if (list.isEmpty()) Text(s["empty"]!!)
        LazyColumn {
            items(list) { a ->
                Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Column(Modifier.padding(10.dp)) {
                        Text("${ShopLogic.formatTime(a.timeMin)} - ${a.clientName} - ${a.serviceName} - ${ShopLogic.formatDzd(a.priceDzd)} [${a.status}]")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { vm.setStatus(a.id, "DONE") }) { Text(s["done"]!!) }
                            TextButton(onClick = { vm.setStatus(a.id, "CANCELLED") }) { Text(s["cancelled"]!!) }
                            TextButton(onClick = { vm.delete(a.id) }) { Text("X") }
                        }
                    }
                }
            }
        }
    }
    if (showAdd) AddSheet(vm, s, services, day) { showAdd = false }
}

@Composable
fun AddSheet(vm: ShopViewModel, s: Map<String, String>, services: List<Service>, day: Long, onClose: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("0900") }
    var chair by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }
    var selIdx by remember { mutableStateOf(0) }
    val sel = services.getOrNull(selIdx)
    Card(Modifier.fillMaxWidth().padding(12.dp)) {
        Column(Modifier.padding(12.dp)) {
            OutlinedTextField(name, { name = it }, label = { Text(s["client"]!!) })
            OutlinedTextField(phone, { phone = it }, label = { Text(s["phone"]!!) })
            if (services.isNotEmpty()) {
                Text(s["service"]!! + ": " + (sel?.name ?: ""))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    services.forEachIndexed { i, svc ->
                        TextButton(onClick = { selIdx = i }) { Text(if (i == selIdx) "[${svc.name}]" else svc.name) }
                    }
                }
            } else {
                OutlinedTextField(price, { price = it }, label = { Text("DZD") })
            }
            OutlinedTextField(time, { time = it }, label = { Text(s["time"]!!) })
            OutlinedTextField(chair, { chair = it }, label = { Text(s["chair"]!!) })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val digits = time.filter { it.isDigit() }.padStart(4, '0')
                    val tMin = digits.take(2).toIntOrNull()?.times(60)
                        ?.plus(digits.drop(2).toIntOrNull() ?: 0) ?: 540
                    val err = ShopLogic.validateAppointment(name, sel?.priceDzd ?: (price.toIntOrNull() ?: 0), tMin)
                    if (err == null) {
                        vm.addAppointment(name, phone, sel, price.toIntOrNull() ?: 300, tMin, chair.toIntOrNull() ?: 1, day)
                        onClose()
                    }
                }) { Text(s["save"]!!) }
                TextButton(onClick = onClose) { Text(s["cancel"]!!) }
            }
        }
    }
}

@Composable
fun CashTab(vm: ShopViewModel, s: Map<String, String>) {
    val day by vm.day.collectAsState()
    val list by vm.appointmentsFor(day).collectAsState(initial = emptyList())
    Column(Modifier.padding(16.dp)) {
        Text(s["takings"]!!, style = MaterialTheme.typography.titleMedium)
        Text(ShopLogic.formatDzd(ShopLogic.dayTakings(list)), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(s["cuts"]!! + ": " + ShopLogic.doneCount(list))
    }
}

@Composable
fun ClientsTab(vm: ShopViewModel, s: Map<String, String>) {
    val clients by vm.clients.collectAsState()
    var selId by remember { mutableStateOf<Long?>(null) }
    val hist by vm.historyFor(selId ?: -1L).collectAsState(initial = emptyList())
    Column(Modifier.padding(12.dp)) {
        LazyColumn {
            items(clients) { c ->
                TextButton(onClick = { selId = c.id }) { Text("${c.name} ${if (c.phone.isNotBlank()) "(${c.phone})" else ""}") }
            }
        }
        if (selId != null) {
            Text(s["history"]!! + ": " + hist.size)
            LazyColumn {
                items(hist) { a ->
                    Text("${LocalDate.ofEpochDay(a.dateEpochDay)} ${ShopLogic.formatTime(a.timeMin)} ${a.serviceName} ${ShopLogic.formatDzd(a.priceDzd)}")
                }
            }
        }
    }
}
