package dz.coiffuredz.app

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Default Algerian barbershop services, seeded once on first launch (offline). */
object SeedDefaults {
    fun seed(context: Context) {
        val app = context.applicationContext as CoiffureDzApp
        CoroutineScope(Dispatchers.IO).launch {
            if (app.db.shop().serviceCount() == 0) {
                listOf(
                    Service(name = "Coupe", priceDzd = 400, durationMin = 20),
                    Service(name = "Barbe", priceDzd = 200, durationMin = 15),
                    Service(name = "Coupe + Barbe", priceDzd = 600, durationMin = 35),
                    Service(name = "Dégradé", priceDzd = 500, durationMin = 30),
                    Service(name = "Enfant", priceDzd = 300, durationMin = 20)
                ).forEach { app.db.shop().upsertService(it) }
            }
        }
    }
}
