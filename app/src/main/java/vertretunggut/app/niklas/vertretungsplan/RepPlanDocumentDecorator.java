package vertretunggut.app.niklas.vertretungsplan;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created by nwuensche on 26.09.16.
 */
public class RepPlanDocumentDecorator {

    private Document repPlan;
    private static final int FIRST_SITE = 1;

    public RepPlanDocumentDecorator(String URL) {
        try {
            repPlan = new DocumentAsync(URL).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public RepPlanDocumentDecorator(Document repPlan) {
        this.repPlan = repPlan;
    }

    public String getTableTitle() {
        return repPlan.select(".list-table-caption").text();
    }

    public Elements getRepPageTable() {
        return repPlan.select(".list-table tr");
    }

    public boolean repPlanAvailable() {
        return !getTableTitle().equals("");
    }

    public static Elements extract(Element line) {
        return line.select("td");
    }

    public static RepPlanDocumentDecorator createTodaysDocument(MainActivity activity) {
        int currentSite = FIRST_SITE;
        RepPlanDocumentDecorator firstRepPlanHTML = createDocument(currentSite);
        DayOfWeek WochenTagVer = DayOfWeek.getDayOfWeekOfRepPlan(firstRepPlanHTML);
        DayOfWeek WochenTagHeute = DayOfWeek.getTodaysDayOfWeek();

        int Difference = WochenTagHeute.getDifferenceTo(WochenTagVer);
        if (Difference > 0) {
            currentSite = (Difference % 5) + 1;
            RepPlanDocumentDecorator nextRepPlanHTML = createDocument(currentSite);
            if (nextRepPlanHTML.repPlanAvailable()) {
                firstRepPlanHTML = nextRepPlanHTML;
            }
            else{
                currentSite = FIRST_SITE;
            }
        }

        activity.setCurrentRepPlanSite(currentSite);

        return firstRepPlanHTML;
    }

    public static RepPlanDocumentDecorator createDocument(int SiteNumber) {
        Document doc = null;

       /* try {
            doc = new DocumentAsync("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + SiteNumber + ".html").execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        try {
            doc = Jsoup.connect("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + SiteNumber + ".html")
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.89 Safari/537.36")
                    .referrer("http://www.google.de")
                    .ignoreHttpErrors(true)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new RepPlanDocumentDecorator(doc);
    }
}
