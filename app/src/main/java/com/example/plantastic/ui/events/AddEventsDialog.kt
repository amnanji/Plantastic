package com.example.plantastic.ui.events

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.ParseException
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.example.plantastic.R
import com.example.plantastic.models.Events
import com.example.plantastic.models.Groups
import com.example.plantastic.repository.GroupsRepository
import com.example.plantastic.repository.UsersAuthRepository
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.TimeZone

class AddEventsDialog : DialogFragment() {
    private lateinit var groupsSpinner: Spinner
    private lateinit var dateTextView: TextView
    private lateinit var dateBtn: Button
    private lateinit var timeTextView: TextView
    private lateinit var timeBtn: Button
    private lateinit var titleTextView: TextInputEditText
    private lateinit var descriptionTextView: TextInputEditText
    private lateinit var locationTextView: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private val groupsRepository = GroupsRepository()
    private var groups: List<Groups?> = ArrayList()

    companion object {
        const val TAG_ADD_TODO_ITEM = "ADD_EVENTS_ITEM"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Referenced from: https://stackoverflow.com/questions/27965662/how-can-i-change-default-dialog-button-text-color-in-android-5
        val builder = AlertDialog.Builder(requireActivity(), R.style.DialogBoxTheme)
        val view: View =
            requireActivity().layoutInflater.inflate(R.layout.dialog_add_new_event, null)


        groupsSpinner = view.findViewById(R.id.eventSpinnerGroup)


        dateTextView = view.findViewById(R.id.eventsViewDate)
        dateBtn = view.findViewById(R.id.eventsBtnDate)
        titleTextView = view.findViewById(R.id.eventsAddTitle)
        locationTextView = view.findViewById(R.id.eventsAddLocation)
        timeBtn = view.findViewById(R.id.eventsBtnTime)
        timeTextView = view.findViewById(R.id.eventsViewTime)
        descriptionTextView = view.findViewById(R.id.eventsAddDescription)
        btnCancel = view.findViewById(R.id.eventsCancelButton)
        btnSave = view.findViewById(R.id.eventsSaveButton)

        val currUser = UsersAuthRepository().getCurrentUser()

        CoroutineScope(Dispatchers.IO).launch {
            groups = groupsRepository.getAllGroupsByUserWithChatNamesAsync(currUser!!.uid)
            withContext(Dispatchers.Main) {
                val groupNames = groups.map { it!!.name }
                val groupsAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    groupNames
                )
                groupsSpinner.adapter = groupsAdapter
            }
        }

        dateBtn.setOnClickListener {
            val calendar = Calendar.getInstance()

            // If user had previously inputted some data, we try to use that value to show in the dialog
            if (dateTextView.text.isNotBlank()) {
                try {
                    calendar.timeInMillis = parseDate()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, year, monthOfYear, dayOfMonth ->
                    dateTextView.text =
                        getString(R.string.date_placeholder, year, (monthOfYear + 1), dayOfMonth)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE)
            )
            datePickerDialog.show()
        }
        timeBtn.setOnClickListener {
            val calendar = Calendar.getInstance()

            // If user had previously inputted some data, we try to use that value to show in the dialog
            if (timeTextView.text.isNotBlank()) {
                try {
                    calendar.timeInMillis = parseTime()
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    timeTextView.text = getString(R.string.time_placeholder, hourOfDay, minute)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false // set to true if you want 24-hour format
            )
            timePickerDialog.show()
        }

        btnSave.setOnClickListener {
            val isDataComplete = validateData()
            if (isDataComplete) {
                val eventItem = Events(
                    titleTextView.text.toString(),
                    locationTextView.text.toString(),
                    parseDateTime(),
                    groups[groupsSpinner.selectedItemPosition]?.id,
                    descriptionTextView.text.toString()
                )
                groupsRepository.addEventsItem(
                    eventItem,
                    groups[groupsSpinner.selectedItemPosition]?.id,
                )


                val readCalendarPermission = ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED

                val writeCalendarPermission = ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_CALENDAR
                ) == PackageManager.PERMISSION_GRANTED
                if (readCalendarPermission && writeCalendarPermission) {
                    addEventToCalendar(eventItem)
                } else {
                    Log.d("Revs", "no permission!!!!!!!!!!!!!!!!")
                    // Request permissions
                    val permissionsToRequest = mutableListOf<String>()

                    if (!readCalendarPermission) {
                        permissionsToRequest.add(Manifest.permission.READ_CALENDAR)
                    }

                    if (!writeCalendarPermission) {
                        permissionsToRequest.add(Manifest.permission.WRITE_CALENDAR)
                    }

                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        permissionsToRequest.toTypedArray(),
                        123
                    )
                }



                dialog?.dismiss()
            }
        }

        btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        dateTextView.addTextChangedListener {
            validateData()
        }
        timeTextView.addTextChangedListener {
            validateData()
        }
        titleTextView.addTextChangedListener {
            validateData()
        }
        descriptionTextView.addTextChangedListener {
            validateData()
        }
        locationTextView.addTextChangedListener {
            validateData()
        }

        btnCancel.setOnClickListener {
            dialog?.dismiss()
        }

        builder.setView(view)
        builder.setTitle("Add New Event")
        val dialog = builder.create()
        // Referenced from: https://stackoverflow.com/questions/18346920/change-the-background-color-of-a-pop-up-dialog
        dialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_borders_15dp)
        return dialog
    }

    private fun parseDate(): Long {
        // Choosing what data we are parsing based on the dialog we need to create (Date or Time)
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.CANADA)
        return sdf.parse(dateTextView.text.toString()).time
    }

    private fun parseTime(): Long {
        // Choosing what data we are parsing based on the dialog we need to create (Date or Time)
        val sdf = SimpleDateFormat("HH:mm", Locale.CANADA)
        return sdf.parse(timeTextView.text.toString()).time
    }

    private fun parseDateTime(): Long {
        val date = dateTextView.text.toString()
        val time = timeTextView.text.toString()
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.CANADA)
        return sdf.parse("$date $time").time


    }

    private fun validateData(): Boolean {
        var flag = true
        if (titleTextView.text?.isBlank() == true) {
            flag = false
        }

        if (descriptionTextView.text?.isBlank() == true) {
            flag = false
        }

        if (dateTextView.text.isBlank()) {
            flag = false
        }
        if (timeTextView.text.isBlank()) {
            flag = false
        }
        if (locationTextView.text?.isBlank() == true) {
            flag = false
        }

        btnSave.isEnabled = flag
        val textColor = if (flag) {
            requireContext().resources.getColor(R.color.pastel_red)
        } else {
            requireContext().resources.getColor(R.color.dialog_positive_button_disabled_state)
        }
        btnSave.setTextColor(textColor)
        return flag
    }

    private fun addEventToCalendar(event: Events): Uri? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = event.date!!

        val startMillis = calendar.timeInMillis
        calendar.add(Calendar.HOUR_OF_DAY, 1)
        val endMillis = calendar.timeInMillis
        val contentValues = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, getDefaultCalendarId()) //Getting calendar ID
            put(CalendarContract.Events.TITLE, event.name)
            put(CalendarContract.Events.EVENT_LOCATION, event.location)
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.DTEND, endMillis)
            put(CalendarContract.Events.DESCRIPTION, event.description)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)


        }

        return requireContext().contentResolver.insert(
            CalendarContract.Events.CONTENT_URI,
            contentValues
        )
    }

    @SuppressLint("Range")
    private fun getDefaultCalendarId(): Long? {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
        )

        val selection = "${CalendarContract.Calendars.IS_PRIMARY} = ?"
        val selectionArgs = arrayOf("1") // 1 for true, indicating the primary calendar

        val cursor = requireContext().contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getLong(it.getColumnIndex(CalendarContract.Calendars._ID))
            }
        }

        return null
    }

}
