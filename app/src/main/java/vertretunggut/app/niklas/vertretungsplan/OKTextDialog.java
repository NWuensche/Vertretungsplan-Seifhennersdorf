package vertretunggut.app.niklas.vertretungsplan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

/**
 * Created by nwuensche on 26.09.16.
 */
public class OKTextDialog implements DialogBuilder {
    private MainActivity activity;
    private String title;

    public OKTextDialog(MainActivity activity, String title) {
        this.activity = activity;
        this.title = title;
    }

    @Override
    public void buildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View rootView = activity.getLayoutInflater().inflate(R.layout.genaueritem, null);
        TextView genauerT = (TextView) rootView.findViewById(R.id.genauertextview);
        genauerT.setText(title);

        builder.setView(rootView)

                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .create()
                .show();
    }
}
