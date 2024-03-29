package com.example.meals_schdueler

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
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

class Daily_Schedule_Choose_RecyclerViewAdapter(

    var values: ArrayList<DailySchedule>,
    var recipes: ArrayList<Recipe>,
    dailyId: ArrayList<Int>?,
    childFragmentManager: FragmentManager,
    progressbar: ProgressBar?,
    searchView: SearchView?,
    noResult: TextView?,
    context: Context
) : RecyclerView.Adapter<Daily_Schedule_Choose_RecyclerViewAdapter.ViewHolder>(), GetAndPost,
    SearchView.OnQueryTextListener {
    private var mValues: ArrayList<DailySchedule> = values
    private var recipe = recipes
    private var childFragmentManager = childFragmentManager
    private var dailyId = dailyId

    private var dailyID = -1
    private var recipeIDs = ""
    private var quantitiesStr = ""
    private var numOfMeals = ""
    private var pos = -1
    private var totalcost = 0.011

    private var page = 0
    private var isSearch = false
    private var recipeToSearch = ""
    private var isScorlled = false
    private var progressBar = progressbar
    private var searchView = searchView
    private var noResultsTextView = noResult
    private var info = false
    private var context = context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Daily_Schedule_Choose_RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_choose, parent, false)
        //  searchView!!.setOnQueryTextListener(this) // if i want to add the search in the future.

        return ViewHolder(view)
    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    fun startTask() {

        var s = AsynTaskNew(this, childFragmentManager,context)
        s.execute()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: DailySchedule = mValues[position]!! // each item postion
        holder.mItem = item

        holder.numOfDaily.setText(position.toString())

        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        if (position == getItemCount() - 1 && !isSearch) {

            page = page + 8
            isScorlled = true
            progressBar!!.visibility = View.VISIBLE
            startTask()
        }

        holder.dailyInfo.setOnClickListener {

            dailyID = mValues.get(position)!!.dailyId
            quantitiesStr = mValues.get(position)!!.quantities
            recipeIDs = mValues.get(position)!!.recipeIds
            numOfMeals = mValues.get(position)!!.numOfMeals
            pos = position + 1

            info = true
            startTask()

        }


        holder.choose.setChecked(holder.arr[position]);
        holder.choose.setOnClickListener() {


            if (holder.choose.isChecked == true && (dailyId!!.isEmpty())) {
                mValues.add((item))
                dailyId!!.add(position)
                holder.arr[position] = true

            } else if (holder.choose.isChecked == false) {
                if (dailyId!!.contains(position)) {
                    holder.arr[position] = false
                    dailyId!!.remove(position)

                }

            }


        }


    }


    fun setmValues(mValues: ArrayList<DailySchedule>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    fun setRecipeList(recipeList: ArrayList<Recipe>) {

        this.recipe = recipeList

        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)

        var dailyInfo: Button = view.findViewById(R.id.buttonInfo)
        var cart: ImageView = view.findViewById(R.id.imageViewCart)
        var numOfDaily: TextView = view.findViewById(R.id.numOfDailyTextView)
        var choose: CheckBox = view.findViewById(R.id.DailyCheckBox)
        val arr = Array(100, { i -> false })
        lateinit var mItem: DailySchedule


        override fun toString(): String {
            return super.toString()
        }


    }

    override fun getItemCount(): Int = values.size
    override fun DoNetWorkOpreation(): String {
        var link =""
        if (info) {

            var string = UserInterFace.userID.toString() + " " + dailyID

             link =
                "https://elad1.000webhostapp.com/getRecipeForDaily.php?ownerIDAndDaily=" + string
        }
//        if (isSearch) { /// if i want to add search in futrue
//            string = UserInterFace.userID.toString() + " " + recipeToSearch
//            link =
//                "https://elad1.000webhostapp.com/getSpecificSharedRecipes.php?nameAndRecipe=" + string
//
//        }
        else {
            if (!isScorlled) {
                page = 0
            }

            var string = UserInterFace.userID.toString() + " " + page

             link = "https://elad1.000webhostapp.com/getDaily.php?ownerIDAndPage=" + string

        }
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
        var start = 0
        recipe!!.clear()
        //recipe size 11
        // ingredient size 15
        if (!str.equals("")) {

            if (info) {

                val recipesAndIngredients: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

                // first recipe id

                var recipesAndIngredients2 = recipesAndIngredients[0].splitIgnoreEmpty("*")
                // first recipe id
                var currentID = recipesAndIngredients2[0].toInt()
                var currentIngId = -1
                // taking 4 Recipe ids to classifiy the ingredients to them.

                var recipeIds: ArrayList<Int> = ArrayList()


//
                var recipeIngredientMap: HashMap<Int, ArrayList<Ingredient>> = HashMap()


                var j = 0
                while (true) {

                    var recipesAndIngredients2 = recipesAndIngredients[j++].splitIgnoreEmpty("*")
                    if (recipesAndIngredients2.size != 10) {
                        break
                    }
                    start++
                    recipeIds.add(recipesAndIngredients2[0].toInt())
                }


                var ingredientList: ArrayList<Ingredient> = ArrayList()

                var quantities: HashMap<Int, ArrayList<Float>> = HashMap()

                var ids: HashMap<Int, ArrayList<Int>> = HashMap()

                // first extracting all ingredients ids and make them Ingredients.
                for (i in start..recipesAndIngredients.size - 1) {

                    var recipesAndIngredients2 = recipesAndIngredients[i].splitIgnoreEmpty("*")
                    currentIngId = recipesAndIngredients2[15].toInt()

                    //if its ingredients details
                    if (recipesAndIngredients2.size == 16 && recipeIds.contains(recipesAndIngredients2[15].toInt())) {

                        var ing = Ingredient(
                            recipesAndIngredients2[0].toInt(),
                            recipesAndIngredients2[1].toInt(),
                            recipesAndIngredients2[2],
                            ImageConvert.StringToBitMap(recipesAndIngredients2[3].toString())!!,
                            recipesAndIngredients2[4],
                            recipesAndIngredients2[5],
                            recipesAndIngredients2[6],
                            recipesAndIngredients2[7].toBoolean(),
                            recipesAndIngredients2[8].toBoolean(),
                            recipesAndIngredients2[9].toFloat(),
                            recipesAndIngredients2[10].toFloat(),
                            recipesAndIngredients2[11].toFloat(),
                            recipesAndIngredients2[12],
                            recipesAndIngredients2[13],
                            false

                        )
                        ingredientList?.add(ing)

                        if (!recipeIngredientMap.containsKey(currentIngId)) {
                            var recipeIngredients: ArrayList<Ingredient> = ArrayList()
                            var quantitiy: ArrayList<Float> = ArrayList()
                            var idss: ArrayList<Int> = ArrayList()
                            recipeIngredientMap.put(currentIngId, recipeIngredients)
                            recipeIngredientMap.get(currentIngId)!!.add(ing)
                            quantities.put(currentIngId, quantitiy)
                            quantities.get(currentIngId)!!.add(recipesAndIngredients2[14].toFloat())
                            ids.put(currentIngId, idss)
                            ids.get(currentIngId)!!.add(recipesAndIngredients2[0].toInt())

                        } else {
                            recipeIngredientMap.get(currentIngId)!!.add(ing)
                            quantities.get(currentIngId)!!.add(recipesAndIngredients2[14].toFloat())
                            ids.get(currentIngId)!!.add(recipesAndIngredients2[0].toInt())
                        }


                        //quantities.add(recipesAndIngredients2[14].toInt())
                        //ids.add(recipesAndIngredients2[0])

                    }
                }





                // currentID = -1
                var recipeIdsArr = recipeIDs.splitIgnoreEmpty(" ")
                for(i in recipeIdsArr){

                    for (j in recipesAndIngredients.indices) {


                        var recipe2 = recipesAndIngredients[j].splitIgnoreEmpty("*")
                        if (recipe2.size == 10 && i.toInt() == recipe2[0].toInt()) {
                            //var s = recipe2[0].toInt()
                            //  if (s != currentID)
                            var instructions = HowToStroreValue(recipe2[9])
                            recipe?.add(
                                Recipe(
                                    recipe2[0].toInt(),
                                    recipe2[1].toInt(),
                                    recipe2[2],
                                    ImageConvert.StringToBitMap(recipe2[3].toString())!!,
                                    recipe2[4],
                                    recipe2[5],
                                    recipe2[6].toDouble(),
                                    recipe2[7].toBoolean(),
                                    recipe2[8].toBoolean(),
                                    recipeIngredientMap.get(recipe2[0].toInt())!!,
                                    quantities.get(recipe2[0].toInt())!!,
                                    instructions
                                    // hashMap.get(recipe2[0].toInt())!!.second

                                )
                            )

                            //currentID = recipe2[0].toInt()

                        }
                    }

                }



                this!!.setRecipeList(recipe!!)

                var dialog = DailyDialogInfo(
                    recipe!!,
                    quantitiesStr,
                    numOfMeals,
                    recipeIDs,
                    pos,
                    dailyID

                )
                dialog.show(childFragmentManager, "DailyDialogInfo")

                info = false
            } else if (!info) {
                if (!isScorlled)
                    mValues!!.clear()



                quantitiesStr = ""
                numOfMeals = ""
                recipeIDs = ""


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
                        totalLists.add(quantitiesStr)
                        totalLists.add(numOfMeals)
                        totalLists.add(recipeIDs)

                        // gathering all quantities , numOfMeal and recipeIds under
                        // the key of that DailyID
                        map.put(currentDailyID.toString(), totalLists)
                        mapTotalCost.put(currentDailyID.toString(), totalcost)
                        //switching to the next DailyID
                        currentDailyID = dailyInfo2[0].toInt()

                        // clearing the variables for next DailyID
                        quantitiesStr = ""
                        numOfMeals = ""
                        recipeIDs = ""
                    }
                    quantitiesStr += "" + dailyInfo2[5] + " "
                    numOfMeals += "" + dailyInfo2[3] + " "
                    recipeIDs += "" + dailyInfo2[4] + " "
                    // saving the last total cost
                    totalcost = dailyInfo2[2].toDouble()
                }
                if (!quantitiesStr.equals("")) {
                    var totalLists: ArrayList<String> = ArrayList()
                    totalLists.add(quantitiesStr)
                    totalLists.add(numOfMeals)
                    totalLists.add(recipeIDs)
                    map.put(currentDailyID.toString(), totalLists)
                    mapTotalCost.put(currentDailyID.toString(), totalcost)
                }

                //  recipeNumbers += "" + i + " "
                // making DailyScheudle objects
                currentDailyID = -1
                for (i in dailyInfo.indices) {
                    var dailyInfo2 = dailyInfo[i].splitIgnoreEmpty("*")
                    if (dailyInfo2[0].toInt() != currentDailyID) {
                        mValues!!.add(
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
                this!!.setmValues(mValues!!)
                //  dailyChoosenRecyclerViewAdapter!!.setRecipeList(recipeList!!)
                progressBar!!.visibility = View.INVISIBLE


//            dailyList!!.clear()
//            recipeList!!.clear()


                //save it also in singleton
                //  UserPropertiesSingelton.getInstance()!!.setUserRecipess(recipeList)
                //dailyRecyclerViewAdapter!!.setmValues(recipeList!!)
                isScorlled = false

            }
        } else {
            progressBar!!.visibility = View.INVISIBLE
//            if (isSearch) { // future
//                mValues!!.clear()
//                //  noResultsTextView.visibility = View.VISIBLE
//                this!!.setmValues(mValues!!)
//            }

        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        if (p0 != "") {
//            noResultsTextView!!.visibility = View.INVISIBLE
//            isSearch = true
//            recipeToSearch = p0!!
//            startTask()
        } else {
//            isSearch = false
//            mValues!!.clear()
//            noResultsTextView!!.visibility = View.INVISIBLE
//            startTask()
        }
        return true
    }
}




