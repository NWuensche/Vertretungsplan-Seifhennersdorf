package vertretunggut.app.niklas.vertretungsplan

import arrow.core.Option
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class GetK private constructor(var currentSite: Int, val ignore: Int) {

    companion object {
         fun new(currentSite: Int) = GetK(currentSite, 0)
    }

    private val doc: Option<Document> = getSite()

    private fun getSite(): Option<Document> {
        val url = "http://www.gymnasium-seifhennersdorf.de/files/V_DH_00$currentSite.html"
        return Option.empty()
        /*async {
            try {
                val doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.de")
                        .ignoreHttpErrors(true)
                        .get()
                Option.just(doc)
            } catch (e: IOException) {
                Option.empty()
            }
        }*/
    }

}