package com.example.meals_schdueler


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DummyContent.DummyItem
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class Recipe_Ingredients_Choose_RecyclerViewAdapter(
    private var values: ArrayList<Ingredient>,
    private var intValues: ArrayList<Int>,
    childFragmentManager: FragmentManager,
    costList: ArrayList<Float>,
    context: Context,
    private var recyclerView: RecyclerView,
    progressbar : ProgressBar?,
    searchView: SearchView?,
    noResult : TextView?
) : RecyclerView.Adapter<Recipe_Ingredients_Choose_RecyclerViewAdapter.ViewHolder>() ,GetAndPost, SearchView.OnQueryTextListener {

    private var mValues: ArrayList<Ingredient> = values
    private var mIntValues: ArrayList<Int> = intValues
    private var childFragmentManager = childFragmentManager
    private var costList: ArrayList<Float> = costList
    private var recyclerview = recyclerView
    private var page = 0
    private var isSearch = false
    private var ingredientToSearch = ""
    private var isScorlled = false
    private var progressBar = progressbar
    private var searchView = searchView
    private var noResultsTextView = noResult



    //    private var arr: ArrayList<Boolean> = ArrayList<Boolean>(100)
//    private var arr2: ArrayList<String> = ArrayList<String>(100)
    private var context = context
    private var posTmp = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_ingredients_choose, parent, false)
        searchView!!.setOnQueryTextListener(this)

        return ViewHolder(view)
    }
        fun startTask() {

        var s = AsynTaskNew(this, childFragmentManager,context)
        s.execute()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: Ingredient = mValues[position] // each item postion
        holder.mItem = item
        holder.ingredientName.setText(item.ingridentName)
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        holder.ingredientImage.setImageBitmap(item.pictureBitMap)
        holder.ingredientInfo.setOnClickListener {
            var dialog = MyIngredientInfo(item, true)
            dialog.show(childFragmentManager, "IngredientInfo")

        }

        if( position == getItemCount() - 1 && !isSearch){
            page = page + 8
            isScorlled = true
            progressBar!!.visibility = View.VISIBLE
            startTask()
        }


        holder.choose.setChecked(holder.arr[position]);
        holder.cost.setText(holder.arr2.get(position))
        //  holder.choose.setChecked(holder.arr[position]);
        // holder.cost.setText(holder.arr2[position])
        holder.choose.setOnClickListener() {


            if (holder.choose.isChecked == true && !(mIntValues.contains(position))) {
                mValues.add(item)
                mIntValues.add(position)
                holder.arr[position] = true
                posTmp = position


                if ((holder.cost.getText().toString().trim().length > 0)) {
                    costList.add(holder.cost.text.toString().toFloat())
                    holder.arr2[position] = holder.cost.text.toString()
                } else {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

                    builder.setTitle("Adding Ingredient")
                    builder.setMessage("You must choose amount!!.")

                    builder.setPositiveButton(
                        "OK",
                        DialogInterface.OnClickListener { dialog, which -> // Do nothing but close the dialog

                            dialog.dismiss()
                        })
                    val alert: AlertDialog = builder.create()
                    alert.show()
                    holder.arr[posTmp] = false
                    mIntValues.remove(posTmp)
                    //mValues.remove(item)
                    holder.choose.isChecked = false
                }

            } else if (holder.choose.isChecked == false) {
                if (mIntValues.contains(position)) {
                    holder.arr[position] = false
                    mIntValues.remove(position)
                }

            }


        }


    }


    fun setmValues(mValues: ArrayList<Ingredient>) {
        this.mValues = mValues
        //  Log.v("Elad1","List size " + mValues.size)
//        if (mValues.size > arr.size) {
//            for (i in 0..(mValues.size-arr.size)-1) {
//                arr.add(false)
//                arr2.add("")
//            }
//        }

//        Log.v("Elad1","arr size" + arr.size)
//        Log.v("Elad1","List sie" + mValues.size.toString())
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)
        var ingredientImage: ImageView = view.findViewById(R.id.imageViewPicIngr)
        var ingredientName: Button = view.findViewById(R.id.buttonIngredientName)
        var ingredientInfo: ImageView = view.findViewById(R.id.imageViewInfo)
        var choose: CheckBox = view.findViewById(R.id.ingCheckBox)
        var cost: EditText = view.findViewById(R.id.editTextCost)
        lateinit var mItem: Ingredient
        val arr = Array(1000, { i -> false })
        val arr2 = Array(1000, { i -> "" })


        override fun toString(): String {
            return super.toString()
        }
    }

    override fun DoNetWorkOpreation(): String {
        if (!isScorlled) {
            page = 0
        }
        var string = UserInterFace.userID.toString() + " " + page

        var link =
            "https://elad1.000webhostapp.com/getMyAndSharedIngredients.php?ownerIDAndPage=" + string
        if (isSearch) {
            string = UserInterFace.userID.toString() + " " + ingredientToSearch
            link =
                "https://elad1.000webhostapp.com/getSpecificMyAndSharedIngredients.php?nameAndIngredient=" + string

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

    override fun getData(str: String) {
        // fixed a default .split spaces , and fixed spaces in howToStore.
        // when we add an ingredient it doesnt update in real time. we have to re compile!!!
        if (!str.equals("")) {
//            if (!isScorlled)
//                ingredientList!!.clear()

            if (isSearch) {
                mValues!!.clear()

            }
            val ingredients: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()

            try {
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
                    if (!mValues!!.contains(ing)) {
                        mValues?.add(ing)
                    }


                }
            }
            catch (e: Exception) {
                Log.v("Elad1", "Failled")
            }


            // initializing the singelton with the user's ingredients list to keep it here on code.
            // should do it on another place !!!
            //  UserPropertiesSingelton.getInstance()!!.setUserIngredientss(sorted)
            // sending the last to the adapter.
            this!!.setmValues(mValues!!)
            progressBar!!.visibility = View.INVISIBLE
            //isScorlled = false

        } else {
            progressBar!!.visibility = View.INVISIBLE
            if (isSearch) {
                mValues!!.clear()
              //  noResultsTextView.visibility = View.VISIBLE
                this!!.setmValues(mValues!!)
            }
        }
     //   isScorlled = false

        progressBar!!.visibility = View.INVISIBLE
    }

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

//    fun startTask() {
//
//        var s = AsynTaskNew(this, childFragmentManager)
//        s.execute()
//    }


    override fun onQueryTextChange(p0: String?): Boolean {
        if (p0 != "") {
            noResultsTextView!!.visibility = View.INVISIBLE
            isSearch = true
            ingredientToSearch = p0!!
            startTask()
        } else {
            isSearch = false
            mValues!!.clear()
            noResultsTextView!!.visibility = View.INVISIBLE
            startTask()
        }
        return true
    }

}