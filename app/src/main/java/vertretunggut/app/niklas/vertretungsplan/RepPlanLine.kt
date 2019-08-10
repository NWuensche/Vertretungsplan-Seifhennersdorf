package vertretunggut.app.niklas.vertretungsplan

/**
 * Created by nwuensche on 30.09.16.
 */
data class RepPlanLine(val hour: String, val teacher: String, val subject: String, val room: String, val schoolClass: String, val type: String, val message: String) {
    val isEmpty: Boolean
        get() = hour == "" && teacher == "" && subject == "" && room == "" && schoolClass == "" && type == "" && message == ""
}
