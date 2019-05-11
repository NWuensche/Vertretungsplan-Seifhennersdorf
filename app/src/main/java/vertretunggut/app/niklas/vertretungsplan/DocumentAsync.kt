package vertretunggut.app.niklas.vertretungsplan

import android.os.AsyncTask

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.io.IOException

class DocumentAsync : AsyncTask<String, Void, Document>() {
    override fun doInBackground(vararg strings: String): Document? {
        val URL = strings[0]
        try {
            return Jsoup.connect(URL)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.de")
                    .ignoreHttpErrors(true)
                    .get()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}
