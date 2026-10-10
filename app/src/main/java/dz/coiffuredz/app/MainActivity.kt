package dz.coiffuredz.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    private val vm: ShopViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val lang = vm.lang
            val dir = if (lang == Lang.AR) LayoutDirection.Rtl else LayoutDirection.Ltr
            CompositionLocalProvider(LocalLayoutDirection provides dir) {
                CoiffureDzTheme {
                    Surface(Modifier.fillMaxSize()) { MainScreen(vm) }
                }
            }
        }
    }
}

/** 2026 theme: dynamic color on Android 12+, system dark mode, M3 fallback tokens. */
@Composable
fun CoiffureDzTheme(content: @Composable () -> Unit) {
    val ctx = LocalContext.current
    val dark = isSystemInDarkTheme()
    val scheme = when {
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        dark -> darkColorScheme()
        else -> lightColorScheme()
    }
    MaterialTheme(colorScheme = scheme, content = content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(vm: ShopViewModel) {
    val s = Strings.get(vm.lang)
    var tab by remember { mutableStateOf(0) }
    var showAdd by remember { mutableStateOf(false) }
    val day by vm.day.collectAsState()
    val services by vm.services.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(s["title"]!!) },
                actions = {
                    IconButton(
                        onClick = { vm.lang = if (vm.lang == Lang.FR) Lang.AR else Lang.FR }
                    ) { Icon(Icons.Filled.Language, contentDescription = s["lang"]) }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0, onClick = { tab = 0 },
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = null) },
                    label = { Text(s["tab_book"]!!) }
                )
                NavigationBarItem(
                    selected = tab == 1, onClick = { tab = 1 },
                    icon = { Icon(Icons.Filled.Wallet, contentDescription = null) },
                    label = { Text(s["tab_cash"]!!) }
                )
                NavigationBarItem(
                    selected = tab == 2, onClick = { tab = 2 },
                    icon = { Icon(Icons.Filled.People, contentDescription = null) },
                    label = { Text(s["tab_clients"]!!) }
                )
            }
        },
        floatingActionButton = {
            if (tab == 0) {
                ExtendedFloatingActionButton(
                    onClick = { showAdd = true },
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text(s["add"]!!) }
                )
            }
        }
    ) { inner ->
        Column(Modifier.padding(inner).fillMaxSize()) {
            when (tab) {
                0 -> BookTab(vm, s)
                1 -> CashTab(vm, s)
                2 -> ClientsTab(vm, s)
            }
        }
    }
    if (showAdd) {
        AddBottomSheet(vm, s, services, day) { showAdd = false }
    }
}

@Composable
fun BookTab(vm: ShopViewModel, s: Map<String, String>) {
    val day by vm.day.collectAsState()
    val list by vm.appointmentsFor(day).collectAsState(initial = emptyList())
    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { vm.day.value = day - 1 }) { Text("<") }
            Text(
                LocalDate.ofEpochDay(day).toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() }
            )
            TextButton(onClick = { vm.day.value = day + 1 }) { Text(">") }
        }
        if (list.isEmpty()) {
            Text(s["empty"]!!, style = MaterialTheme.typography.bodyLarge)
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(list, key = { it.id }) { a ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text("${ShopLogic.formatTime(a.timeMin)} · ${a.clientName}") },
                        supportingContent = {
                            Text("${a.serviceName} · ${ShopLogic.formatDzd(a.priceDzd)} · ${s["chair"]!!} ${a.chair}")
                        },
                        trailingContent = { Text(statusLabel(s, a.status)) }
                    )
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { vm.setStatus(a.id, "DONE") }) { Text(s["done"]!!) }
                        TextButton(onClick = { vm.setStatus(a.id, "CANCELLED") }) { Text(s["cancelled"]!!) }
                        TextButton(onClick = { vm.delete(a.id) }) { Text("✕") }
                    }
                }
            }
        }
        Spacer(Modifier.height(88.dp)) // clearance above FAB
    }
}

private fun statusLabel(s: Map<String, String>, status: String): String = when (status) {
    "DONE" -> s["done"]!!
    "CANCELLED" -> s["cancelled"]!!
    else -> s["booked"]!!
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBottomSheet(vm: ShopViewModel, s: Map<String, String>, services: List<Service>, day: Long, onClose: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("0900") }
    var chair by remember { mutableStateOf("1") }
    var price by remember { mutableStateOf("") }
    var selIdx by remember { mutableStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }
    val sel = services.getOrNull(selIdx)
    ModalBottomSheet(onDismissRequest = onClose, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(s["add"]!!, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
            OutlinedTextField(name, { name = it; error = null }, label = { Text(s["client"]!!) }, singleLine = true, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(phone, { phone = it }, label = { Text(s["phone"]!!) }, singleLine = true, modifier = Modifier.fillMaxWidth())
            if (services.isNotEmpty()) {
                Text(s["service"]!!, style = MaterialTheme.typography.labelLarge)
                services.forEachIndexed { i, svc ->
                    FilterChip(
                        selected = i == selIdx, onClick = { selIdx = i },
                        label = { Text("${svc.name} · ${ShopLogic.formatDzd(svc.priceDzd)}") }
                    )
                    Spacer(Modifier.height(4.dp))
                }
            } else {
                OutlinedTextField(price, { price = it }, label = { Text("DZD") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(time, { time = it }, label = { Text(s["time"]!!) }, singleLine = true, modifier = Modifier.weight(1f))
                OutlinedTextField(chair, { chair = it }, label = { Text(s["chair"]!!) }, singleLine = true, modifier = Modifier.weight(1f))
            }
            if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
            Button(
                onClick = {
                    val digits = time.filter { it.isDigit() }.padStart(4, '0')
                    val tMin = digits.take(2).toIntOrNull()?.times(60)
                        ?.plus(digits.drop(2).toIntOrNull() ?: 0) ?: 540
                    val err = ShopLogic.validateAppointment(name, sel?.priceDzd ?: (price.toIntOrNull() ?: 0), tMin)
                    if (err == null) {
                        vm.addAppointment(name, phone, sel, price.toIntOrNull() ?: 300, tMin, chair.toIntOrNull() ?: 1, day)
                        onClose()
                    } else {
                        error = err
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(s["save"]!!) }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun CashTab(vm: ShopViewModel, s: Map<String, String>) {
    val day by vm.day.collectAsState()
    val list by vm.appointmentsFor(day).collectAsState(initial = emptyList())
    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            s["takings"]!! + " · " + LocalDate.ofEpochDay(day).toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.semantics { heading() }
        )
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    ShopLogic.formatDzd(ShopLogic.dayTakings(list)),
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text(s["cuts"]!! + ": " + ShopLogic.doneCount(list), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

/** Adaptive list-detail: side-by-side on tablets (≥600dp), stacked on phones. */
@Composable
fun ClientsTab(vm: ShopViewModel, s: Map<String, String>) {
    val clients by vm.clients.collectAsState()
    var selId by remember { mutableStateOf<Long?>(null) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxWidth >= 600.dp) {
            Row(Modifier.fillMaxSize()) {
                ClientList(
                    clients, selId, { selId = it }, s,
                    modifier = Modifier.width(280.dp).fillMaxHeight()
                )
                ClientDetail(vm, s, selId, modifier = Modifier.weight(1f).fillMaxHeight())
            }
        } else {
            if (selId == null) {
                ClientList(clients, null, { selId = it }, s, modifier = Modifier.fillMaxSize())
            } else {
                Column(Modifier.fillMaxSize()) {
                    TextButton(onClick = { selId = null }) { Text("<") }
                    ClientDetail(vm, s, selId, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun ClientList(
    clients: List<Client>, selId: Long?, onSelect: (Long) -> Unit, s: Map<String, String>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier.padding(horizontal = 8.dp)) {
        items(clients, key = { it.id }) { c ->
            val selected = c.id == selId
            Surface(
                tonalElevation = if (selected) 4.dp else 0.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            ) {
                ListItem(
                    headlineContent = { Text(c.name) },
                    supportingContent = { if (c.phone.isNotBlank()) Text(c.phone) },
                    modifier = Modifier.clickable { onSelect(c.id) }
                )
            }
        }
    }
}

@Composable
fun ClientDetail(vm: ShopViewModel, s: Map<String, String>, selId: Long?, modifier: Modifier = Modifier) {
    val hist by vm.historyFor(selId ?: -1L).collectAsState(initial = emptyList())
    Column(modifier.padding(16.dp)) {
        if (selId == null) {
            Text(s["history"]!!, style = MaterialTheme.typography.bodyLarge)
        } else {
            Text(
                s["history"]!! + " (" + hist.size + ")",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { heading() }
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(hist, key = { it.id }) { a ->
                    Text("${LocalDate.ofEpochDay(a.dateEpochDay)} · ${ShopLogic.formatTime(a.timeMin)} · ${a.serviceName} · ${ShopLogic.formatDzd(a.priceDzd)}")
                }
            }
        }
    }
}
