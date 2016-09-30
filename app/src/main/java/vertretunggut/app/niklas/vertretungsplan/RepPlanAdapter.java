package vertretunggut.app.niklas.vertretungsplan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nwuensche on 30.09.16.
 */
public class RepPlanAdapter extends ArrayAdapter<RepPlanLine> {

    public RepPlanAdapter(Context context, ArrayList<RepPlanLine> lines) {
        super(context, 0, lines);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RepPlanLine line = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);
        }

        TextView hour = (TextView) convertView.findViewById(R.id.hour);
        TextView teacher = (TextView) convertView.findViewById(R.id.teacher);
        TextView subject = (TextView) convertView.findViewById(R.id.subject);
        TextView room = (TextView) convertView.findViewById(R.id.room);
        TextView schoolClass = (TextView) convertView.findViewById(R.id.schoolClass);
        TextView type = (TextView) convertView.findViewById(R.id.type);
        TextView message = (TextView) convertView.findViewById(R.id.message);

        hour.setText(line.getHour());
        teacher.setText(line.getTeacher());
        subject.setText(line.getSubject());
        room.setText(line.getRoom());
        schoolClass.setText(line.getSchoolClass());
        type.setText(line.getType());
        message.setText(line.getMessage());

        return convertView;
    }

}
