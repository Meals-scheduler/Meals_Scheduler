package com.example.meals_schdueler

import android.app.AlertDialog
import android.os.AsyncTask
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.example.meals_schdueler.dummy.DailySchedule


class AsynTaskNew(action: GetAndPost, childFragmentManager: FragmentManager) :
    AsyncTask<Void, Void, String>() {

    //Android AsyncTask: You can interact with UI thread (Main Thread) with AsynTask when you have to do some background task.
    // AsyncTask class perform background operations and publish results on the UI thread without having to manipulate threads.
    //
    //In your application, you can use android AsyncTask class for short operations or task ( few seconds),
    // for the long-running operation you have to choose another option.

    var action: GetAndPost = action
    var childFragmentManager = childFragmentManager
    lateinit var pbDialog: ProgressBarDialog


    override fun doInBackground(vararg params: Void?): String? {
        // ...
        Log.v("Elad1", "don in background good")

        return action.DoNetWorkOpreation()


    }

    override fun onPreExecute() {
        super.onPreExecute()
        // ...
        pbDialog = ProgressBarDialog()
        pbDialog.show(childFragmentManager, "progressbar")


    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)

        if (result != null) {
            action.getData(result)
        }
        // if we upload a new ingredient we want to refresh the MyIngredients tab so that the user will be able to see the new uploaded ingredient.
//        if (action is Ingredient) {
//            if (MyingredientFragment1.getInstance1().isAdded){
//                MyingredientFragment1.getInstance1().startTask()
//                Log.v("Elad1","HERE YA")
//
//            }
//
//        }
        else if (action is Recipe) {
            if (MyRecipeFragment.getInstance1().isAdded)
                MyRecipeFragment.getInstance1().startTask()
        } else if (action is DeleteAlertDialog) {

            when ((action as DeleteAlertDialog).type) {
                "Ingredient" -> MyingredientFragment1.getInstance1().startTask()
                "Recipe" -> MyRecipeFragment.getInstance1().startTask()
                "Daily" -> MyDailyScheduleFragment.getInstance1().startTask()
                "Weekly" -> MyWeeklyScheduleFragment.getInstance1().startTask()
                "Monthly" -> MyMonthlyScheudleFragment.getInstance1().startTask()
                "Yearly" -> MyYearlyScheudleFragment.getInstance1().startTask()
                "Event" -> MyEventScheduleFragment.getInstance1().startTask()
                "Upcoming" -> UpcomingScheduleFragment.getInstance1().startTask()
            }


        } else if (action is DailySchedule) {
            // if (MyDailyScheduleFragment.getInstance1().isAdded)
            MyDailyScheduleFragment.getInstance1().startTask()
        }
        else if (action is WeeklySchedule) {
            MyWeeklyScheduleFragment.getInstance1().startTask()
        } else if (action is MonthlySchedule) {
            MyMonthlyScheudleFragment.getInstance1().startTask()
        } else if (action is YearlySchedule) {
            MyYearlyScheudleFragment.getInstance1().startTask()
        }
        else if( action is Event){
            MyEventScheduleFragment.getInstance1().startTask()
        }
        if (!(action is DeleteAlertDialog)) {
            pbDialog.dismiss()
        }
    }
}