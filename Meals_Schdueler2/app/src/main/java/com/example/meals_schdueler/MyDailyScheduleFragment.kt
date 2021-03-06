package com.example.meals_schdueler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DailySchedule
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MyDailyScheduleFragment : Fragment(), GetAndPost {


    private var columnCount = 1
    private var dailyList: ArrayList<DailySchedule>? = null
    private var recipeList: ArrayList<Recipe>? = null
    private var dailyRecyclerViewAdapter: My_Daily_RecylerViewAdapter? = null


    private var quantities: String = ""
    private var numOfMeal: String = ""
    private var recipeIds: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeList = ArrayList()
        dailyList = ArrayList()
        dailyRecyclerViewAdapter = My_Daily_RecylerViewAdapter(
            dailyList!!,
            childFragmentManager,
            this.context
        )

        // to know how many objects from the wanted type will be in a line
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }

    }


    companion object {

        var instance: MyDailyScheduleFragment? = null

        fun getInstance1(): MyDailyScheduleFragment {
            return instance!!
        }


        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            MyDailyScheduleFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)

                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val x = inflater.inflate(R.layout.my_daily_schedule_list, null)
        val recyclerView = x.findViewById<View>(R.id.list) as RecyclerView


        instance = this

        if (x is RecyclerView) {
            with(x) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                recyclerView.adapter = dailyRecyclerViewAdapter

            }
        }

        startTask()

        return x
    }

    fun startTask() {
        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }

    override fun DoNetWorkOpreation(): String {

        var link = "https://elad1.000webhostapp.com/getDaily.php?ownerID=" + UserInterFace.userID;


        Log.v("Elad1", "here")

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


        Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()
    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun getData(str: String) {

        if (!str.equals("")) {
            quantities = ""
            numOfMeal = ""
            recipeIds = ""
            recipeList!!.clear()
            dailyList!!.clear()
            Log.v("Elad1", "STR is" + str)
            val dailyInfo: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

            // map to map each DailyID with a key as ID and contains all 3
            // array lists (e.g - quantities,recipeIds,numOfMeals)
            var map: HashMap<String, ArrayList<String>> = HashMap()

            // first attach each meal to its dailyID.
            var currentDailyID = dailyInfo[0].get(0).toInt() - 48
            for (i in dailyInfo.indices) {
                var dailyInfo2 = dailyInfo[i].splitIgnoreEmpty(",")
                //means we switch to the next DailyID
                if (dailyInfo2[0].toInt() != currentDailyID) {
                    var totalLists: ArrayList<String> = ArrayList()
                    totalLists.add(quantities)
                    totalLists.add(numOfMeal)
                    totalLists.add(recipeIds)

                    // gathering all quantities , numOfMeal and recipeIds under
                    // the key of that DailyID
                    map.put(currentDailyID.toString(), totalLists)

                    //switching to the next DailyID
                    currentDailyID = dailyInfo2[0].toInt()

                    // clearing the variables for next DailyID
                    quantities = ""
                    numOfMeal = ""
                    recipeIds = ""
                }
                quantities += "" + dailyInfo2[4] + " "
                numOfMeal += "" + dailyInfo2[2] + " "
                recipeIds += "" + dailyInfo2[3] + " "
            }
            if (!quantities.equals("")) {
                var totalLists: ArrayList<String> = ArrayList()
                totalLists.add(quantities)
                totalLists.add(numOfMeal)
                totalLists.add(recipeIds)
                map.put(currentDailyID.toString(), totalLists)
            }

            //  recipeNumbers += "" + i + " "

            currentDailyID = -1
            for (i in dailyInfo.indices) {
                var dailyInfo2 = dailyInfo[i].splitIgnoreEmpty(",")
                if (dailyInfo2[0].toInt() != currentDailyID) {
                    dailyList!!.add(
                        DailySchedule(
                            dailyInfo2[0].toInt(),
                            dailyInfo2[1].toInt(),
                            map.get(dailyInfo2[0])!!.get(1),
                            map.get(dailyInfo2[0])!!.get(0),
                            map.get(dailyInfo2[0])!!.get(2),
                            false

                        )
                    )
                    currentDailyID = dailyInfo2[0].toInt()
                }
            }

            // now getting the recipes for each daily

            var j = 0
            currentDailyID = -1
            for (i in dailyList!!) {
                //taking all the recipes for this daily
                if (currentDailyID != i.dailyId) {
                    var ids = i.recipeIds.splitIgnoreEmpty(" ")
                    for (k in ids.indices) {
                        var recipe = deepCopyRecipe(
                            UserPropertiesSingelton.getInstance()!!.getUserMapRecipe()!!
                                .get(ids[j++].toInt())!!

                        )


                        recipeList!!.add(recipe)
                    }
                    j = 0
                    currentDailyID = i.dailyId
                }

            }

            Log.v("Elad1","DAILY SIZe" + dailyList!!.size)
            Log.v("Elad1","RECCCCIP#E SIZe" +recipeList!!.size)
            UserPropertiesSingelton.getInstance()!!.setUserDaily(dailyList)
            dailyRecyclerViewAdapter!!.setmValues(dailyList!!)
            dailyRecyclerViewAdapter!!.setRecipeList(recipeList!!)

//            dailyList!!.clear()
//            recipeList!!.clear()



            //save it also in singleton
            //  UserPropertiesSingelton.getInstance()!!.setUserRecipess(recipeList)
            //dailyRecyclerViewAdapter!!.setmValues(recipeList!!)
        }
    }

    private fun deepCopyRecipe(recipe: Recipe): Recipe {

        var listOfIngredients = ArrayList<Ingredient>()
        var listOfQuantities = ArrayList<Int>()

        for (i in recipe.listOfIngredients) {
            listOfIngredients.add(i)
        }
        for (i in recipe.quantityList) {
            listOfQuantities.add(i)
        }
        var copiedRecipe = Recipe(
            recipe.recipeId,
            recipe.ownerId,
            recipe.recipeName,
            recipe.pictureBitMap,
            recipe.typeOfMeal,
            recipe.numOfPortions,
            recipe.totalCost,
            recipe.shareRecipe,
            recipe.shareInfo,
            listOfIngredients,
            listOfQuantities
        )

        return copiedRecipe
    }


    fun deepCopy(arrToCopy: ArrayList<String>): ArrayList<String> {
        var s: ArrayList<String> = ArrayList()

        for (i in arrToCopy) {
            s.add(i)
        }
        return s
    }

}