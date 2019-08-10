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
class RepPlanDocumentDecorator(private val repPlan: Document) {

    val tableTitle: String
        get() = repPlan.select(".list-table-caption").text()

    val repPageTable: Elements
        get() = repPlan.select(".list-table tr")

    fun repPlanAvailable(): Boolean {
        return tableTitle != ""
    }

    companion object {
        fun extract(line: Element): Elements {
            return line.select("td")
        }

    }
}
