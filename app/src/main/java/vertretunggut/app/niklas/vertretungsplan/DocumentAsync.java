package vertretunggut.app.niklas.vertretungsplan;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class DocumentAsync extends AsyncTask<String, Void, Document> {
    @Override
    protected Document doInBackground(String... strings) {
        String URL = strings[0];
        try {
           return Jsoup.connect(URL)
                   .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                   .referrer("http://www.google.de")
                   .ignoreHttpErrors(true)
                   .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
