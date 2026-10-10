package dz.coiffuredz.app

/** Minimal FR/AR strings. Clean-room, no external translations. */
enum class Lang { FR, AR }

object Strings {
    fun get(l: Lang): Map<String, String> = when (l) {
        Lang.FR -> mapOf(
            "title" to "CoiffureDZ",
            "tab_book" to "Rendez-vous",
            "tab_cash" to "Recette",
            "tab_clients" to "Clients",
            "add" to "+ Ajouter",
            "client" to "Client",
            "service" to "Service",
            "time" to "Heure (ex 0930)",
            "chair" to "Fauteuil",
            "save" to "Enregistrer",
            "cancel" to "Annuler",
            "done" to "Terminé",
            "cancelled" to "Annulé",
            "booked" to "Réservé",
            "takings" to "Recette du jour",
            "cuts" to "Coupes terminées",
            "history" to "Historique",
            "phone" to "Téléphone",
            "today" to "Aujourd'hui",
            "empty" to "Aucun rendez-vous. Ajoutez le premier client.",
            "lang" to "العربية"
        )
        Lang.AR -> mapOf(
            "title" to "كوافير ديزاد",
            "tab_book" to "المواعيد",
            "tab_cash" to "المداخيل",
            "tab_clients" to "الزبائن",
            "add" to "+ إضافة",
            "client" to "الزبون",
            "service" to "الخدمة",
            "time" to "الوقت (مثال 0930)",
            "chair" to "الكرسي",
            "save" to "حفظ",
            "cancel" to "إلغاء",
            "done" to "تم",
            "cancelled" to "ملغي",
            "booked" to "محجوز",
            "takings" to "مداخيل اليوم",
            "cuts" to "الخدمات المنجزة",
            "history" to "السجل",
            "phone" to "الهاتف",
            "today" to "اليوم",
            "empty" to "لا توجد مواعيد. أضف أول زبون.",
            "lang" to "Français"
        )
    }
}
