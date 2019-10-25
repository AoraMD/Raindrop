package moe.aoramd.raindrop.netease.interceptor

import moe.aoramd.lookinglass.manager.ContextManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class PutCookieInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val sharedPreferences =
            ContextManager.sharedPreferences("netease-cookie")
        val cookies = sharedPreferences.getStringSet("cookies", hashSetOf()) as HashSet<String>
        for (cookie in cookies) {
            builder.addHeader("Cookie", cookie)
        }
        try {
            return chain.proceed(builder.build())
        } catch (e: Exception) {
            e.printStackTrace()
            throw IOException("Network error")
        }
    }
}