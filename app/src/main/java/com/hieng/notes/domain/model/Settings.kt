package com.hieng.notes.domain.model

data class Settings(
    val viewMode: Boolean = false,
    val automaticTheme: Boolean = true,
    val darkTheme: Boolean = false,
    var dynamicTheme: Boolean = false,
    var amoledTheme: Boolean = false,
    var minimalisticMode: Boolean = false,
    var extremeAmoledMode: Boolean = false,
    var isMarkdownEnabled: Boolean = true,
    var screenProtection: Boolean = false,
    var encryptBackup: Boolean = false,
    var sortDescending: Boolean = true,
    var vaultSettingEnabled: Boolean = false,
    var vaultEnabled: Boolean = false,
    var editMode: Boolean = false,
    var gallerySync: Boolean = false,
    var showOnlyTitle: Boolean = false,
    var termsOfService: Boolean = true,
    var useMonoSpaceFont: Boolean = false,

    var cornerRadius: Int = 12,
)

