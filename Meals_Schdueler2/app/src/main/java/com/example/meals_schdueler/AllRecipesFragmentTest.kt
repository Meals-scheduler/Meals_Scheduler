package com.example.meals_schdueler


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class AllRecipesFragmentTest : Fragment(), GetAndPost, NestedScrollView.OnScrollChangeListener {
    //var builder: java.lang.StringBuilder? = null
    private var columnCount = 1
    private var recipeList: ArrayList<Recipe>? = null
    private lateinit var nestedScroll: NestedScrollView // list of ingredietns
    private var progressBar: ProgressBar? = null
    private var AllRecipeRecyclerViewAdapter: All_Recipes_RecyclerViewAdapter? =
        null // adapter for the list.

    // private lateinit var adapter : Recipe
    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeList = ArrayList()

        AllRecipeRecyclerViewAdapter =
            All_Recipes_RecyclerViewAdapter(recipeList!!, childFragmentManager)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_recipes_list, container, false)
        val recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView
        nestedScroll = view.findViewById(R.id.scroll_view)
        progressBar = view.findViewById(R.id.progress_bar)
        nestedScroll.setOnScrollChangeListener(this)


        // Set the adapter
        val context = view.context
        // to avoid constant loading of AllIngredients Data , we want to load only once. - to ask Harel

        instance = this




        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = AllRecipeRecyclerViewAdapter

        startTask()



        return view
    }


    fun startTask() {
        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }

    companion object {


        var instance: AllRecipesFragmentTest? = null


        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            AllRecipesFragmentTest().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }


    override fun DoNetWorkOpreation(): String {
        var string = UserInterFace.userID.toString() + " " + page

        var link =
            "https://elad1.000webhostapp.com/getSharedRecipes.php?ownerIDAndPage=" + string


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


        //Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()

    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun getData(str: String) {

        //recipe size 11
        // ingredient size 15
        if (!str.equals("")) {
            //  recipeList!!.clear()
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


            for (j in 0..3) {
                var recipesAndIngredients2 = recipesAndIngredients[j].splitIgnoreEmpty("*")
                recipeIds.add(recipesAndIngredients2[0].toInt())
            }

            //var isFirst = true

            // saving all the ingredietns and quantities of each Recipe in map.
            // first we extract the ids and quantity into map.
            // then we use another map to convert all ids to real ingredients.

//            var hashMap: HashMap<Int, Pair<ArrayList<String>, ArrayList<Int>>> =
//                HashMap()

            var map: HashMap<Int, ArrayList<Ingredient>> = HashMap()

//            var ingredientList: ArrayList<Ingredient> = ArrayList()
//
//            var quantities: ArrayList<Int> = ArrayList()

            var ingredientList: ArrayList<Ingredient> = ArrayList()

            var quantities: HashMap<Int, ArrayList<Int>> = HashMap()

            var ids: HashMap<Int, ArrayList<Int>> = HashMap()

            // first extracting all ingredients ids and make them Ingredients.
            for (i in 4..recipesAndIngredients.size - 1) {

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
                        var quantitiy :ArrayList<Int> = ArrayList()
                        var idss :ArrayList<Int> = ArrayList()
                        recipeIngredientMap.put(currentIngId, recipeIngredients)
                        recipeIngredientMap.get(currentIngId)!!.add(ing)
                        quantities.put(currentIngId, quantitiy)
                        quantities.get(currentIngId)!!.add(recipesAndIngredients2[14].toInt())
                        ids.put(currentIngId, idss)
                        ids.get(currentIngId)!!.add(recipesAndIngredients2[0].toInt())

                    } else {
                        recipeIngredientMap.get(currentIngId)!!.add(ing)
                        quantities.get(currentIngId)!!.add(recipesAndIngredients2[14].toInt())
                        ids.get(currentIngId)!!.add(recipesAndIngredients2[0].toInt())
                    }


                    //quantities.add(recipesAndIngredients2[14].toInt())
                    //ids.add(recipesAndIngredients2[0])

                }
            }

//            var fromListToMap: HashMap<String, Ingredient> = HashMap()
//
//            for (i in ingredientList) {
//                fromListToMap.put(i.ingredientID.toString(), i)
//            }


            var quantities2: ArrayList<Int> = ArrayList()
            var ids2: ArrayList<Int> = ArrayList()
            var j = 0
            var ingredientList2: ArrayList<Ingredient> = ArrayList()
            //  var quantities: ArrayList<String> = ArrayList()
            for (i in recipesAndIngredients.indices) {
                var recipesAndIngredients2 = recipesAndIngredients[i].splitIgnoreEmpty("*")
                //  var id = recipesAndIngredients2[0].toInt()
                if (recipesAndIngredients2.size == 9) {
                    if (recipesAndIngredients2[0].toInt() != currentID) {

                        // copying the lists of the ids and aquantities
                        // var tmpIds = deepCopy(ids)
                        //  var tmpQuantities = deepCopy(quantities)
                        // put it into the map with the Key of the RecipeID
                        //hashMap.put(currentID, Pair(ids2, quantities2))
                       // quantities2 = ArrayList()
                      //  ids2 = ArrayList()

                        // copy the ingredietns lists to map with key of RecipieID
                        //var tmplist = deepCopyIng(ingredientList2)
                        map.put(currentID, ingredientList2)
                        //  ingredientList2 = ArrayList()
                        // switching to next recipe id
                        currentID = recipesAndIngredients2[0].toInt()

                        // clear all lists for the next recipe lists
//                        ingredientList2.clear()
//                        ids.clear()
//                        quantities.clear()
                    }

                    // ids2.add(ids.get(j))
                  //  ids2= ids.get(currentID)!!
                  //  quantities2 = quantities.get(currentID)!!
                    //   quantities2.add(quantities.get(j))
                    // getting the specific ingredient by its ID


                    /// doesnt work cause its not mine!!!!!!
                    // ingredientList2.add(fromListToMap.get(ids.get(j++)!!)!!)
                    ingredientList2 = recipeIngredientMap.get(currentID)!!


                } else {
                    break
                }
            }
           // hashMap.put(currentID, Pair(ids, quantities))
            map.put(currentID, ingredientList2)


            currentID = -1
            for (i in recipesAndIngredients.indices) {

                var recipe2 = recipesAndIngredients[i].splitIgnoreEmpty("*")
                if (recipe2.size == 9) {
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
                                quantities.get(s)!!
                               // hashMap.get(recipe2[0].toInt())!!.second

                            )
                        )

                    currentID = recipe2[0].toInt()

                }
            }

            AllRecipeRecyclerViewAdapter!!.setmValues(recipeList!!)
            progressBar!!.visibility = View.INVISIBLE
        }

    }


    override fun onScrollChange(
        v: NestedScrollView?,
        scrollX: Int,
        scrollY: Int,
        oldScrollX: Int,
        oldScrollY: Int
    ) {
        Log.v("Elad1", "NESTEDDDDDDDD")
        if (scrollY == v!!.getChildAt(0).measuredHeight - v.measuredHeight) {
            page = page + 4
            progressBar!!.visibility = View.VISIBLE
            startTask()
        }
    }


}

