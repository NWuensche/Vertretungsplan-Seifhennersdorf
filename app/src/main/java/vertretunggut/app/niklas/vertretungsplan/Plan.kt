package vertretunggut.app.niklas.vertretungsplan

import android.util.Log
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import kotlinx.android.synthetic.main.activity_main.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException

class Plan (activity: MainActivity, searchForToday: Boolean) {

    var repPlanDecorator: Option<RepPlanDocumentDecorator> = Option.empty()
    var repPlanTable: Option<Elements> = Option.empty()

    init {
        var currentSite = activity.currentRepPlanSite
        val firstDoc = getDoc(currentSite)
        if (searchForToday) {
            //Jump to today, if possible
            repPlanDecorator = firstDoc.map { fDoc ->
                val firstRepPlanHTML = RepPlanDocumentDecorator(fDoc)
                val difference = differenceToToday(firstRepPlanHTML)
                if (difference > 0) {
                    val approxCurrentSite = difference % 5 + 1
                    val newDoc = getDoc(approxCurrentSite)
                    //When approximated current day is not available, use the old site again
                    newDoc.fold(
                            {
                                repPlanTable = Some(firstRepPlanHTML.repPageTable)
                                firstRepPlanHTML
                            },
                            { nDoc ->
                                currentSite = approxCurrentSite
                                val newRepPlan = RepPlanDocumentDecorator(nDoc)
                                repPlanTable = Some(newRepPlan.repPageTable)
                                newRepPlan
                            }
                    )
                } else {
                    firstRepPlanHTML
                }
                //TODO Return currentSite
            }

        } else {
            repPlanDecorator = firstDoc.map { RepPlanDocumentDecorator(it) }
            repPlanTable = repPlanDecorator.map { it.repPageTable }
        }
        activity.currentRepPlanSite = currentSite
    }

    private fun differenceToToday(repPlanHTML: RepPlanDocumentDecorator): Int {
        val WochenTagHeute = DayOfWeek.todaysDayOfWeek
        val WochenTagVer = DayOfWeek.getDayOfWeekOfRepPlan(repPlanHTML)

        val Difference = WochenTagHeute!!.getDifferenceTo(WochenTagVer!!)
        return Difference
    }

    //Doc also empty when no title -> No timetable available
    private fun getDoc(currSite: Int): Option<Document> {
        val url = "http://www.gymnasium-seifhennersdorf.de/files/V_DH_00$currSite.html"
        val docO: Option<Document> =
                try {
                    val doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.de")
                        .ignoreHttpErrors(true)
                        .get()
                        Some(doc)
                        /*when()...doc.isAvailable() -> Some(doc)
                        else -> Option.empty()*/
                } catch (e: IOException) {
                    Option.empty()
                }
        Log.e("TEST", if (docO.isDefined()) "DEF" else "NOT Def")
        return docO
    }

    //TODO Buttons don't get reenabled

    fun parseAndStoreRepPageTable(): List<RepPlanLine> {
       return repPlanTable.map { table ->
            table
                    .map {line ->
                val allDataInCurrentLine = RepPlanDocumentDecorator.extract(line)
                parseAndStoreDataInLine(allDataInCurrentLine, "", false)
                    }
                    .flatMap { it.toList() } //filter empty options
           //TODO Make easier
        }
                .getOrElse { emptyList() }

    }

    //If in search, always use the last set hour as current Hour. lastSetHour is the last displayed hour in the table, so the current hour.
    private fun parseAndStoreDataInLine(allDataInCurrentLine: Elements, lastSetHour: String, isSearch: Boolean): Option<RepPlanLine> {
        if (allDataInCurrentLine.size == 0) return Option.empty()

        val line = RepPlanLine(
                hour = if (isSearch) lastSetHour else allDataInCurrentLine[0].text(), //Show everywhere hour if in search
                teacher = allDataInCurrentLine[1].text(),
                subject = allDataInCurrentLine[2].text(),
                room = allDataInCurrentLine[3].text(),
                schoolClass = allDataInCurrentLine[4].text(),
                type = allDataInCurrentLine[5].text(),
                message = allDataInCurrentLine[6].text()
                )

        if (!line.isEmpty) return Option.just(line)

        return Option.empty()
    }

    //If in search, always use the last set hour as current Hour. lastSetHour is the last displayed hour in the table, so the current hour.
    fun startSearch(search: String): List<RepPlanLine> {
        var isFirstLine = true
        var lastSetHour = ""
        val rowsWithSearch = mutableListOf<Option<RepPlanLine>>()
        for (Zeile in repPlanTable.toList().flatten()) {
            val EinzelnZeile = RepPlanDocumentDecorator.extract(Zeile)
            if (isFirstLine) {
                isFirstLine = false
                continue
            }

            // Store last hour that was set
            if (!(EinzelnZeile.toTypedArray()[0] as Element).text().replace("\u00A0", "").isEmpty()) {
                lastSetHour = (EinzelnZeile.toTypedArray()[0] as Element).text().replace("\u00A0", "")
            }

            for (data in EinzelnZeile) {
                if (dataContainsSearch(data.text(), search)) {
                    rowsWithSearch.add(parseAndStoreDataInLine(EinzelnZeile, lastSetHour, true))
                    break
                }
            }
        }
        return rowsWithSearch.flatMap { it.toList() }
    }

    private fun dataContainsSearch(data: String, search: String): Boolean {
        var data = data
        data = data.toLowerCase()
        return data.contains(search.toLowerCase())
    }

    fun updateTitleBarTitle(parent: MainActivity, internetConnected: Boolean = true) {
        val titleBar = MainActivityWrapper(parent)

        var headerTitle: String
        if (!internetConnected) {
            titleBar.hideMoveButtons()
        } else {
            titleBar.showMoveButtons()
        }

        val somethingInTable = parent.list_view.adapter.count != 0
        //TODO Problem when in search and next day with titlebar
        if (nothingToShow(somethingInTable)) {
            headerTitle = "Kein Inhalt"
            if (!internetConnected) {
                headerTitle = "Keine Internetverbindung"
            }
            createDialogAndMaybeDisableButton(titleBar, parent)
        } else {
            headerTitle = repPlanDecorator.map{it.tableTitle}.getOrElse { "Kein Inhalt" }
        }

        titleBar.setTitle(headerTitle)
    }

    fun createDialogAndMaybeDisableButton(titleBar: MainActivityWrapper, parent:MainActivity) {
        val somethingInTable = parent.list_view.adapter.count != 0
        val toastMessage = repPlanDecorator.map {
            if (!it.repPlanAvailable()) {
                titleBar.disableLastPressedButton()
                "Keine Vertretungen für diesen Tag"
            } else if (searchFoundNothing(somethingInTable)) {
                parent.supportActionBar!!.title = it.tableTitle
                "Keine Vertretungen für " + parent.search + " an diesem Tag."
            } else {
                ""
            }
        }.getOrElse { "Kein Internet" } // Looks like it only happens when there is no Internet

        if (toastMessage != "Kein Internet") {
            parent.toast(toastMessage)
        }
    }

    fun searchFoundNothing(somethingInTable: Boolean): Boolean {
        return repPlanDecorator.map {
             it.repPlanAvailable() && !somethingInTable
        }.getOrElse { true }
    }

    fun nothingToShow(somethingInTable: Boolean): Boolean {
        return repPlanDecorator.map {
            !it.repPlanAvailable() || searchFoundNothing(somethingInTable)
        }.getOrElse { true }
    }

}

//TODO How often DEF in log?