package com.example.meals_schdueler

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class Recipe_Ingredients_Choose_Dialog(
    recipeIngredients: Recipe_Ingredients_List,
    userIngredientss: ArrayList<Ingredient>,
    costList: ArrayList<Float>
) :
    DialogFragment(), NestedScrollView.OnScrollChangeListener, SearchView.OnQueryTextListener,
    GetAndPost {

    lateinit var description: EditText
    lateinit var btnDone: Button
    lateinit var exit: ImageView

    private lateinit var nestedScroll: NestedScrollView // list of ingredietns
    private var progressBar: ProgressBar? = null
    private lateinit var searchView: SearchView
    private lateinit var noResultsTextView: TextView

    private var page = 0
    private var isSearch = false
    private var ingredientToSearch = ""
    private var isScorlled = false


    var l: Recipe_Ingredients_List = recipeIngredients

    // private var isFirstTime = isFirstTime
    private var ingredientList: ArrayList<Ingredient>? = userIngredientss
    private var costList: ArrayList<Float> = costList
    private var ingredietnsChoosenRecyclerViewAdapter: Recipe_Ingredients_Choose_RecyclerViewAdapter? =
        null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        ingredietnsChoosenRecyclerViewAdapter = Recipe_Ingredients_Choose_RecyclerViewAdapter(
            ingredientList!!, l.list!!, childFragmentManager, costList, requireContext()
        )


        var view: View =
            inflater.inflate(R.layout.recipe_ingredients_choose_dialog, container, false)
        btnDone = view.findViewById(R.id.doneBtn)
        val recyclerView = view.findViewById<View>(R.id.list) as RecyclerView

        ingredietnsChoosenRecyclerViewAdapter!!.setmValues(ingredientList!!)
        exit = view.findViewById(R.id.imageViewX)
//        cost = rootView.findViewById(R.id.editTextCost)
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
        recyclerView.adapter = ingredietnsChoosenRecyclerViewAdapter
        startTask()
        return view

    }

    // trigger the onDissmiss in AddRecipeFramgent to tell it that this dialog is dissmissed.
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
                isScorlled = true
                progressBar!!.visibility = View.VISIBLE
                startTask()
            }
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    fun startTask() {

        var s = AsynTaskNew(this, childFragmentManager)
        s.execute()
    }


    override fun onQueryTextChange(p0: String?): Boolean {
        if (p0 != "") {
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

    override fun DoNetWorkOpreation(): String {
        if (!isScorlled) {
            page = 0
        }
        var string = UserInterFace.userID.toString() + " " + page

        var link =
            "https://elad1.000webhostapp.com/getSharedIngredients.php?ownerIDAndPage=" + string
        if (isSearch) {
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
//            if (!isScorlled)
//                ingredientList!!.clear()

            if (isSearch) {
                ingredientList!!.clear()

            }
            val ingredients: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

            for (i in ingredients.indices) {

                var ingredient2 = ingredients[i].splitIgnoreEmpty("*")


                var ing = Ingredient(
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
                if (!ingredientList!!.contains(ing)) {
                    ingredientList?.add(ing)
                }


            }


            // initializing the singelton with the user's ingredients list to keep it here on code.
            // should do it on another place !!!
            //  UserPropertiesSingelton.getInstance()!!.setUserIngredientss(sorted)
            // sending the last to the adapter.
            ingredietnsChoosenRecyclerViewAdapter!!.setmValues(ingredientList!!)
            progressBar!!.visibility = View.INVISIBLE
            isScorlled = false

        } else {
            if (isSearch) {
                ingredientList!!.clear()
                noResultsTextView.visibility = View.VISIBLE
                ingredietnsChoosenRecyclerViewAdapter!!.setmValues(ingredientList!!)
            }
        }
        isScorlled = false
        progressBar!!.visibility = View.INVISIBLE
    }

}