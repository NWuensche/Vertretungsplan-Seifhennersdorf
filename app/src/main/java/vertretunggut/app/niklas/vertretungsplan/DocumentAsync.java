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
                   .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.89 Safari/537.36")
                   .referrer("http://www.google.de")
                   .ignoreHttpErrors(true)
                   .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
