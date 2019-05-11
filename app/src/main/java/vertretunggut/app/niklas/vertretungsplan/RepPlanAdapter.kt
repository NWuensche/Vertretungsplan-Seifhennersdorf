package vertretunggut.app.niklas.vertretungsplan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList

/**
 * Created by nwuensche on 30.09.16.
 */
class RepPlanAdapter(context: Context, lines: ArrayList<RepPlanLine>) : ArrayAdapter<RepPlanLine>(context, 0, lines) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val line = getItem(position)

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_row, parent, false)
        }

        val hour = convertView!!.findViewById<View>(R.id.hour) as TextView
        val teacher = convertView.findViewById<View>(R.id.teacher) as TextView
        val subject = convertView.findViewById<View>(R.id.subject) as TextView
        val room = convertView.findViewById<View>(R.id.room) as TextView
        val schoolClass = convertView.findViewById<View>(R.id.schoolClass) as TextView
        val type = convertView.findViewById<View>(R.id.type) as TextView
        val message = convertView.findViewById<View>(R.id.message) as TextView

        hour.text = line!!.hour
        teacher.text = line.teacher
        subject.text = line.subject
        room.text = line.room
        schoolClass.text = line.schoolClass
        type.text = line.type
        message.text = line.message

        return convertView
    }

}
