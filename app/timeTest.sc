import java.sql.Timestamp
import java.time.ZoneId
import java.util.TimeZone

TimeZone.setDefault(TimeZone.getTimeZone("UTC")) // TODO move this to the app initialization and test it

val utcClock = java.time.Clock.systemUTC()
val now = utcClock.instant()
now.atZone(ZoneId.of("UTC")).toInstant()
val currentTimestamp = Timestamp.from(now)
val fixedTimestamp = new Timestamp(now.toEpochMilli)
