package vertretunggut.app.niklas.vertretungsplan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by nwuensche on 25.09.16.
 */
public class AboutDialog implements DialogBuilder {
    MainActivity activity;

    public AboutDialog(MainActivity activity) {
        this.activity = activity;
    }

    public void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View rootView = activity.getLayoutInflater().inflate(R.layout.uber, null);

        addOKButton(builder, rootView);
        addPicture(rootView);
        addTextViews(rootView);
    }

    private void addOKButton(AlertDialog.Builder builder, final View rootView) {
        builder.setView(rootView)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create()
                .show();
    }

    private void addPicture(final View rootView) {
        ImageView I = (ImageView) rootView.findViewById(R.id.CC);
        I.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://creativecommons.org/licenses/by/3.0/"));
                activity.startActivity(browserIntent);
            }
        });
    }

    private void addTextViews(final View rootView) {
        String urlCCText = "http://game-icons.net/";
        String urlJSoupLicense = "http://jsoup.org/license";
        String urlJSoup = "http://jsoup.org/";
        int idCCText = R.id.CCText;
        int idMIT = R.id.mit;
        int idMITText = R.id.mittext;

        addTextView(rootView, idCCText, urlCCText);
        addTextView(rootView, idMIT, urlJSoupLicense);
        addTextView(rootView, idMITText, urlJSoup);
    }

    private void addTextView(final View rootView, int id, final String url) {
        TextView T = (TextView) rootView.findViewById(id);
        T.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                activity.startActivity(browserIntent);

            }
        });
    }
}
