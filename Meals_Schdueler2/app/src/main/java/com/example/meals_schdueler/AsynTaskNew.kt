package com.example.meals_schdueler

import android.os.AsyncTask
import android.util.Log

class AsynTaskNew(action : GetAndPost) : AsyncTask<Void, Void, String>() {

    var action : GetAndPost = action

    override fun doInBackground(vararg params: Void?): String? {
        // ...
        Log.v("Elad1", "don in background good")
        action.DoNetWorkOpreation()

        return ""
    }

    override fun onPreExecute() {
        super.onPreExecute()
        // ...
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        // ...
    }
}