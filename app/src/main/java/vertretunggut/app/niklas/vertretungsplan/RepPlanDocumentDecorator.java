package vertretunggut.app.niklas.vertretungsplan;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by nwuensche on 26.09.16.
 */
public class RepPlanDocumentDecorator extends Document{

    public static final int FIRST_SITE = 1;

    public RepPlanDocumentDecorator(String URL){
        super(URL);
    }

    public String getTableTitle(){
        return select(".list-table-caption").text();
    }

    public Elements getRepPageTable() {
        return select(".list-table tr");
    }

    public boolean repPlanAvailable(){
        return !getTableTitle().equals("");
    }

    public static Elements extract(Element line) {
        return line.select("td");
    }

    public static RepPlanDocumentDecorator createTodaysDocument(){
        int currentSite = FIRST_SITE;
        RepPlanDocumentDecorator maybeRepPlanHTML = createDocument(currentSite);
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

    public static RepPlanDocumentDecorator createDocument(int SiteNumber){
        RepPlanDocumentDecorator doc = new RepPlanDocumentDecorator("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + SiteNumber + ".html"); // TODO No null!

        try {
            doc = (RepPlanDocumentDecorator) Jsoup.connect("http://www.gymnasium-seifhennersdorf.de/files/V_DH_00" + SiteNumber + ".html").get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return doc;
    }
}