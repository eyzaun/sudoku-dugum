package com.extremesudoku.utils

import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Error Message Helper
 * Hataları kullanıcı dostu Türkçe mesajlara çevirir
 */
object ErrorMessages {
    
    /**
     * Exception'ı kullanıcı dostu mesaja çevir
     */
    fun getErrorMessage(error: Throwable): String {
        return when (error) {
            // Network errors
            is UnknownHostException -> "İnternet bağlantınızı kontrol edin"
            is SocketTimeoutException -> "Bağlantı zaman aşımına uğradı. Tekrar deneyin"
            is FirebaseNetworkException -> "İnternet bağlantısı gerekli"
            
            // Firestore errors
            is FirebaseFirestoreException -> {
                when (error.code) {
                    FirebaseFirestoreException.Code.PERMISSION_DENIED -> 
                        "Bu işlem için yetkiniz yok"
                    FirebaseFirestoreException.Code.UNAVAILABLE -> 
                        "Sunucu şu anda ulaşılamıyor. Lütfen tekrar deneyin"
                    FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> 
                        "İstek zaman aşımına uğradı"
                    FirebaseFirestoreException.Code.NOT_FOUND -> 
                        "İstenen veri bulunamadı"
                    FirebaseFirestoreException.Code.ALREADY_EXISTS -> 
                        "Bu veri zaten mevcut"
                    FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> 
                        "Quota aşıldı. Lütfen daha sonra tekrar deneyin"
                    else -> "Bir hata oluştu: ${error.message}"
                }
            }
            
            // Auth errors
            is FirebaseAuthException -> {
                when (error.errorCode) {
                    "ERROR_INVALID_EMAIL" -> "Geçersiz e-posta adresi"
                    "ERROR_WRONG_PASSWORD" -> "Yanlış şifre"
                    "ERROR_USER_NOT_FOUND" -> "Kullanıcı bulunamadı"
                    "ERROR_USER_DISABLED" -> "Hesabınız devre dışı bırakılmış"
                    "ERROR_TOO_MANY_REQUESTS" -> "Çok fazla deneme. Lütfen bekleyin"
                    "ERROR_NETWORK_REQUEST_FAILED" -> "İnternet bağlantınızı kontrol edin"
                    else -> "Giriş hatası: ${error.message}"
                }
            }
            
            // Generic Firebase error
            is FirebaseException -> "Firebase hatası: ${error.message}"
            
            // PvP specific errors
            else -> {
                val message = error.message ?: "Bilinmeyen hata"
                when {
                    message.contains("match", ignoreCase = true) -> 
                        "Maç yüklenemedi. Lütfen tekrar deneyin"
                    message.contains("opponent", ignoreCase = true) -> 
                        "Rakip bilgisi alınamadı"
                    message.contains("timeout", ignoreCase = true) -> 
                        "Zaman aşımı. Lütfen tekrar deneyin"
                    else -> message
                }
            }
        }
    }
    
    /**
     * PvP özel hata mesajları
     */
    object PvP {
        const val MATCHMAKING_FAILED = "Eşleşme başarısız. Lütfen tekrar deneyin"
        const val MATCH_NOT_FOUND = "Maç bulunamadı"
        const val OPPONENT_DISCONNECTED = "Rakibiniz bağlantısını kaybetti"
        const val CONNECTION_LOST = "Bağlantınız kesildi. Yeniden bağlanılıyor..."
        const val RECONNECTING = "Yeniden bağlanıyor..."
        const val RECONNECTED = "Bağlantı yeniden kuruldu!"
        const val MATCH_EXPIRED = "Maç süresi doldu"
        const val ALREADY_IN_MATCH = "Zaten bir maçtasınız"
        const val NO_PLAYERS_AVAILABLE = "Şu anda müsait oyuncu yok. Lütfen daha sonra tekrar deneyin"
    }
    
    /**
     * Genel mesajlar
     */
    object General {
        const val LOADING = "Yükleniyor..."
        const val PLEASE_WAIT = "Lütfen bekleyin..."
        const val SUCCESS = "Başarılı!"
        const val ERROR = "Hata oluştu"
        const val TRY_AGAIN = "Tekrar deneyin"
        const val CANCEL = "İptal"
        const val OK = "Tamam"
        const val YES = "Evet"
        const val NO = "Hayır"
    }
}
