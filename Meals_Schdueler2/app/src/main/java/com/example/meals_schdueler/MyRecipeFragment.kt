package com.example.meals_schdueler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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


class MyRecipeFragment : Fragment(), GetAndPost {

    private var columnCount = 1
    private var recipeList: ArrayList<Recipe>? = null // list of ingredietns
    private var recipeRecyclerViewAdapter: MyRecipeRecyclerViewAdapter? =
        null // adapter for the list.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeList = ArrayList<Recipe>()
        recipeRecyclerViewAdapter = MyRecipeRecyclerViewAdapter(recipeList!!, childFragmentManager)
        arguments?.let {
            columnCount = it.getInt(MyingredientFragment1.ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myrecipe_list, container, false)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView


        val context = view.context
        instance = this

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                recyclerView.adapter = recipeRecyclerViewAdapter

            }
        }

        startTask()
//        var s = AsynTaskNew(this)
//        s.execute()
        return view
    }

    companion object {

        var instance: MyRecipeFragment? = null

        fun getInstance1(): MyRecipeFragment {
            return instance!!
        }

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            MyingredientFragment1().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)

                }
            }
    }


    override fun DoNetWorkOpreation(): String {


        var link = "https://elad1.000webhostapp.com/getRecipe.php?ownerID=" + UserInterFace.userID;

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

    // to avoid empty string cells .split function returns.
    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun getData(str: String) {

        recipeList!!.clear()
        val recipes: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

        var currentID = recipes[0].get(0).toInt() - 48

        // saving all the ingredietns and quantities of each Recipe in map.
        // first we extract the ids and quantity into map.
        // then we use another map to convert all ids to real ingredients.

        var hashMap: HashMap<Int, Pair<ArrayList<String>, ArrayList<String>>> =
            HashMap()

        var map: HashMap<Int, ArrayList<Ingredient>> = HashMap()

        var ingredientList: ArrayList<Ingredient> = ArrayList()

        var originalIngredientsList =
            UserPropertiesSingelton.getInstance()!!.getUserIngredientss()!!

        var fromListToMap: HashMap<String, Ingredient> = HashMap()

        for (i in originalIngredientsList) {
            fromListToMap.put(i.ingredientID.toString(), i)
        }

        var ids: ArrayList<String> = ArrayList()
        var quantities: ArrayList<String> = ArrayList()
        for (i in recipes.indices) {
            var recipe2 = recipes[i].splitIgnoreEmpty(",")
            if (recipe2[0].get(0).toInt() - 48 != currentID) {

                // copying the lists of the ids and aquantities
                var tmpIds = deepCopy(ids)
                var tmpQuantities = deepCopy(quantities)
                // put it into the map with the Key of the RecipeID
                hashMap.put(currentID, Pair(tmpIds, tmpQuantities))

                // copy the ingredietns lists to map with key of RecipieID
               var tmplist = deepCopyIng(ingredientList)
                map.put(currentID, tmplist)

                // switching to next recipe id
                currentID = recipe2[0].get(0).toInt() - 48

                // clear all lists for the next recipe lists
                ingredientList.clear()
                ids.clear()
                quantities.clear()
            }

            ids.add(recipe2[9])
            quantities.add(recipe2[10])
            // getting the specific ingredient by its ID
            ingredientList.add(fromListToMap.get(recipe2[9])!!)
        }
        hashMap.put(currentID, Pair(ids, quantities))
        map.put(currentID, ingredientList)

//
//        for (i in map) {
//            Log.v("Elad1", i.key.toString())
//            Log.v("Elad1", i.value.get(0).ingredientID.toString())
//            Log.v("Elad1", i.value.get(2).ingridentName)
//        }


        // now converting the id's list to Ingredient list.
        currentID = -1
        for (i in recipes.indices) {
            Log.v("Elad1", recipes.indices.toString())
            var recipe2 = recipes[i].splitIgnoreEmpty(",")
            var s = recipe2[0].get(0).toInt() - 48
            if (s!= currentID)
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
                        convertToInt(hashMap.get(recipe2[0].toInt())!!.second)

                    )
                )

            currentID = recipe2[0].get(0).toInt() - 48

        }

        recipeRecyclerViewAdapter!!.setmValues(recipeList!!)


    }

    fun convertToInt(quantities: ArrayList<String>): ArrayList<Int> {
        var quantity = ArrayList<Int>()
        for (i in quantities)
            quantity.add(i.toInt())

        return quantity
    }

    private fun deepCopyIng(ingredientList: ArrayList<Ingredient>): ArrayList<Ingredient> {
        var s: ArrayList<Ingredient> = ArrayList()
        for (i in ingredientList) {
            s.add(i)
        }
        return s
    }


    fun deepCopy(arrToCopy: ArrayList<String>): ArrayList<String> {
        var s: ArrayList<String> = ArrayList()

        for (i in arrToCopy) {
            s.add(i)
        }
        return s
    }

    fun startTask() {

        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }
}