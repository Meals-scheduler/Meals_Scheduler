package com.example.meals_schdueler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class AllIngredientsFragmentTest : Fragment(), GetAndPost, NestedScrollView.OnScrollChangeListener,
    SearchView.OnQueryTextListener {

    private var columnCount = 1
    private var ingredientList: ArrayList<Ingredient>? = null
    private lateinit var nestedScroll: NestedScrollView // list of ingredietns
    private var progressBar: ProgressBar? = null
    private var AllIngredientRecyclerViewAdapter: All_IIngredients_RecyclerViewAdapter? =
        null // adapter for the list.
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: TextView

    // private lateinit var adapter : Recipe
    private var page = 0
    private var isSearch = false
    private var ingredientToSearch = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ingredientList = ArrayList()

        AllIngredientRecyclerViewAdapter =
            All_IIngredients_RecyclerViewAdapter(ingredientList!!, childFragmentManager)
        arguments?.let {
            columnCount = it.getInt(AllRecipesFragmentTest.ARG_COLUMN_COUNT)
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_allingredients1_list, container, false)
        val recyclerView = view.findViewById<View>(R.id.recycler_view) as RecyclerView
        nestedScroll = view.findViewById(R.id.scroll_view)
        progressBar = view.findViewById(R.id.progress_bar)
        nestedScroll.setOnScrollChangeListener(this)
        searchView = view.findViewById(R.id.search_bar)
        searchView.setOnQueryTextListener(this)
        noResultsTextView = view.findViewById(R.id.tv_emptyTextView)


        // Set the adapter
        val context = view.context
        // to avoid constant loading of AllIngredients Data , we want to load only once. - to ask Harel

        instance = this




        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = AllIngredientRecyclerViewAdapter

        startTask()



        return view
    }

    fun startTask() {
        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }

    companion object {


        var instance: AllIngredientsFragmentTest? = null


        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            AllIngredientsFragmentTest().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }


    override fun DoNetWorkOpreation(): String {
        var string = UserInterFace.userID.toString() + " " + page

        var link =
            "https://elad1.000webhostapp.com/getSharedIngredients.php?ownerIDAndPage=" + string
        if (isSearch) {
            Log.v("Elad1", "search")
            string = UserInterFace.userID.toString() + " " + ingredientToSearch
            link =
                "https://elad1.000webhostapp.com/getSpecificSharedIngredients.php?nameAndIngredient=" + string
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

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun getData(str: String) {

        // ingredientList!!.clear()
        // fixed a default .split spaces , and fixed spaces in howToStore.
        // when we add an ingredient it doesnt update in real time. we have to re compile!!!
        if (!str.equals("")) {
            if (isSearch) {
                ingredientList!!.clear()

            }


            val ingredients: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

            for (i in ingredients.indices) {
                Log.v("Elad1", ingredients.indices.toString())
                var ingredient2 = ingredients[i].splitIgnoreEmpty("*")
                ingredientList?.add(
                    Ingredient(
                        ingredient2[0].toInt(),
                        ingredient2[1].toInt(),
                        ingredient2[2],
                        ImageConvert.StringToBitMap(ingredient2[3].toString())!!,
                        ingredient2[4],
                        ingredient2[5],
                        ingredient2[6],
                        ingredient2[7].toBoolean(),
                        ingredient2[8].toBoolean(),
                        ingredient2[9].toFloat(),
                        ingredient2[10].toFloat(),
                        ingredient2[11].toFloat(),
                        ingredient2[12],
                        ingredient2[13],
                        false

                    )
                )
            }


            AllIngredientRecyclerViewAdapter!!.setmValues(ingredientList!!)
            progressBar!!.visibility = View.INVISIBLE
        } else {
            if (isSearch) {
                ingredientList!!.clear()
                noResultsTextView.visibility = View.VISIBLE
                AllIngredientRecyclerViewAdapter!!.setmValues(ingredientList!!)
            }
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
            page = page + 4
            progressBar!!.visibility = View.VISIBLE
            startTask()
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        if (p0 != "") {
            Log.v("Elad1", "wanna search")
            noResultsTextView.visibility = View.INVISIBLE
            isSearch = true
            ingredientToSearch = p0!!
            startTask()
        } else {
            isSearch = false
            ingredientList!!.clear()
            noResultsTextView.visibility = View.INVISIBLE
            startTask()
        }

        return true
    }
}