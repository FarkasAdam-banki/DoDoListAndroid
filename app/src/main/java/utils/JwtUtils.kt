package utils

import android.util.Base64
import org.json.JSONObject

object JwtUtils {
    fun decodeJwt(jwt: String): String? {
        return try {
            val payload = jwt.split(".")[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedJson = String(decodedBytes, Charsets.UTF_8)
            val jsonObject = JSONObject(decodedJson)
            jsonObject.getString("email")
        } catch (e: Exception) {
            null
        }
    }
}