package vertretunggut.app.niklas.vertretungsplan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import kotlinx.android.synthetic.main.list_row.view.*

import java.util.ArrayList

/**
 * Created by nwuensche on 30.09.16.
 */
class RepPlanAdapter(context: Context, lines: ArrayList<RepPlanLine>) : ArrayAdapter<RepPlanLine>(context, 0, lines) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val convertView = if (convertView != null)
            convertView
            else LayoutInflater.from(context).inflate(R.layout.list_row, parent, false)

        val line = getItem(position)

        convertView!!.run {
            hour.text = line!!.hour
            teacher.text = line.teacher
            subject.text = line.subject
            room.text = line.room
            schoolClass.text = line.schoolClass
            type.text = line.type
            message.text = line.message
        }
        return convertView
    }

}
