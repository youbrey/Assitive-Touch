package com.example.model

enum class TouchAction(
    val title: String,
    val description: String,
    val isSystemAction: Boolean = true
) {
    OPEN_MENU(
        title = "Buka Menu",
        description = "Membuka panel menu virtual multi-fungsi",
        isSystemAction = false
    ),
    SCREENSHOT(
        title = "Tangkapan Layar",
        description = "Mengambil tangkapan layar instan",
        isSystemAction = true
    ),
    LOCK_SCREEN(
        title = "Kunci Layar",
        description = "Mengunci layar perangkat tanpa tombol fisik",
        isSystemAction = true
    ),
    VOLUME_PANEL(
        title = "Penyesuaian Volume",
        description = "Buka mixer kontrol volume media, nada dering, dan alarm",
        isSystemAction = false
    ),
    POWER_DIALOG(
        title = "Daya & Muat Ulang",
        description = "Menu sistem matikan daya (Turn Off) dan restart HP",
        isSystemAction = true
    ),
    HOME(
        title = "Beranda",
        description = "Kembali ke layar utama",
        isSystemAction = true
    ),
    BACK(
        title = "Kembali",
        description = "Kembali ke langkah atau layar sebelumnya",
        isSystemAction = true
    ),
    RECENTS(
        title = "Aplikasi Terbaru",
        description = "Buka daftar aplikasi yang baru dibuka",
        isSystemAction = true
    ),
    NOTIFICATIONS(
        title = "Panel Notifikasi",
        description = "Tarik ke bawah bayangan notifikasi",
        isSystemAction = true
    ),
    QUICK_SETTINGS(
        title = "Pengaturan Cepat",
        description = "Buka panel toggle pengaturan cepat sistem",
        isSystemAction = true
    ),
    SPLIT_SCREEN(
        title = "Layar Terpisah",
        description = "Buka mode multitasking dua jendela",
        isSystemAction = true
    ),
    FLASHLIGHT(
        title = "Senter / Flashlight",
        description = "Nyalakan atau matikan lampu senter",
        isSystemAction = false
    ),
    NONE(
        title = "Tidak Ada Aksi",
        description = "Tidak melakukan tindakan apapun",
        isSystemAction = false
    )
}
