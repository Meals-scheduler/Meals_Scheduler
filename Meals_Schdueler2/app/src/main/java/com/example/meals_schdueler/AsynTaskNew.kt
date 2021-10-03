package com.example.meals_schdueler

import android.app.AlertDialog
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import androidx.fragment.app.FragmentManager


class AsynTaskNew(action: GetAndPost, childFragmentManager: FragmentManager, context: Context) :
    AsyncTask<Void, Void, String>() {

    //Android AsyncTask: You can interact with UI thread (Main Thread) with AsynTask when you have to do some background task.
    // AsyncTask class perform background operations and publish results on the UI thread without having to manipulate threads.
    //
    //In your application, you can use android AsyncTask class for short operations or task ( few seconds),
    // for the long-running operation you have to choose another option.

    var action: GetAndPost = action
    var childFragmentManager = childFragmentManager
    lateinit var pbDialog: ProgressBarDialog
    var context = context


    override fun doInBackground(vararg params: Void?): String? {
        // ...
        try {
            Log.v("Elad1", "don in background good")

            return action.DoNetWorkOpreation()

        } catch (e: Exception) {
            Log.v("Elad1", "Failled")
            pbDialog.dismiss()


            try {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Sorry,something went wrong. try again!").show()

            } catch (e: Exception) {
                Log.v("Elad1", "Failled because of builder ")
            }


        }

        return ""
    }

    override fun onPreExecute() {
        super.onPreExecute()
        // ...
        pbDialog = ProgressBarDialog()
        pbDialog.show(childFragmentManager, "progressbar")


    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        try {
            if (result != null) {
                action.getData(result)
            }

            if (!(action is DeleteAlertDialog)) {
                pbDialog.dismiss()
            }
        } catch (e: Exception) {
            Log.v("Elad1", "Failled")
            pbDialog.dismiss()


            try {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Sorry,something went wrong. try again!").show()

            } catch (e: Exception) {
                Log.v("Elad1", "Failled because of builder ")
            }


        }

    }
}