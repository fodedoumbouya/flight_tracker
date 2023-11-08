package com.example.flight_tracker.network

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by sergio on 04/03/2021
 * All rights reserved GoodBarber
 */
class RequestManager {
    var TAG = RequestManager::class.java.simpleName

    companion object {
        fun get(
            sourceUrl: String?,
            params: Map<String, Any>?
        ) : RequestListener<String>{
            val result = StringBuilder()
            var finalSourceUrl = sourceUrl
            try {
                //Params
                var c = 0
                for (key in params!!.keys) {
                    val value = params[key]
                    if (c != 0) {
                        finalSourceUrl += "&"
                    } else {
                        finalSourceUrl += "?"
                    }

                    finalSourceUrl += "$key=$value"
                    c++

                }
                val url = URL(finalSourceUrl)
                val httpURLConnection =
                    url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "GET"
                httpURLConnection.connectTimeout = 10000
                httpURLConnection.readTimeout = 10000
                Log.i(
                    "RequestManager",
                    "Request[GET]: \nURL: $finalSourceUrl\nNb Param: $c, httpURLConnection : ${httpURLConnection.responseCode}"
                )
                val reader =
                    BufferedReader(InputStreamReader(httpURLConnection.inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    result.append(line)
                }
                reader.close()
                return RequestListener.Success(result.toString())
            } catch (e: IOException) {
                Log.e(
                    "RequestManager",
                    "Error while doing GET request (url: " + finalSourceUrl + ") - " + e.cause
                )
                return RequestListener.Error(e)
            }
        }

        suspend fun getSuspended(
            sourceUrl: String?,
            params: Map<String, Any>?,
            responseAlwaysOK : Boolean = false
        ): RequestListener<String> {
            val result = StringBuilder()
            var finalSourceUrl = sourceUrl
            var responseCode = -1
            try {
                //Params
                var c = 0
                for (key in params!!.keys) {
                    val value = params[key]
                    if (c != 0) {
                        finalSourceUrl += "&"
                    } else {
                        finalSourceUrl += "?"
                    }

                    finalSourceUrl += "$key=$value"
                    c++

                }
                val url = URL(finalSourceUrl)
                val httpURLConnection =
                    url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "GET"
                httpURLConnection.connectTimeout = 30000
                httpURLConnection.readTimeout = 30000
                responseCode = httpURLConnection.responseCode
                Log.i(
                    "RequestManager",
                    "Request[GET]: \nURL: $finalSourceUrl\nNb Param: $c, httpURLConnection : ${httpURLConnection.responseCode}"
                )
                val reader =
                    BufferedReader(InputStreamReader(httpURLConnection.inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    result.append(line)
                }
                reader.close()
                return RequestListener.Success(result.toString())
            } catch (e: IOException) {
                Log.e(
                    "RequestManager",
                    "Error while doing GET request : ${e.message} "
                )

                if(responseCode == 404) {
                    return RequestListener.Failed("Error while doing GET request : $responseCode")
                }
                return RequestListener.Error(e)
            }
        }
    }

}