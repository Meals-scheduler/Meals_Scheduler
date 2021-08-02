package com.example.meals_schdueler

import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meals_schdueler.dummy.DummyContent.DummyItem
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.collections.ArrayList
import com.example.meals_schdueler.MyingredientFragment1 as MyingredientFragment11

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 * TODO: Replace the implementation with code for your data type.
 */
class MyItemRecyclerViewAdapter(
    private var values: ArrayList<Ingredient>,
    childFragmentManager: FragmentManager
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    private var mValues: ArrayList<Ingredient> = values
    private var childFragmentManager = childFragmentManager
    private var pos = 0
    private var imgId = 0
    private var isFinished = false
    private var imagesArr: ArrayList<Bitmap> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_myingredient1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item: Ingredient = mValues[position] // each item postion
        holder.mItem = item
        holder.ingredientName.setText(item.ingridentName)
        //var bitmap2 = ImageConvert.StringToBitMap(item.picture)
        holder.ingredientImage.setImageBitmap(item.pictureBitMap)
        holder.ingredientInfo.setOnClickListener {
            var dialog = MyIngredientInfo(item, false)
            dialog.show(childFragmentManager, "IngredientInfo")

        }
        holder.deleteIngredient.setOnClickListener {
            var dialog = DeleteAlertDialog(
                item.ingridentName,
                item.pictureBitMap,
                item.ingredientID,
                false,
                false
            )
            dialog.show(childFragmentManager, "DeleteAlertDialog")
        }
//
//        if (position >= 0) {
//            pos = position
//            imgId = item.ingredientID
//            var s = AsynTaskNew(this, childFragmentManager)
//            s.execute()



//            if (isFinished) {
//                var j = pos
//                var i = 4
//                while (i != 0) {
//                    mValues[pos].pictureBitMap = imagesArr.get(pos)
//                    holder.ingredientImage.setImageBitmap(imagesArr.get(pos))
//                    notifyDataSetChanged()
//                    //  item.pictureBitMap = imagesArr.get(pos)
//                    j++
//                    i--
//                }
//                isFinished = false
//
//            }


            //holder.idView.text = item.id
            // holder.contentView.text = item.content
        }

        fun setmValues(mValues: ArrayList<Ingredient>) {

            this.mValues = mValues
            notifyDataSetChanged() // notifying android that we changed the list,refresh the list that was empty at first.
        }

        override fun getItemCount(): Int = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            // val idView: TextView = view.findViewById(R.id.item_number)
            //val contentView: TextView = view.findViewById(R.id.content)
            var ingredientImage: ImageView = view.findViewById(R.id.imageViewPicIngr)
            var ingredientCart: ImageView = view.findViewById(R.id.imageViewCart)
            var ingredientName: Button = view.findViewById(R.id.buttonIngredientName)
            var ingredientInfo: ImageView = view.findViewById(R.id.imageViewInfo)
            var deleteIngredient: ImageView = view.findViewById(R.id.imageViewDel)
            lateinit var mItem: Ingredient


            override fun toString(): String {
                return super.toString()
            }
        }

//    override fun DoNetWorkOpreation(): String {
//        val link =
//            "https://elad1.000webhostapp.com/getIngredientImg.php?imgId=" + imgId;
//        Log.v("Elad1", "here")
//
//        val sb = StringBuilder()
//
//        val url = URL(link)
//        val urlConnection = url.openConnection() as HttpURLConnection
//        try {
//            val `in`: InputStream = BufferedInputStream(urlConnection.inputStream)
//            val bin = BufferedReader(InputStreamReader(`in`))
//            // temporary string to hold each line read from the reader.
//            var inputLine: String?
//
//            while (bin.readLine().also { inputLine = it } != null) {
//                sb.append(inputLine)
//
//            }
//        } finally {
//            // regardless of success or failure, we will disconnect from the URLConnection.
//            urlConnection.disconnect()
//        }
//
//
//        Log.v("Elad1", "Id came is" + sb.toString())
//        return sb.toString()
//    }
//
//    override fun getData(str: String) {
//        Log.v("Elad1", "str is " + str)
//        val images: Array<String> = str.splitIgnoreEmpty("***").toTypedArray()
//
//
//        for (i in images.indices) {
//            Log.v("Elad1", images.indices.toString())
//            var images2 = images[i].splitIgnoreEmpty("*")
//            imagesArr.add(ImageConvert.StringToBitMap(images2[0])!!)
//
//
//        }
//        isFinished = true
//    }
//
//    // to avoid empty string cells .split function returns.
//    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
//        return this.split(*delimiters).filter {
//            it.isNotEmpty()
//        }
//    }

    }

