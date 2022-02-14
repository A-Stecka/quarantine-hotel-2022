package com.iotproject.hotel

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONObject
import androidx.core.content.edit
import com.google.android.material.textfield.TextInputLayout.END_ICON_NONE
import java.util.regex.Pattern


class UserFragment : Fragment() {

    private var requestQueue: RequestQueue? = null
    private var requestQueueAddGuest: RequestQueue? = null
    private var requestQueueGetGuest: RequestQueue? = null

    var countriesList = mutableListOf<String>()
    var countriesCodesMap = HashMap<Int, String>()
    var guestDataMap = HashMap<String, String>()
    val guestData = arrayOf("name","surname","document_no","phone_no","email","address","zip_code","city","country")

    var guestId: Int = 0
    var guestName: String? = null
    var guestSurname: String? = null
    var guestDocument: String? = null
    var guestPhone: String? = null
    var guestEmail: String? = null
    var guestAddress: String? = null
    var guestCity: String? = null
    var guestCode: String? = null
    var guestCountry: String? = null
    var guestCountryCode: Int? = null

    private val BASEURL = "http://192.168.0.66:8080/api/"

    private fun getJsonDataFromApi(volleyListener: VolleyListener){
        val urlCountries = BASEURL + "countries"
        requestQueue = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, urlCountries, null,
                { response -> parseJson(response, volleyListener) },
                { error -> error.printStackTrace() })
        requestQueue?.add(jsonObjectRequest)
    }

    private fun parseJson(response: JSONObject, volleyListener: VolleyListener){
        for (i in 1..response.length()){
            val country = response.getString(i.toString())
            countriesList.add(country)
            countriesCodesMap[i] = country
        }
        volleyListener.onResponseReceived()
    }

    private fun addGuest(volleyListener: VolleyListener, guestJson: JSONObject){
        val urlAddGuest = BASEURL + "addGuest"
        requestQueueAddGuest = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, urlAddGuest, guestJson,
            { response -> guestAdded(response, volleyListener) },
                { error -> error.printStackTrace() })
        requestQueueAddGuest?.add(jsonObjectRequest)
    }

    private fun guestAdded(response: JSONObject, volleyListener: VolleyListener){
        Toast.makeText(activity, "Data saved", Toast.LENGTH_SHORT).show()
        guestId = response.getInt("guest_id")
        volleyListener.onResponseReceived()
    }

    private fun getGuest(volleyListener: VolleyListener, guestIdJson: JSONObject){
        val urlGetGuest = BASEURL + "getGuest"
        requestQueueGetGuest = Volley.newRequestQueue(requireContext())
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, urlGetGuest, guestIdJson,
                { response -> guestGot(response, volleyListener) },
                { error -> error.printStackTrace() })
        requestQueueGetGuest?.add(jsonObjectRequest)
    }

    private fun guestGot(response: JSONObject, volleyListener: VolleyListener){
        for (i in 0 until response.length()){
            val data = response.getString(guestData[i]) //value
            guestDataMap[guestData[i]] = data
        }
        volleyListener.onResponseReceived()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputName: TextInputLayout = view.findViewById(R.id.nameTextField)
        val inputSurname: TextInputLayout = view.findViewById(R.id.surnameTextField)
        val inputDocument: TextInputLayout = view.findViewById(R.id.documentTextField)
        val inputPhone: TextInputLayout = view.findViewById(R.id.phoneTextField)
        val inputEmail: TextInputLayout = view.findViewById(R.id.emailTextField)
        val inputAddress: TextInputLayout = view.findViewById(R.id.addressTextField)
        val inputCity: TextInputLayout = view.findViewById(R.id.cityTextField)
        val inputCode: TextInputLayout = view.findViewById(R.id.codeTextField)
        val inputCountry: TextInputLayout = view.findViewById(R.id.countryTextField)

        val preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(requireContext())
        val buttonSave: Button = view.findViewById(R.id.saveButton)

        val checkedIn = preferences.getBoolean("checked_in", false)

        if (checkedIn){
            val volleyListenerCheckedIn: VolleyListener = object : VolleyListener {
                override fun onResponseReceived() {
                    inputName.editText?.setText(guestDataMap[guestData[0]])
                    inputSurname.editText?.setText(guestDataMap[guestData[1]])
                    inputDocument.editText?.setText(guestDataMap[guestData[2]])
                    inputPhone.editText?.setText(guestDataMap[guestData[3]])
                    inputEmail.editText?.setText(guestDataMap[guestData[4]])
                    inputAddress.editText?.setText(guestDataMap[guestData[5]])
                    inputCity.editText?.setText(guestDataMap[guestData[7]])
                    inputCode.editText?.setText(guestDataMap[guestData[6]])
                    inputCountry.editText?.setText(guestDataMap[guestData[8]])

                    inputName.editText?.inputType = InputType.TYPE_NULL
                    inputSurname.editText?.inputType = InputType.TYPE_NULL
                    inputDocument.editText?.inputType = InputType.TYPE_NULL
                    inputPhone.editText?.inputType = InputType.TYPE_NULL
                    inputEmail.editText?.inputType = InputType.TYPE_NULL
                    inputAddress.editText?.inputType = InputType.TYPE_NULL
                    inputCity.editText?.inputType = InputType.TYPE_NULL
                    inputCode.editText?.inputType = InputType.TYPE_NULL
                    inputCountry.editText?.inputType = InputType.TYPE_NULL

                    inputCountry.endIconMode = END_ICON_NONE
                    buttonSave.isEnabled = false
                }
            }
            guestId = preferences.getInt("guest_id", 0)
            val guestIdJsonObject = JSONObject()
            guestIdJsonObject.put("guest_id", guestId)
            getGuest(volleyListenerCheckedIn, guestIdJsonObject)
        }

        else {
            val volleyListener: VolleyListener = object : VolleyListener {
                override fun onResponseReceived() {
                    val countryAdapter = ArrayAdapter(requireContext(), R.layout.country_item, countriesList)
                    (inputCountry.editText as? AutoCompleteTextView)?.setAdapter(countryAdapter)
                }
            }

            getJsonDataFromApi(volleyListener)

            buttonSave.setOnClickListener {

                if (Pattern.matches("^[A-Z][a-z]*\$", inputName.editText?.text.toString()))
                guestName = inputName.editText?.text.toString()
                else Toast.makeText(requireContext(), "Invalid name", Toast.LENGTH_SHORT).show()

                if (Pattern.matches("^[A-Z][a-z]*'?-?[A-Z][a-z]*", inputSurname.editText?.text.toString())
                        || Pattern.matches("^[A-Z][a-z]*\$", inputSurname.editText?.text.toString()))
                guestSurname = inputSurname.editText?.text.toString()
                else Toast.makeText(requireContext(), "Invalid surname", Toast.LENGTH_SHORT).show()

                if (Pattern.matches("^[A-Z0-9][A-Z ]*[0-9 ]*", inputDocument.editText?.text.toString()))
                guestDocument = inputDocument.editText?.text.toString()
                else Toast.makeText(requireContext(), "Invalid document", Toast.LENGTH_SHORT).show()

                if (Pattern.matches("^\\+[1-9][0-9 ]*", inputPhone.editText?.text.toString()))
                guestPhone = inputPhone.editText?.text.toString()
                else Toast.makeText(requireContext(), "Invalid phone", Toast.LENGTH_SHORT).show()

                if (Pattern.matches("^[0-9a-zA-Z][0-9a-zA-Z.]*@[0-9a-zA-Z.]*.[0-9a-zA-Z]", inputEmail.editText?.text.toString()))
                guestEmail = inputEmail.editText?.text.toString()
                else Toast.makeText(requireContext(), "Invalid e-mail", Toast.LENGTH_SHORT).show()

                if (Pattern.matches("[A-Za-z0-9. \\-']*", inputAddress.editText?.text.toString()))
                guestAddress = inputAddress.editText?.text.toString()
                else Toast.makeText(requireContext(), "Invalid address", Toast.LENGTH_SHORT).show()

                if (Pattern.matches("^[A-Z][A-Za-z. \\-']*", inputCity.editText?.text.toString()))
                guestCity = inputCity.editText?.text.toString()
                else Toast.makeText(requireContext(), "Invalid city", Toast.LENGTH_SHORT).show()

                if (Pattern.matches("^[A-Z0-9][A-Z0-9 \\-]*", inputCode.editText?.text.toString()))
                guestCode = inputCode.editText?.text.toString()
                else Toast.makeText(requireContext(), "Invalid code", Toast.LENGTH_SHORT).show()

                guestCountry = inputCountry.editText?.text.toString()
                if (countriesCodesMap.isNotEmpty())
                guestCountryCode = countriesCodesMap.filterValues { it == guestCountry }.keys.first().toInt()
                else Toast.makeText(requireContext(), "Invalid country", Toast.LENGTH_SHORT).show()

                if (guestEmail != null || guestName != null || guestSurname != null || guestDocument != null
                        || guestPhone != null || guestAddress != null || guestCode != null || guestCity != null
                        || guestCountry != null) {

                    val guestJsonObject = JSONObject()
                    guestJsonObject.put("email", guestEmail)
                    guestJsonObject.put("name", guestName)
                    guestJsonObject.put("surname", guestSurname)
                    guestJsonObject.put("doc_no", guestDocument)
                    guestJsonObject.put("phone_no", guestPhone)
                    guestJsonObject.put("address", guestAddress)
                    guestJsonObject.put("zip_code", guestCode)
                    guestJsonObject.put("city", guestCity)
                    guestJsonObject.put("country_code", guestCountryCode)

                    val volleyListenerGuest: VolleyListener = object : VolleyListener {
                        override fun onResponseReceived() {
                            preferences.edit {
                                putInt("guest_id", guestId)
                                putBoolean("checked_in", false)
                                putString("guest_name", guestName)
                                putString("guest_surname", guestSurname)
                            }
                            findNavController().navigate(R.id.action_userFragment_to_checkInFragment)
                        }
                    }
                    addGuest(volleyListenerGuest, guestJsonObject)
                }
            }
        }
    }
}