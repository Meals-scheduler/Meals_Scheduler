package com.example.meals_schdueler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CreatorInfoDialog(shareInfo: Boolean, ownerId: Int) : DialogFragment() , View.OnClickListener, GetAndPost{


    lateinit var creatorImage : ImageView
    lateinit var xImage : ImageView
    lateinit var creatorName : TextView
    lateinit var creatorMail : TextView
    var shareInfo = shareInfo
    var ownerId = ownerId

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.creator_info, container, false)

        creatorImage = rootView.findViewById(R.id.imageViewCreaotr)
        creatorName = rootView.findViewById(R.id.creatorName)
        creatorMail = rootView.findViewById(R.id.textViewMail)
        xImage = rootView.findViewById(R.id.imageViewX)

        xImage.setOnClickListener(this)
        // if this user accepts to share his/hers info
        if(shareInfo){
            var s = AsynTaskNew(this, childFragmentManager,requireContext())
            s.execute()
        }

        return rootView


    }

    override fun onClick(p0: View?) {
        when(p0){
            xImage-> dismiss()
        }
    }

    override fun DoNetWorkOpreation(): String {
        val link = "https://elad1.000webhostapp.com/getUserInfo.php?OwnerID="+ownerId ;


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

    // to avoid empty string cells .split function returns.
    fun CharSequence.splitIgnoreEmpty(vararg delimiters: String): List<String> {
        return this.split(*delimiters).filter {
            it.isNotEmpty()
        }
    }

    override fun getData(str: String) {

        val userInfo: Array<String> = str.splitIgnoreEmpty(",").toTypedArray()
        creatorName.setText(userInfo[0]+" " +userInfo[1])
        creatorMail.setText(userInfo[2])
        //creatorImage.setImageBitmap(ImageConvert.StringToBitMap(userInfo[3]))


    }

}