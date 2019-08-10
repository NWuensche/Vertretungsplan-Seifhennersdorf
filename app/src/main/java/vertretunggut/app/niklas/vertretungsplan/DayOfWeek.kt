package vertretunggut.app.niklas.vertretungsplan


import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by nwuensche on 22.09.16.
 */
enum class DayOfWeek constructor(val dayOfWeek: Int) {
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    WEEKEND(6),
    ERROR(7);

    fun getDifferenceTo(compare: DayOfWeek): Int {
        return dayOfWeek - compare.dayOfWeek
    }

    companion object {

        fun getDayOfWeekOfRepPlan(repPlan: RepPlanDocumentDecorator): DayOfWeek? {
            val title = repPlan.tableTitle
            val datumVertretungsplan = getParsedWeekDayNumber(title)

            return getParsedWeekday(datumVertretungsplan)
        }

        private fun getParsedWeekDayNumber(titleTable: String): Int {
            val itemsOfTitle = StringTokenizer(titleTable)
            itemsOfTitle.nextToken() // Ignore shown Weekday, because "Heute" creates problems
            val date = itemsOfTitle.nextToken()

            val parsedDate = parseStringToDate(date)

            val c = Calendar.getInstance()
            c.time = parsedDate

            return c.get(Calendar.DAY_OF_WEEK)
        }

        private fun parseStringToDate(date: String): Date? {
            var dateParser: Date? = null // TODO besser

            try {
                dateParser = SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY).parse(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return dateParser
        }

        private fun getParsedWeekday(dayOfWeek: Int): DayOfWeek? {
            // Caution: 1st day is Sunday
            when (dayOfWeek) {
                2 -> return DayOfWeek.MONDAY
                3 -> return DayOfWeek.TUESDAY
                4 -> return DayOfWeek.WEDNESDAY
                5 -> return DayOfWeek.THURSDAY
                6 -> return DayOfWeek.FRIDAY
                7 -> return DayOfWeek.WEEKEND
                1 -> return DayOfWeek.WEEKEND
            }
            return null // TODO besser
        }

        val todaysDayOfWeek: DayOfWeek?
            get() {
                val cal = Calendar.getInstance()
                val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                return getParsedWeekday(dayOfWeek)
            }
    }
}
