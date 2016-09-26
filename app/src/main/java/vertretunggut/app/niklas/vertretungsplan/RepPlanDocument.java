package vertretunggut.app.niklas.vertretungsplan;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by nwuensche on 26.09.16.
 */
public class RepPlanDocument extends Document{

    public static final int FIRST_SITE = 1;

    public RepPlanDocument(String URL){
        super(URL);
    }

    public String getTableTitle(){
        return select(".list-table-caption").text();
    }

    public Elements getRepPageTable(Document repPlan) {
        return select(".list-table tr");
    }

    public boolean repPlanAvailable(){
        return !getTableTitle().equals("");
    }

    public static Elements extract(Element line) {
        return line.select("td");
    }

    public static RepPlanDocument createTodaysDocument(){
        int currentSite = FIRST_SITE;
        RepPlanDocument maybeRepPlanHTML = createDocument(currentSite);
        DayOfWeek WochenTagVer = DayOfWeek.getDayOfWeekOfRepPlan(maybeRepPlanHTML);
        DayOfWeek WochenTagHeute = DayOfWeek.getTodaysDayOfWeek();

        int Difference = WochenTagHeute.getDifferenceTo(WochenTagVer);
        if (Difference > 0) {
            currentSite = (Difference % 5) + 1;
            maybeRepPlanHTML = createDocument(currentSite); // TODO Optimieren
            if (!maybeRepPlanHTML.repPlanAvailable()) {
                currentSite = FIRST_SITE;
                maybeRepPlanHTML = createDocument(currentSite);
            }
        }
        return maybeRepPlanHTML;
    }

    public static RepPlanDocument createDocument(int SiteNumber){
        RepPlanDocument doc = new RepPlanDocument("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + SiteNumber + ".html"); // TODO No null!

        try {
            doc = (RepPlanDocument) Jsoup.connect("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + SiteNumber + ".html").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }
}
