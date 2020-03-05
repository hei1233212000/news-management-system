package challenge.news_management_system.web.util

import java.net.URLEncoder

fun String?.encodeAsUrlPath(): String = this?.let{
    URLEncoder.encode(this, "UTF-8")
            .replace("+", "%20")
} ?: ""