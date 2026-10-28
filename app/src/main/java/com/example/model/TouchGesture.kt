package com.example.model

enum class TouchGesture(val label: String, val description: String) {
    SINGLE_TAP(
        label = "Ketukan Tunggal (Single Tap)",
        description = "Ketuk tombol virtual satu kali"
    ),
    DOUBLE_TAP(
        label = "Ketukan Ganda (Double Tap)",
        description = "Ketuk tombol virtual dua kali dengan cepat"
    ),
    LONG_PRESS(
        label = "Tekan Lama (Long Press)",
        description = "Tahan tombol virtual selama 0.5 detik"
    )
}
