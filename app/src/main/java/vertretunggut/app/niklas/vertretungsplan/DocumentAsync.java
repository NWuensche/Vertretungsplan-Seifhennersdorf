package vertretunggut.app.niklas.vertretungsplan;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class DocumentAsync extends AsyncTask<Void, Void, Document> {
    private String URL;

    DocumentAsync(String URL) {
        this.URL = URL;
    }

    @Override
    protected Document doInBackground(Void... voids) {

        try {
           return Jsoup.connect(URL)
                   .userAgent("Mozilla")
                   .referrer("http://www.google.de")
                   .ignoreHttpErrors(true)
                   .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
