package dz.coiffuredz.app

import org.junit.Assert.*
import org.junit.Test

class ShopLogicTest {
    private fun appt(price: Int, status: String) = Appointment(
        clientId = 1, clientName = "Test", serviceId = 1,
        serviceName = "Coupe", priceDzd = price,
        dateEpochDay = 20000, timeMin = 540, status = status
    )

    @Test fun takings_countsOnlyDone() {
        val list = listOf(appt(400, "DONE"), appt(600, "BOOKED"), appt(200, "DONE"), appt(300, "CANCELLED"))
        assertEquals(600, ShopLogic.dayTakings(list))
    }

    @Test fun takings_emptyIsZero() {
        assertEquals(0, ShopLogic.dayTakings(emptyList()))
    }

    @Test fun doneCount_correct() {
        val list = listOf(appt(400, "DONE"), appt(600, "BOOKED"), appt(200, "DONE"))
        assertEquals(2, ShopLogic.doneCount(list))
    }

    @Test fun validate_blankName() {
        assertEquals("name", ShopLogic.validateAppointment("  ", 400, 540))
    }

    @Test fun validate_negativePrice() {
        assertEquals("price", ShopLogic.validateAppointment("Amine", -1, 540))
    }

    @Test fun validate_badTime() {
        assertEquals("time", ShopLogic.validateAppointment("Amine", 400, 24 * 60))
        assertEquals("time", ShopLogic.validateAppointment("Amine", 400, -5))
    }

    @Test fun validate_ok() {
        assertNull(ShopLogic.validateAppointment("Amine", 400, 540))
    }

    @Test fun formatDzd_containsDzd() {
        assertTrue(ShopLogic.formatDzd(1500).contains("DZD"))
    }

    @Test fun formatTime_midnightAndNoon() {
        assertEquals("00:00", ShopLogic.formatTime(0))
        assertEquals("12:30", ShopLogic.formatTime(750))
        assertEquals("09:00", ShopLogic.formatTime(540))
    }
}
