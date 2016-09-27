package vertretunggut.app.niklas.vertretungsplan;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by nwuensche on 25.09.16.
 */
public class SearchDialog implements DialogBuilder {

    MainActivity activity;

    public SearchDialog(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void buildDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final View rootView = activity.getLayoutInflater().inflate(R.layout.dialog, null);

        setUpDialog(builder, rootView);
        showKeyboard();
    }

    private void setUpDialog(AlertDialog.Builder builder, final View rootView) {
        builder.setView(rootView)
               .setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText EKlasse = (EditText) rootView.findViewById(R.id.editKlasse);
                        String search = EKlasse.getText().toString();
                        search.replaceAll("\\s+","");
                        activity.setSearch(search);
                        activity.restartRepPlanGetter();
                    }
                })

                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })

                .create()
                .show();
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }
}
