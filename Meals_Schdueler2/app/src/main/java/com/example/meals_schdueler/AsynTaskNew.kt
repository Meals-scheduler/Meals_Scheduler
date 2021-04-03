
package com.example.meals_schdueler

import android.os.AsyncTask
import android.util.Log


class AsynTaskNew(action: GetAndPost) : AsyncTask<Void, Void, String>() {

    //Android AsyncTask: You can interact with UI thread (Main Thread) with AsynTask when you have to do some background task.
    // AsyncTask class perform background operations and publish results on the UI thread without having to manipulate threads.
    //
    //In your application, you can use android AsyncTask class for short operations or task ( few seconds),
    // for the long-running operation you have to choose another option.

    var action : GetAndPost = action



    override fun doInBackground(vararg params: Void?): String? {
        // ...
        Log.v("Elad1", "don in background good")
        return action.DoNetWorkOpreation()


    }

    override fun onPreExecute() {
        super.onPreExecute()
        // ...
//        var pbDialog = ProgressBarDialog()
//        pbDialog.show()



    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result != null) {
            action.getData(result)
        }
        // if we upload a new ingredient we want to refresh the MyIngredients tab so that the user will be able to see the new uploaded ingredient.
        if(action is Ingredient){
            MyingredientFragment1.getInstance1().startTask()
            AddIngredientFragment.pbDialog.dismiss()



        }
    }
}