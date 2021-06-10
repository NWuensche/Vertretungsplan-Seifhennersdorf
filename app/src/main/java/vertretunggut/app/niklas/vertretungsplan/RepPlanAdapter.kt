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
        val convertView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_row, parent, false)

        val line = getItem(position)

        convertView!!.run {
            findViewById<TextView>(R.id.hour).text = line!!.hour
            findViewById<TextView>(R.id.teacher).text = line.teacher
            findViewById<TextView>(R.id.subject).text = line.subject
            findViewById<TextView>(R.id.room).text = line.room
            findViewById<TextView>(R.id.schoolClass).text = line.schoolClass
            findViewById<TextView>(R.id.type).text = line.type
            findViewById<TextView>(R.id.message).text = line.message
        }
        return convertView
    }

}
