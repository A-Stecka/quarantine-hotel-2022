package com.iotproject.hotel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class RoomCardFragment : Fragment() {

    private var requestQueue: RequestQueue? = null

    private val BASEURL = "http://192.168.0.66:8080/api/"

    private fun blockCard(volleyListener: VolleyListener, blockCardJson: JSONObject){
        val urlBlockCard = BASEURL + "blockCard"
        requestQueue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, urlBlockCard, blockCardJson,
                { response -> cardBlocked(response, volleyListener) },
                { error -> error.printStackTrace() })
        requestQueue?.add(jsonObjectRequest)
    }

    private fun checkOut(volleyListener: VolleyListener, checkOutJson: JSONObject){
        val urlCheckOut = BASEURL + "checkOut"
        requestQueue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, urlCheckOut, checkOutJson,
                { response -> checkedOut(response, volleyListener) },
                { error -> error.printStackTrace() })
        requestQueue?.add(jsonObjectRequest)
    }

    private fun cardBlocked(response: JSONObject, volleyListener: VolleyListener){
        Toast.makeText(activity, "Card blocked", Toast.LENGTH_SHORT).show()
        volleyListener.onResponseReceived()
    }

    private fun checkedOut(response: JSONObject, volleyListener: VolleyListener){
        Toast.makeText(activity, "Checked-out", Toast.LENGTH_SHORT).show()
        volleyListener.onResponseReceived()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_room_card, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())

        val guestId = preferences.getInt("guest_id", 0)
        val guestIdJsonObject = JSONObject()
        if (guestId > 0) {
            guestIdJsonObject.put("guest_id", guestId)
        }

        val outputRoomNumber: TextView = view.findViewById(R.id.roomNumberText)
        var room = preferences.getInt("room", 0)
        if (room > 0) outputRoomNumber.text = room.toString()

        val checkOutButton: Button = view.findViewById(R.id.checkOutButton)
        val blockButton: Button = view.findViewById(R.id.blockCardButton)

        val volleyListenerCheckOut: VolleyListener = object : VolleyListener {
            override fun onResponseReceived() {
                checkOutButton.isEnabled = false
                outputRoomNumber.text = ""
                preferences.edit {
                    putInt("room", 0)
                }
                room = 0
                blockButton.isEnabled = false
            }
        }

        if (room == 0) {
            checkOutButton.isEnabled = false
            blockButton.isEnabled = false
        }

        checkOutButton.setOnClickListener {
            if (guestId > 0)
            checkOut(volleyListenerCheckOut, guestIdJsonObject)
        }

        val volleyListener: VolleyListener = object : VolleyListener {
            override fun onResponseReceived() {
                blockButton.isEnabled = false
            }
        }

        blockButton.setOnClickListener {
            if (guestId > 0)
            blockCard(volleyListener, guestIdJsonObject)
        }
    }
}