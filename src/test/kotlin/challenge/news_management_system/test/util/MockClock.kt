package challenge.news_management_system.test.util

import challenge.news_management_system.test.service.Resettable
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class MockClock : Clock(), Resettable {
    private var instant: Instant? = null
    private val zoneId = ZoneId.systemDefault()

    fun setTime(time: LocalDateTime) {
        instant = time.atZone(zoneId).toInstant()
    }

    override fun reset() {
        instant = null
    }

    override fun withZone(zone: ZoneId): Clock = fixed(instant(), zone)

    override fun getZone(): ZoneId = zoneId

    override fun instant(): Instant {
        return instant ?: Instant.now()
    }
}