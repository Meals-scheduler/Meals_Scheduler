package com.example.meals_schdueler

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

/**
 * A fragment representing a list of Items.
 */
class AllingredientsFragment1 : Fragment(), GetAndPost {

    private var columnCount = 1
    private var ingredientList: ArrayList<Ingredient>? = null // list of ingredietns
    private var AllIngredientRecyclerViewAdapter: All_IIngredients_RecyclerViewAdapter? =
        null // adapter for the list.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ingredientList = ArrayList<Ingredient>()
        AllIngredientRecyclerViewAdapter =
            All_IIngredients_RecyclerViewAdapter(ingredientList!!, childFragmentManager)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_allingredients1_list, container, false)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView

        // Set the adapter
        // Set the adapter
        val context = view.context
        if (instance == null){
            startTask()
            Log.v("Elad1","LOADED")
        }
        else{
            Log.v("Elad1"," NOT LOADED")
        }
        instance = this

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                recyclerView.adapter = AllIngredientRecyclerViewAdapter
            }
        }



        return view
    }

    companion object {


        var instance: AllingredientsFragment1? = null
//
//        fun getInstance1(): AllingredientsFragment1 {
//            return instance!!
//        }

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
            "https://elad1.000webhostapp.com/getSharedIngredients.php?ownerID=" + UserInterFace.userID;
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
        ingredientList!!.clear()
        // fixed a default .split spaces , and fixed spaces in howToStore.
        // when we add an ingredient it doesnt update in real time. we have to re compile!!!

        val ingredients: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

        for (i in ingredients.indices) {
            Log.v("Elad1", ingredients.indices.toString())
            var ingredient2 = ingredients[i].splitIgnoreEmpty(",")
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
    }

    fun startTask() {
        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }
}