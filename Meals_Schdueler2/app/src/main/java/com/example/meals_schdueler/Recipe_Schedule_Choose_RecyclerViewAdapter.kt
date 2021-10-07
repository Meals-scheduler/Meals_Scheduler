package com.example.meals_schdueler


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class Recipe_Schedule_Choose_RecyclerViewAdapter(

    private var values: ArrayList<Recipe>,
    private var intValues: ArrayList<Int>,
    private var quantities: ArrayList<Int>,
    childFragmentManager: FragmentManager,
    progressbar: ProgressBar?,
    searchView: SearchView?,
    noResult: TextView?,
    context: Context
) : RecyclerView.Adapter<Recipe_Schedule_Choose_RecyclerViewAdapter.ViewHolder>(), GetAndPost,
    SearchView.OnQueryTextListener {

    private var mValues: ArrayList<Recipe> = values
    private var mIntValues: ArrayList<Int> = intValues
    private var childFragmentManager = childFragmentManager
    private var quantitiess = quantities
    private var page = 0
    private var isSearch = false
    private var recipeToSearch = ""
    private var isScorlled = false
    private var progressBar = progressbar
    private var searchView = searchView
    private var noResultsTextView = noResult
    private var context = context

    var str: String = ""


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Recipe_Schedule_Choose_RecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipes_choose, parent, false)
        searchView!!.setOnQueryTextListener(this)

        return ViewHolder(view)
    }

    fun startTask() {

        var s = AsynTaskNew(this, childFragmentManager,context)
        s.execute()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        var item: Recipe = mValues[position]!! // each item postion
        holder.mItem = item
        holder.ingredientName.setText(item.recipeName)
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        holder.ingredientImage.setImageBitmap(item.pictureBitMap)
        holder.ingredientInfo.setOnClickListener {
            var instructions = HowToStroreValue(item.instructions.howToStore)
            var dialog = MyRecipeIngredietns(
                item.listOfIngredients,
                item.recipeName,
                item.pictureBitMap,
                item.numOfPortions,
                item.quantityList,
                item.totalCost,
                item.typeOfMeal,
                instructions
            )
            dialog.show(childFragmentManager, "MyRecipeIngredients")

        }

        if (position == getItemCount() - 1 && !isSearch) {
            page = page + 8
            isScorlled = true
            progressBar!!.visibility = View.VISIBLE
            startTask()
        }

        holder.quantity.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {

                str = parent!!.getItemAtPosition(position).toString()


//                if (firstTime && quantitiess.size == mValues.size) {
//                    quantitiess.clear()
//                    firstTime = false
//                }
            }


            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        })


        holder.choose.setChecked(holder.arr[position]);
        holder.choose.setOnClickListener() {


            if (holder.choose.isChecked == true && !(mIntValues.contains(position))) {
                mValues.add(item)
                mIntValues.add(position)
                holder.arr[position] = true
                if (str == "") {
                    quantitiess.add(1)
                } else {
                    quantitiess.add(str.toInt())
                    str = ""
                }


//                if ((holder.cost.getText().toString().trim().length > 0)) {
//                    holder.arr2[position]=holder.cost.text.toString()
//                }

            } else if (holder.choose.isChecked == false) {
                if (mIntValues.contains(position)) {
                    holder.arr[position] = false
                    mIntValues.remove(position)
                    quantitiess.remove(position)
                }

            }


        }


    }


    fun setmValues(mValues: ArrayList<Recipe>) {
        this.mValues = mValues
        notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
    }


    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // val idView: TextView = view.findViewById(R.id.item_number)
        //val contentView: TextView = view.findViewById(R.id.content)
        var ingredientImage: ImageView = view.findViewById(R.id.imageViewPicRecipe)
        var ingredientName: Button = view.findViewById(R.id.buttonRecipeName)
        var ingredientInfo: ImageView = view.findViewById(R.id.imageViewIngredientsInfo)
        var choose: CheckBox = view.findViewById(R.id.RecipeCheckBox)
        var quantity: Spinner = view.findViewById(R.id.spinner)
        lateinit var mItem: Recipe
        val arr = Array(100, { i -> false })
//        val arr2 = Array(mValues.size, {i->""})



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
            "https://elad1.000webhostapp.com/getMyAndSharedRecipes.php?ownerIDAndPage=" + string
        if (isSearch) {
            string = UserInterFace.userID.toString() + " " + recipeToSearch
            link =
                "https://elad1.000webhostapp.com/getSpecificMyAndSharedRecipes.php?nameAndRecipe=" + string

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

    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun getData(str: String) {

        //recipe size 11
        // ingredient size 15
        if (!str.equals("")) {

            var start = 8
            //  recipeList!!.clear()
            val recipesAndIngredients: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()
            if (isSearch) {
                mValues!!.clear()

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
                var count = 0
                for (j in recipesAndIngredients.indices) {
                    var recipesAndIngredients2 = recipesAndIngredients[j].splitIgnoreEmpty("*")
                    if (recipesAndIngredients2.size == 10) {
                        recipeIds.add(recipesAndIngredients2[0].toInt())
                        count++
                    } else {
                        count++
                        start = count
                        break
                    }

                }
            } else {
                if (recipesAndIngredients.size < 7) {
                    for (j in 0..recipesAndIngredients.size-1) {
                        var recipesAndIngredients2 = recipesAndIngredients[j].splitIgnoreEmpty("*")
                        recipeIds.add(recipesAndIngredients2[0].toInt())
                    }
                } else {
                    for (j in 0..7) {
                        var recipesAndIngredients2 = recipesAndIngredients[j].splitIgnoreEmpty("*")
                        recipeIds.add(recipesAndIngredients2[0].toInt())
                    }
                }
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





            currentID = -1
            for (i in recipesAndIngredients.indices) {

                var recipe2 = recipesAndIngredients[i].splitIgnoreEmpty("*")
                if (recipe2.size == 10) {
                    var s = recipe2[0].toInt()
                    var instructions =
                        HowToStroreValue(recipe2[9])
                    if (s != currentID)
                        mValues?.add(
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
                                quantities.get(s)!!,
                                // hashMap.get(recipe2[0].toInt())!!.second
                                instructions
                            )
                        )

                    currentID = recipe2[0].toInt()

                }
            }

            this!!.setmValues(mValues!!)
            progressBar!!.visibility = View.INVISIBLE
        } else {
            progressBar!!.visibility = View.INVISIBLE
            if (isSearch) {
                mValues!!.clear()
                //  noResultsTextView.visibility = View.VISIBLE
                this!!.setmValues(mValues!!)
            }
        }

    }


    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        if (p0 != "") {
            noResultsTextView!!.visibility = View.INVISIBLE
            isSearch = true
            recipeToSearch = p0!!
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


