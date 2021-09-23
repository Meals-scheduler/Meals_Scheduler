package com.example.meals_schdueler

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import java.util.*
import kotlin.collections.ArrayList


/**
 * A fragment representing a list of Items.
 */
class MyingredientFragment1 : Fragment(), GetAndPost,NestedScrollView.OnScrollChangeListener,
    SearchView.OnQueryTextListener {

    private var columnCount = 1
    private var ingredientList: ArrayList<Ingredient>? = null // list of ingredietns
    private var ingredientRecyclerViewAdapter: MyItemRecyclerViewAdapter? =
        null // adapter for the list.
    private lateinit var nestedScroll: NestedScrollView // list of ingredietns
    private var progressBar: ProgressBar? = null
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: TextView
    private var isScorlled = false

    private var page = 0
    private var isSearch = false
    private var ingredientToSearch = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ingredientList = ArrayList()
        ingredientRecyclerViewAdapter =
            MyItemRecyclerViewAdapter(ingredientList!!, childFragmentManager)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    fun getRecycler() :MyItemRecyclerViewAdapter{
        return ingredientRecyclerViewAdapter!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myingredient1_list, container, false)
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
//                recyclerView.adapter = ingredientRecyclerViewAdapter
//
//            }
//        }
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = ingredientRecyclerViewAdapter
        startTask()
//        var s = AsynTaskNew(this)
//        s.execute()
        return view
    }

    companion object {

        var instance: MyingredientFragment1? = null

        fun getInstance1(): MyingredientFragment1 {
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

    /// if WEBHOST doesnt work - this query doesnt excuted and the program falls - need to be fixed!!!!!!
    override fun DoNetWorkOpreation(): String {
        if(!isScorlled){
            page =0
        }
        var string = UserInterFace.userID.toString() + " " + page


        var link =
            "https://elad1.000webhostapp.com/getIngredient.php?ownerIDAndPage=" + string


        if (isSearch) {
            Log.v("Elad1", "search")
            string = UserInterFace.userID.toString() + " " + ingredientToSearch
            link =
                "https://elad1.000webhostapp.com/getSpecificMyIngredients.php?nameAndIngredient=" + string
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

        // fixed a default .split spaces , and fixed spaces in howToStore.
        // when we add an ingredient it doesnt update in real time. we have to re compile!!!
        if (!str.equals("")) {
            if (!isScorlled)
                ingredientList!!.clear()

            if (isSearch) {
                ingredientList!!.clear()

            }
            val ingredients: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

            for (i in ingredients.indices) {

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


            // initializing the singelton with the user's ingredients list to keep it here on code.
            // should do it on another place !!!
            //  UserPropertiesSingelton.getInstance()!!.setUserIngredientss(sorted)
            // sending the last to the adapter.
            ingredientRecyclerViewAdapter!!.setmValues(ingredientList!!)
            progressBar!!.visibility = View.INVISIBLE
            isScorlled = false

        }
        else {
            if (isSearch) {
                ingredientList!!.clear()
                noResultsTextView.visibility = View.VISIBLE
                ingredientRecyclerViewAdapter!!.setmValues(ingredientList!!)
            }
        }
        isScorlled = false
        progressBar!!.visibility = View.INVISIBLE
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
            page = page + 8
            progressBar!!.visibility = View.VISIBLE
            isScorlled = true
            startTask()
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return  false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        if (p0 != "") {
            Log.v("Elad1", "wanna search")
            noResultsTextView.visibility = View.INVISIBLE
            isSearch = true
            ingredientToSearch = p0!!
            startTask()
        } else {
            page = 0
            isSearch = false
            ingredientList!!.clear()
            noResultsTextView.visibility = View.INVISIBLE
            startTask()
        }

        return true
    }


}