package dz.coiffuredz.app

/** Pure business logic — unit tested on JVM, no Android needed. */
object ShopLogic {
    /** Sum of DONE appointments only. Walk-ins and bookings count once marked DONE. */
    fun dayTakings(list: List<Appointment>): Int =
        list.filter { it.status == "DONE" }.sumOf { it.priceDzd }

    fun doneCount(list: List<Appointment>): Int = list.count { it.status == "DONE" }

    fun validateAppointment(name: String, priceDzd: Int, timeMin: Int): String? {
        if (name.isBlank()) return "name"
        if (priceDzd < 0) return "price"
        if (timeMin < 0 || timeMin >= 24 * 60) return "time"
        return null
    }

    fun formatDzd(v: Int): String = "%,d DZD".format(v).replace(',', ' ')

    fun formatTime(min: Int): String = "%02d:%02d".format(min / 60, min % 60)
}
