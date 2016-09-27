package vertretunggut.app.niklas.vertretungsplan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nwuensche on 23.09.16.
 */
public class RepPlan {
    List<String> preview;
    List<String> fullText;

    public RepPlan() {
        preview = new ArrayList<>();
        fullText = new ArrayList<>();
    }

    public void add(String input) {
        if(toLongForScreen(input)) {
            preview.add(input.substring(0, 3) + "..");
            fullText.add(input);
        }
        else {
            preview.add(input);
            fullText.add(input);
        }
    }

    private boolean toLongForScreen(String input) {
        return input.length() > 5;
    }

    public String getFullTextAt(int position) {
        return fullText.get(position);
    }

    public List<String> getPreviewList() {
        return preview;
    }

    public boolean containsContent() {
        return !preview.isEmpty();
    }
}
