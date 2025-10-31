package com.extremesudoku.data.models.pvp

/**
 * Oyuncu bağlantı durumu
 */
sealed class ConnectionState {
    object Connected : ConnectionState()
    object Disconnected : ConnectionState()
    object Reconnecting : ConnectionState()
    object CONNECTION_LOST : ConnectionState()
}
