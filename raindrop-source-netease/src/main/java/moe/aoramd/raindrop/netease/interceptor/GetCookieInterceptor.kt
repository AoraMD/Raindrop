package moe.aoramd.raindrop.netease.interceptor

import moe.aoramd.lookinglass.manager.ContextManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.lang.Exception

class GetCookieInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response
        try {
            response = chain.proceed(chain.request())
        } catch (e: Exception) {
            e.printStackTrace()
            throw IOException("Network error")
        }
        response.headers("Set-Cookie").let {
            if (it.isNotEmpty()) {
                val cookies = HashSet<String>()
                for (cookie in it)
                    cookies.add(cookie)
                val editor = ContextManager.sharedPreferences("netease-cookie").edit()
                editor.putStringSet("cookies", cookies)
                editor.apply()
            }
        }
        return response
    }
}