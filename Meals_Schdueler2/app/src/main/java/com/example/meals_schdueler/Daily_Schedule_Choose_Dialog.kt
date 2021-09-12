package com.example.meals_schdueler

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DailySchedule
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class Daily_Schedule_Choose_Dialog(

    dailyList: ArrayList<DailySchedule>,
    recipeList:  ArrayList<Recipe>,
    list:Recipe_Ingredients_List,
) : DialogFragment() , NestedScrollView.OnScrollChangeListener ,  SearchView.OnQueryTextListener , GetAndPost{

    lateinit var exit: ImageView
   // private var recipesList: ArrayList<Recipe>? = null
    private lateinit var btnDone: Button
    private var dailyList = dailyList
    private var recipeList = recipeList
    private var l: Recipe_Ingredients_List = list

    private lateinit var nestedScroll: NestedScrollView // list of ingredietns
    private var progressBar: ProgressBar? = null
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: TextView

    private var page = 0
    private var isSearch = false
    private var dailyToSearch = ""
    private var isScorlled = false
    private var quantities: String = ""
    private var numOfMeal: String = ""
    private var recipeIds: String = ""
    private var totalcost = 0.011


    private var dailyChoosenRecyclerViewAdapter: Daily_Schedule_Choose_RecyclerViewAdapter? =
        null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        dailyChoosenRecyclerViewAdapter = Daily_Schedule_Choose_RecyclerViewAdapter(
            dailyList!!, recipeList,l.list, childFragmentManager
        )

        var view: View =
            inflater.inflate(R.layout.daily_choose_list, container, false)
        btnDone = view.findViewById(R.id.doneBtn)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView

        dailyChoosenRecyclerViewAdapter!!.setmValues(dailyList!!)
        exit=view.findViewById(R.id.imageViewX)
        nestedScroll = view.findViewById(R.id.scroll_view)
        progressBar = view.findViewById(R.id.progress_bar)
        nestedScroll.setOnScrollChangeListener(this)
        searchView = view.findViewById(R.id.search_bar)
        searchView.setOnQueryTextListener(this)
        noResultsTextView = view.findViewById(R.id.tv_emptyTextView)


        btnDone.setOnClickListener({

            dismiss()
        })

        exit.setOnClickListener({
            dismiss()
        })

        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = dailyChoosenRecyclerViewAdapter
        startTask()
        return view

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        val parentFragment: Fragment? = parentFragment
        if (parentFragment is DialogInterface.OnDismissListener) {
            (parentFragment as DialogInterface.OnDismissListener?)!!.onDismiss(dialog)
        }



    }

    override fun onScrollChange(
        v: NestedScrollView?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        if (scrollY == v!!.getChildAt(0).measuredHeight - v.measuredHeight) {
            if (!isSearch) {
                page = page + 4
                progressBar!!.visibility = View.VISIBLE
                isScorlled = true
                startTask()
            }
        }
    }
    fun startTask() {

        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        if (p0 != "") {
            noResultsTextView.visibility = View.INVISIBLE
            isSearch = true
            dailyToSearch = p0!!
            startTask()
        } else {
            isSearch = false
            dailyList!!.clear()
            noResultsTextView.visibility = View.INVISIBLE
            startTask()
        }
        return true
    }

    override fun DoNetWorkOpreation(): String {
        if(!isScorlled){
            page =0
        }
        var string = UserInterFace.userID.toString() + " " + page

        var link = "https://elad1.000webhostapp.com/getDaily.php?ownerIDAndPage=" + string


        val sb = StringBuilder()

        val url = URL(link)
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            val `in`: InputStream = BufferedInputStream(urlConnection.inputStream)
            val bin = BufferedReader(InputStreamReader(`in`))
            // temporary string to hold each line read from the reader.
            var inputLine: String?

            while (bin.readLine().also { inputLine = it } != null) {
                sb.append(inputLine)

            }
        } finally {
            // regardless of success or failure, we will disconnect from the URLConnection.
            urlConnection.disconnect()
        }



        return sb.toString()
    }

    override fun getData(str: String) {
        progressBar!!.visibility = View.INVISIBLE
        if (!str.equals("")) {
            if (!isScorlled)
                dailyList!!.clear()



            quantities = ""
            numOfMeal = ""
            recipeIds = ""


            val dailyInfo: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

            // map to map each DailyID with a key as ID and contains all 3
            // array lists (e.g - quantities,recipeIds,numOfMeals)
            var map: HashMap<String, ArrayList<String>> = HashMap()
            var mapTotalCost: HashMap<String, Double> = HashMap()
            // first attach each meal to its dailyID.
            var dailyInfo2 = dailyInfo[0].splitIgnoreEmpty("*")
            var currentDailyID = dailyInfo2[0].toInt()
            for (i in dailyInfo.indices) {
                dailyInfo2 = dailyInfo[i].splitIgnoreEmpty("*")
                //means we switch to the next DailyID
                if (dailyInfo2[0].toInt() != currentDailyID) {
                    var totalLists: ArrayList<String> = ArrayList()
                    totalLists.add(quantities)
                    totalLists.add(numOfMeal)
                    totalLists.add(recipeIds)

                    // gathering all quantities , numOfMeal and recipeIds under
                    // the key of that DailyID
                    map.put(currentDailyID.toString(), totalLists)
                    mapTotalCost.put(currentDailyID.toString(), totalcost)
                    //switching to the next DailyID
                    currentDailyID = dailyInfo2[0].toInt()

                    // clearing the variables for next DailyID
                    quantities = ""
                    numOfMeal = ""
                    recipeIds = ""
                }
                quantities += "" + dailyInfo2[5] + " "
                numOfMeal += "" + dailyInfo2[3] + " "
                recipeIds += "" + dailyInfo2[4] + " "
                // saving the last total cost
                totalcost = dailyInfo2[2].toDouble()
            }
            if (!quantities.equals("")) {
                var totalLists: ArrayList<String> = ArrayList()
                totalLists.add(quantities)
                totalLists.add(numOfMeal)
                totalLists.add(recipeIds)
                map.put(currentDailyID.toString(), totalLists)
                mapTotalCost.put(currentDailyID.toString(), totalcost)
            }

            //  recipeNumbers += "" + i + " "
            // making DailyScheudle objects
            currentDailyID = -1
            for (i in dailyInfo.indices) {
                var dailyInfo2 = dailyInfo[i].splitIgnoreEmpty("*")
                if (dailyInfo2[0].toInt() != currentDailyID) {
                    dailyList!!.add(
                        DailySchedule(
                            dailyInfo2[0].toInt(),
                            dailyInfo2[1].toInt(),
                            map.get(dailyInfo2[0])!!.get(1),
                            map.get(dailyInfo2[0])!!.get(0),
                            map.get(dailyInfo2[0])!!.get(2),
                            mapTotalCost.get(dailyInfo2[0])!!,
                            false

                        )
                    )
                    currentDailyID = dailyInfo2[0].toInt()
                }
            }

            // TreeMap to store values of HashMap

            // TreeMap to store values of HashMap
            // sorted = TreeMap<String, DailySchedule>()

            // Copy all data from hashMap into TreeMap

            // Copy all data from hashMap into TreeMap


            // now getting the recipes for each daily


            currentDailyID = -1


            //UserPropertiesSingelton.getInstance()!!.setUserDaily(sorted)
            dailyChoosenRecyclerViewAdapter!!.setmValues(dailyList!!)
          //  dailyChoosenRecyclerViewAdapter!!.setRecipeList(recipeList!!)
            progressBar!!.visibility = View.INVISIBLE


//            dailyList!!.clear()
//            recipeList!!.clear()


            //save it also in singleton
            //  UserPropertiesSingelton.getInstance()!!.setUserRecipess(recipeList)
            //dailyRecyclerViewAdapter!!.setmValues(recipeList!!)
            isScorlled = false
        }
        isScorlled = false
    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

}