package moe.aoramd.raindrop.netease.interceptor

import moe.aoramd.lookinglass.manager.ContextManager
import okhttp3.Interceptor
import okhttp3.Response

class GetCookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
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