package com.example.meals_schdueler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
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


class MyRecipeFragment : Fragment(), GetAndPost, NestedScrollView.OnScrollChangeListener,
    SearchView.OnQueryTextListener {

    private var columnCount = 1
    private var recipeList: ArrayList<Recipe>? = null // list of ingredietns
    private var recipeRecyclerViewAdapter: MyRecipeRecyclerViewAdapter? =
        null // adapter for the list.
    private lateinit var nestedScroll: NestedScrollView // list of ingredietns
    private var progressBar: ProgressBar? = null
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: TextView
    private var isScorlled = false
    private var page = 0
    private var isSearch = false
    private var recipeToSearch = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeList = ArrayList()
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
        val recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView
        nestedScroll = view.findViewById(R.id.scroll_view)
        progressBar = view.findViewById(R.id.progress_bar)
        nestedScroll.setOnScrollChangeListener(this)
        searchView = view.findViewById(R.id.search_bar)
        searchView.setOnQueryTextListener(this)
        noResultsTextView = view.findViewById(R.id.tv_emptyTextView)


        val context = view.context
        instance = this

        // Set the adapter
//        if (view is RecyclerView) {
//            with(view) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
//
//
//            }
//        }

        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = recipeRecyclerViewAdapter

        startTask()

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
            MyRecipeFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)

                }
            }
    }


    override fun DoNetWorkOpreation(): String {
        if(!isScorlled){
            page =0
        }
        var string = UserInterFace.userID.toString() + " " + page

        var link = "https://elad1.000webhostapp.com/getRecipe.php?ownerIDAndPage=" + string
        if (isSearch) {
            string = UserInterFace.userID.toString() + " " + recipeToSearch
            link =
                "https://elad1.000webhostapp.com/getSpecificMyRecipes.php?nameAndRecipe=" + string
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


        //Log.v("Elad1", "Id came is" + sb.toString())
        return sb.toString()
    }

    // to avoid empty string cells .split function returns.
    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun getData(str: String) {
        var start = 0
        //recipe size 11
        // ingredient size 15
        if (!str.equals("")) {
            if (!isScorlled)
                recipeList!!.clear()

            //  recipeList!!.clear()
            val recipesAndIngredients: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()
            if (isSearch) {
                recipeList!!.clear()

            }
            // first recipe id

            var recipesAndIngredients2 = recipesAndIngredients[0].splitIgnoreEmpty("*")
            // first recipe id
            var currentID = recipesAndIngredients2[0].toInt()
            var currentIngId = -1
            // taking 4 Recipe ids to classifiy the ingredients to them.

            var recipeIds: ArrayList<Int> = ArrayList()


//
            var recipeIngredientMap: HashMap<Int, ArrayList<Ingredient>> = HashMap()

            if (isSearch) {

                for (j in recipesAndIngredients.indices) {
                    var recipesAndIngredients2 = recipesAndIngredients[j].splitIgnoreEmpty("*")
                    if (recipesAndIngredients2.size == 9) {
                        recipeIds.add(recipesAndIngredients2[0].toInt())
                        start++
                    } else {

                        break
                    }

                }
            } else {
                var j = 0
                while (true) {

                    var recipesAndIngredients2 = recipesAndIngredients[j++].splitIgnoreEmpty("*")
                    if (recipesAndIngredients2.size != 9) {
                        break
                    }
                    start++
                    recipeIds.add(recipesAndIngredients2[0].toInt())
                }
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
                        var quantitiy: ArrayList<Int> = ArrayList()
                        var idss: ArrayList<Int> = ArrayList()
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
                                recipeIngredientMap.get(recipe2[0].toInt())!!,
                                quantities.get(s)!!
                                // hashMap.get(recipe2[0].toInt())!!.second

                            )
                        )

                    currentID = recipe2[0].toInt()

                } else {
                    break
                }
            }

            recipeRecyclerViewAdapter!!.setmValues(recipeList!!)
            progressBar!!.visibility = View.INVISIBLE
            isScorlled = false
        } else {
            if (isSearch) {
                recipeList!!.clear()
                noResultsTextView.visibility = View.VISIBLE
                recipeRecyclerViewAdapter!!.setmValues(recipeList!!)
            }
        }
        progressBar!!.visibility = View.INVISIBLE
        isScorlled= false


    }


    fun startTask() {

        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
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

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        if (p0 != "") {
            noResultsTextView.visibility = View.INVISIBLE
            isSearch = true
            recipeToSearch = p0!!
            startTask()
        } else {
            isSearch = false
            recipeList!!.clear()
            noResultsTextView.visibility = View.INVISIBLE
            startTask()
        }
        return true
    }
}