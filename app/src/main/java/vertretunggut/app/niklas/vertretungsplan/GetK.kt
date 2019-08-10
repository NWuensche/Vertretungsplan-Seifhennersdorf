package vertretunggut.app.niklas.vertretungsplan

import android.util.Log
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.util.*

class GetK (var currentSite: Int, val searchForToday: Boolean) {
    companion object {
        private val FIRST_SITE = 1
    }

    var repPlanHTML: Option<RepPlanDocumentDecorator> = Option.empty()
    var repPlanTable: Option<Elements> = Option.empty()
    init {
        val firstDoc = getDoc(currentSite)
        repPlanHTML = firstDoc.map { RepPlanDocumentDecorator(it) }
        repPlanTable = repPlanHTML.map { it.repPageTable }

        if (searchForToday) {
            //Jump to today, if possible
            //TODO Ein bisschen Redundant mit oben
            repPlanHTML = firstDoc.map { fDoc ->
                val firstRepPlanHTML = RepPlanDocumentDecorator(fDoc)
                val difference = differenceToToday(firstRepPlanHTML)
                if (difference > 0) {
                    val approxCurrentSite = difference % 5 + 1
                    val newDoc = getDoc(approxCurrentSite)
                    //When approximated current day is not available, use the old site again
                    newDoc.fold(
                            {
                                repPlanTable = Some(firstRepPlanHTML.repPageTable)
                                firstRepPlanHTML
                            },
                            { nDoc ->
                                currentSite = approxCurrentSite
                                val newRepPlan = RepPlanDocumentDecorator(nDoc)
                                repPlanTable = Some(newRepPlan.repPageTable)
                                newRepPlan
                            }
                    )
                } else {
                    firstRepPlanHTML
                }
        }

        }
    }

    private fun differenceToToday(repPlanHTML: RepPlanDocumentDecorator): Int {
        val WochenTagHeute = DayOfWeek.todaysDayOfWeek
        val WochenTagVer = DayOfWeek.getDayOfWeekOfRepPlan(repPlanHTML)

        val Difference = WochenTagHeute!!.getDifferenceTo(WochenTagVer!!)
        return Difference
    }

    //Doc also empty when no title -> No timetable available
    private fun getDoc(currentSite: Int): Option<Document> {
        val url = "http://www.gymnasium-seifhennersdorf.de/files/V_DH_00$currentSite.html"
        val docO: Option<Document> =
                try {
                    val doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.de")
                        .ignoreHttpErrors(true)
                        .get()
                    when {
                        doc.isAvailable() -> Some(doc)
                        else -> Option.empty()
                    }
                } catch (e: IOException) {
                    Option.empty()
                }
        Log.e("TEST", if (docO.isDefined()) "DEF" else "NOT Def")
        return docO
    }

    fun parseAndStoreRepPageTable(): List<RepPlanLine> {
       return repPlanTable.map { table ->
            table
                    .map {line ->
                val allDataInCurrentLine = RepPlanDocumentDecorator.extract(line)
                parseAndStoreDataInLine(allDataInCurrentLine, "", false)
                    }
                    .flatMap { it.toList() } //filter empty options
        }
                .getOrElse { emptyList() }

    }

    //If in search, always use the last set hour as current Hour. lastSetHour is the last displayed hour in the table, so the current hour.
    private fun parseAndStoreDataInLine(allDataInCurrentLine: Elements, lastSetHour: String, isSearch: Boolean): Option<RepPlanLine> {
        if (allDataInCurrentLine.size == 0) return Option.empty()
        
        val line = RepPlanLine(
                hour = if (isSearch) lastSetHour else allDataInCurrentLine[0].text(), //Show everywhere hour if in search
                teacher = allDataInCurrentLine[1].text(),
                subject = allDataInCurrentLine[2].text(),
                room = allDataInCurrentLine[3].text(),
                schoolClass = allDataInCurrentLine[4].text(),
                type = allDataInCurrentLine[5].text(),
                message = allDataInCurrentLine[6].text()
                )

        if (!line.isEmpty) return Option.just(line)

        return Option.empty()
    }

}

//TODO How often DEF in log?
fun Document.isAvailable(): Boolean {
    return select(".list-table-caption").text().isNotEmpty()
}
