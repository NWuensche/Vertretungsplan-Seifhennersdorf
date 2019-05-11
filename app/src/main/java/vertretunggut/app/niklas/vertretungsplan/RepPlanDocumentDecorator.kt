package vertretunggut.app.niklas.vertretungsplan

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import java.io.IOException
import java.util.concurrent.ExecutionException

/**
 * Created by nwuensche on 26.09.16.
 */
class RepPlanDocumentDecorator {

    private var repPlan: Document? = null

    val tableTitle: String
        get() = repPlan!!.select(".list-table-caption").text()

    val repPageTable: Elements
        get() = repPlan!!.select(".list-table tr")

    constructor(URL: String) {
        try {
            repPlan = DocumentAsync().execute(URL).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

    }

    constructor(repPlan: Document) {
        this.repPlan = repPlan
    }

    fun repPlanAvailable(): Boolean {
        return tableTitle != ""
    }

    companion object {
        private val FIRST_SITE = 1

        fun extract(line: Element): Elements {
            return line.select("td")
        }

        fun createTodaysDocument(activity: MainActivity): RepPlanDocumentDecorator {
            var currentSite = FIRST_SITE
            var firstRepPlanHTML = createDocument(currentSite)
            val WochenTagVer = DayOfWeek.getDayOfWeekOfRepPlan(firstRepPlanHTML)
            val WochenTagHeute = DayOfWeek.todaysDayOfWeek

            val Difference = WochenTagHeute!!.getDifferenceTo(WochenTagVer!!)
            if (Difference > 0) {
                currentSite = Difference % 5 + 1
                val nextRepPlanHTML = createDocument(currentSite)
                if (nextRepPlanHTML.repPlanAvailable()) {
                    firstRepPlanHTML = nextRepPlanHTML
                } else {
                    currentSite = FIRST_SITE
                }
            }

            activity.setCurrentRepPlanSite(currentSite)

            return firstRepPlanHTML
        }

        fun createDocument(SiteNumber: Int): RepPlanDocumentDecorator {
            var doc: Document? = null

            // Doesn't work for some reason when using DocumentAsync also here
            try {
                doc = Jsoup.connect("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00$SiteNumber.html")
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.de")
                        .ignoreHttpErrors(true)
                        .get()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            //TODO Nicht mit !! machen
            return RepPlanDocumentDecorator(doc!!)
        }
    }
}
