package com.example.model

enum class ButtonTheme(
    val title: String,
    val description: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val accentColorHex: Long,
    val isGlow: Boolean = false
) {
    CLASSIC_IOS(
        title = "Classic iOS",
        description = "Gaya ikonik translucent lingkaran abu-abu & putih bersih",
        primaryColorHex = 0xFFFFFFFF,
        secondaryColorHex = 0x992B2B2B,
        accentColorHex = 0xFFECEFF1,
        isGlow = false
    ),
    CYBER_NEON(
        title = "Cyber Neon",
        description = "Cincin futuristik biru cyan menyala dengan aksen ungu",
        primaryColorHex = 0xFF00E5FF,
        secondaryColorHex = 0xCC0D1B2A,
        accentColorHex = 0xFF7C4DFF,
        isGlow = true
    ),
    EMERALD_GLOW(
        title = "Emerald Glow",
        description = "Nuansa hijau zamrud bercahaya lembut dan modern",
        primaryColorHex = 0xFF00E676,
        secondaryColorHex = 0xCC0A2218,
        accentColorHex = 0xFF69F0AE,
        isGlow = true
    ),
    SUNSET_BLAZE(
        title = "Sunset Blaze",
        description = "Gradasi jingga amber hangat yang energik dan dinamis",
        primaryColorHex = 0xFFFF9100,
        secondaryColorHex = 0xCC2A1608,
        accentColorHex = 0xFFFF5252,
        isGlow = true
    ),
    DARK_GLASS(
        title = "Dark Obsidian Glass",
        description = "Kaca hitam frosted elegan dengan bezel perak minimalis",
        primaryColorHex = 0xFFE0E0E0,
        secondaryColorHex = 0xDD121212,
        accentColorHex = 0xFF9E9E9E,
        isGlow = false
    ),
    MINIMAL_DOT(
        title = "Minimalist Dot",
        description = "Titik ringkas dan tidak mencolok untuk fokus maksimal",
        primaryColorHex = 0xFFFAFAFA,
        secondaryColorHex = 0x88424242,
        accentColorHex = 0xFFFFFFFF,
        isGlow = false
    )
}
