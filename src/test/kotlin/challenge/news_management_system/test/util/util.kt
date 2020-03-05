package challenge.news_management_system.test.util

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.beanutils.BeanMap
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Any.toJson(): String = ObjectMapper().writeValueAsString(this)

fun Any?.toFieldValueMap(): Map<String, String> = this?.let {
    BeanMap(this)
            .map { (k, v) ->
                when (v) {
                    is LocalDateTime -> k.toString() to v.toIso8601Format()
                    else -> k.toString() to v.toString()
                }
            }
            .toMap()
} ?: emptyMap()

fun LocalDateTime?.toIso8601Format(): String = this?.let {
    format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
} ?: ""