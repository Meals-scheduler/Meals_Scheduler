package com.example.meals_schdueler

import android.os.AsyncTask
import android.util.Log

class AsynTaskNew(action : GetAndPost) : AsyncTask<Void, Void, String>() {

    var action : GetAndPost = action

    override fun doInBackground(vararg params: Void?): String? {
        // ...
        Log.v("Elad1", "don in background good")
         return action.DoNetWorkOpreation()


    }

    override fun onPreExecute() {
        super.onPreExecute()
        // ...
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result != null) {
            action.getData(result)
        }

    }
}