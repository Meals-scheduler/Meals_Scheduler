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
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class AllRecipesFragment : Fragment(), GetAndPost {
    private var columnCount = 1
    private var recipeList: ArrayList<Recipe>? = null // list of ingredietns
    private var AllRecipeRecyclerViewAdapter: All_Recipes_RecyclerViewAdapter? =
        null // adapter for the list.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeList = ArrayList<Recipe>()
        AllRecipeRecyclerViewAdapter =
            All_Recipes_RecyclerViewAdapter(recipeList!!, childFragmentManager)

        arguments?.let {
            columnCount = it.getInt(AllingredientsFragment1.ARG_COLUMN_COUNT)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_recipes_list, container, false)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView

        // Set the adapter
        val context = view.context
        // to avoid constant loading of AllIngredients Data , we want to load only once. - to ask Harel
        if (instance == null) {
           // startTask()

        } else {

        }
        instance = this

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                recyclerView.adapter = AllRecipeRecyclerViewAdapter
            }
        }


         startTask()
        return view
    }

    companion object {


        var instance: AllRecipesFragment? = null


        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            AllingredientsFragment1().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }


    override fun DoNetWorkOpreation(): String {
        val link =
            "https://elad1.000webhostapp.com/getSharedRecipes.php?ownerID=" + UserInterFace.userID;


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


        //  Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()
    }


    // to avoid empty string cells .split function returns.
    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun getData(str: String) {

        //recipe size 11
        // ingredient size 15
        if (!str.equals("")) {
            recipeList!!.clear()
            val recipesAndIngredients: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

            // first recipe id

            var recipesAndIngredients2 = recipesAndIngredients[0].splitIgnoreEmpty("*")
            // first recipe id
            var currentID = recipesAndIngredients2[0].toInt()


            // saving all the ingredietns and quantities of each Recipe in map.
            // first we extract the ids and quantity into map.
            // then we use another map to convert all ids to real ingredients.

            var hashMap: HashMap<Int, Pair<ArrayList<String>, ArrayList<Float>>> =
                HashMap()

            var map: HashMap<Int, ArrayList<Ingredient>> = HashMap()

            var ingredientList: ArrayList<Ingredient> = ArrayList()

            // first extracting all ingredients ids and make them Ingredients.
            for (i in recipesAndIngredients.indices) {

                var recipesAndIngredients2 = recipesAndIngredients[i].splitIgnoreEmpty("*")
                //if its ingredients details
                if (recipesAndIngredients2.size == 15) {
                    ingredientList?.add(
                        Ingredient(
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
                    )

                }
            }

            var fromListToMap: HashMap<String, Ingredient> = HashMap()

            for (i in ingredientList) {
                fromListToMap.put(i.ingredientID.toString(), i)
            }
            var quantities: ArrayList<Float> = ArrayList()
            var ids: ArrayList<String> = ArrayList()
            var ingredientList2: ArrayList<Ingredient> = ArrayList()
            //  var quantities: ArrayList<String> = ArrayList()
            for (i in recipesAndIngredients.indices) {
                var recipesAndIngredients2 = recipesAndIngredients[i].splitIgnoreEmpty("*")
              //  var id = recipesAndIngredients2[0].toInt()
                if (recipesAndIngredients2.size == 11) {
                    if (recipesAndIngredients2[0].toInt()  != currentID) {

                        // copying the lists of the ids and aquantities
                        // var tmpIds = deepCopy(ids)
                        //  var tmpQuantities = deepCopy(quantities)
                        // put it into the map with the Key of the RecipeID
                        hashMap.put(currentID, Pair(ids, quantities))
                        quantities = ArrayList()
                        ids = ArrayList()

                        // copy the ingredietns lists to map with key of RecipieID
                        //var tmplist = deepCopyIng(ingredientList2)
                        map.put(currentID, ingredientList2)
                        ingredientList2 = ArrayList()
                        // switching to next recipe id
                        currentID = recipesAndIngredients2[0].toInt()

                        // clear all lists for the next recipe lists
//                        ingredientList2.clear()
//                        ids.clear()
//                        quantities.clear()
                    }

                    ids.add(recipesAndIngredients2[9])
                    quantities.add(recipesAndIngredients2[10].toFloat())
                    // getting the specific ingredient by its ID


                    /// doesnt work cause its not mine!!!!!!
                    ingredientList2.add(
                        fromListToMap.get(recipesAndIngredients2[9])!!
                    )


                } else {
                    break
                }
            }
            hashMap.put(currentID, Pair(ids, quantities))
            map.put(currentID, ingredientList2)


            currentID = -1
            for (i in recipesAndIngredients.indices) {

                var recipe2 = recipesAndIngredients[i].splitIgnoreEmpty("*")
                if (recipe2.size == 11) {
                    var s = recipe2[0].toInt()
                    if (s != currentID)
                        recipeList?.add(
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
                                map.get(recipe2[0].toInt())!!,
                                hashMap.get(recipe2[0].toFloat())!!.second

                            )
                        )

                    currentID = recipe2[0].toInt()

                }
            }

            AllRecipeRecyclerViewAdapter!!.setmValues(recipeList!!)
        }

    }

//    fun convertToInt(quantities: ArrayList<String>): ArrayList<Int> {
//        var quantity = ArrayList<Int>()
//        for (i in quantities)
//            quantity.add(i.toInt())
//
//        return quantity
//    }
//
//    private fun deepCopyIng(ingredientList: ArrayList<Ingredient>): ArrayList<Ingredient> {
//        var s: ArrayList<Ingredient> = ArrayList()
//        for (i in ingredientList) {
//            s.add(i)
//        }
//        return s
//    }
//
//
//    fun deepCopy(arrToCopy: ArrayList<String>): ArrayList<String> {
//        var s: ArrayList<String> = ArrayList()
//
//        for (i in arrToCopy) {
//            s.add(i)
//        }
//        return s
//    }

    fun startTask() {
        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }
}