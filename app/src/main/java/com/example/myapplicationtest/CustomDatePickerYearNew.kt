package com.example.myapplicationtest

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.res.ResourcesCompat
import com.imfarrik.customdatepicker.databinding.CustomNewCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

class CustomDatePickerYearNew
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private companion object {
        const val CUSTOM_GREY = "#a0a0a0"
        const val ALL_WEEKS = 6
        const val ALL_DAYS = 6 * 7
        const val LAST_MONTH = 11
    }

    private var calendar = Calendar.getInstance()

    private var weeks: Array<LinearLayout?> = arrayOfNulls(ALL_WEEKS)
    private var days: Array<Button?> = arrayOfNulls(ALL_DAYS)

    private val defaultButtonParams: LayoutParams?
    private var userButtonParams: LayoutParams? = null

    private var mListener: DayClickListener? = null

    private var selectedDayButton: Button? = null

    private var isFirstClick = true

    private var currentDateDay = 0
    private var chosenDateDay = 0
    private var currentDateMonth = 0
    private var chosenDateMonth = 0
    private var currentDateYear = 0
    private var chosenDateYear = 0
    private var pickedDateDay = 0
    private var pickedDateMonth = 0
    private var pickedDateYear = 0

    var fullDate = "$pickedDateDay-$pickedDateMonth-$pickedDateYear"

    private val binding = CustomNewCalendarBinding.inflate(LayoutInflater.from(context))

    init {

        addView(binding.root)

        currentDateDay = calendar.get(Calendar.DAY_OF_MONTH)

        chosenDateMonth = calendar[Calendar.MONTH]
        currentDateMonth = chosenDateMonth
        chosenDateYear = calendar[Calendar.YEAR]
        currentDateYear = chosenDateYear
        fullDate = "$currentDateDay-${currentDateMonth + 1}-$chosenDateYear"

        binding.currentDate.text = formatDate(fullDate)
        binding.currentMonth.text = currentDateYear.toString()

        initializeDaysWeeks()

        defaultButtonParams = userButtonParams ?: getDaysLayoutParams()

        addDaysInCalendar(defaultButtonParams, context)

        initCalendarWithDate(chosenDateYear, chosenDateMonth, chosenDateDay)

        binding.next.setOnClickListener {
            getNextMonth()
        }

        binding.pre.setOnClickListener {
            getPreMonth()
        }

    }

    private fun getPreMonth() {
        calendar.add(Calendar.MONTH, -1)

        binding.currentDate.text =
            "${calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())},"
        binding.currentMonth.text = calendar.get(Calendar.YEAR).toString()

        initCalendarWithDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun getNextMonth() {
        if (isFirstClick) {
            isFirstClick = false
            calendar.add(Calendar.MONTH, +2)
        } else {
            calendar.add(Calendar.MONTH, +1)
        }

        binding.currentDate.text =
            "${calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())},"
        binding.currentMonth.text = calendar.get(Calendar.YEAR).toString()

        initCalendarWithDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun initializeDaysWeeks() {
        val listOfWeeks = arrayOf(
            binding.calendarWeek1,
            binding.calendarWeek2,
            binding.calendarWeek3,
            binding.calendarWeek4,
            binding.calendarWeek5,
            binding.calendarWeek6
        )
        for (i in listOfWeeks.indices) {
            weeks[i] = listOfWeeks[i]
        }
    }

    private fun getDaysLayoutParams(): LayoutParams {
        val buttonParams = LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        buttonParams.weight = 1f
        return buttonParams
    }

    private fun addDaysInCalendar(
        buttonParams: LayoutParams, context: Context,
    ) {
        var engDaysArrayCounter = 0
        for (weekNumber in 0..5) {
            for (dayInWeek in 0..6) {
                val day = Button(context)
                day.setTextColor(Color.parseColor(CUSTOM_GREY))
                day.setBackgroundColor(Color.TRANSPARENT)
                day.layoutParams = buttonParams
                day.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                day.typeface = ResourcesCompat.getFont(context, R.font.muller_regular)
                day.setSingleLine()
                days[engDaysArrayCounter] = day
                weeks[weekNumber]!!.addView(day)
                ++engDaysArrayCounter
            }
        }
    }

    private fun initCalendarWithDate(year: Int, month: Int, day: Int) {

        val daysInCurrentMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        chosenDateYear = year
        chosenDateMonth = month
        chosenDateDay = day

        calendar.set(year, month, 1)

        val firstDayOfCurrentMonth = calendar.get(Calendar.DAY_OF_WEEK)

        var dayNumber = 1
        val daysLeftInFirstWeek: Int
        val indexOfDayAfterLastDayOfMonth: Int

        if (firstDayOfCurrentMonth == 1) {
            daysLeftInFirstWeek = firstDayOfCurrentMonth + 5
            indexOfDayAfterLastDayOfMonth = daysLeftInFirstWeek + daysInCurrentMonth

            for (i in firstDayOfCurrentMonth + 5..daysInCurrentMonth + 5) {
                if (currentDateMonth == chosenDateMonth && currentDateYear == chosenDateYear && dayNumber == currentDateDay) {
                    days[i]!!.setTextColor(Color.RED)
                } else {
                    days[i]!!.setTextColor(Color.BLACK)
                    days[i]!!.setBackgroundColor(Color.TRANSPARENT)
                }
                val dateArr = IntArray(3)
                dateArr[0] = dayNumber
                dateArr[1] = chosenDateMonth
                dateArr[2] = chosenDateYear
                days[i]!!.tag = dateArr
                days[i]!!.text = dayNumber.toString()
                days[i]!!.setOnClickListener { view: View? ->
                    onDayClick(
                        view!!
                    )
                }
                ++dayNumber
            }

        } else {
            daysLeftInFirstWeek = firstDayOfCurrentMonth - 2
            indexOfDayAfterLastDayOfMonth = daysLeftInFirstWeek + daysInCurrentMonth
            for (i in firstDayOfCurrentMonth - 2..daysInCurrentMonth + (firstDayOfCurrentMonth - 3)) {
                if (currentDateMonth == chosenDateMonth && currentDateYear == chosenDateYear && dayNumber == currentDateDay) {
                    days[i]!!.setTextColor(Color.RED)
                } else {
                    days[i]!!.setTextColor(Color.BLACK)
                    days[i]!!.setBackgroundColor(Color.TRANSPARENT)
                }
                val dateArr = IntArray(3)
                dateArr[0] = dayNumber
                dateArr[1] = chosenDateMonth
                dateArr[2] = chosenDateYear
                days[i]!!.tag = dateArr
                days[i]!!.text = dayNumber.toString()
                days[i]!!.setOnClickListener { view: View? ->
                    onDayClick(
                        view!!
                    )
                }
                ++dayNumber
            }
        }

        if (month > 0) {
            calendar.set(year, month - 1, 1)
        } else {
            calendar.set(year - 1, 11, 1)
        }
        var daysInPreviousMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in daysLeftInFirstWeek - 1 downTo 0) {
//            val dateArr = IntArray(3)
//            if (chosenDateMonth > 0) {
//                if (currentDateMonth != chosenDateMonth - 1 || currentDateYear != chosenDateYear || daysInPreviousMonth != currentDateDay) {
//                    days[i]!!.setBackgroundColor(Color.TRANSPARENT)
//                }
//                val listOfInt = listOf(daysInPreviousMonth, chosenDateMonth - 1, chosenDateYear)
//                for (j in dateArr.indices) {
//                    dateArr[j] = listOfInt[j]
//                }
//            } else {
//                if (currentDateMonth != LAST_MONTH || currentDateYear != chosenDateYear - 1 || daysInPreviousMonth != currentDateDay) {
//                    days[i]!!.setBackgroundColor(Color.TRANSPARENT)
//                }
//                val listOfInt = listOf(daysInPreviousMonth, LAST_MONTH, chosenDateYear - 1)
//                for (j in dateArr.indices) {
//                    dateArr[j] = listOfInt[j]
//                }
//            }
            days[i]!!.apply {
//                tag = dateArr
                setTextColor(Color.parseColor(CUSTOM_GREY))
                text = daysInPreviousMonth--.toString()
                setOnClickListener { getPreMonth() }
            }
        }

        var nextMonthDaysCounter = 1
        for (i in indexOfDayAfterLastDayOfMonth until days.size) {
            val dateArr = IntArray(3)
            if (chosenDateMonth < 11) {
                if (currentDateMonth == chosenDateMonth + 1 && currentDateYear == chosenDateYear && nextMonthDaysCounter == currentDateDay) {
                    days[i]!!.setBackgroundResource(R.drawable.btn_bg)
                } else {
                    days[i]!!.setBackgroundColor(Color.TRANSPARENT)
                }
                dateArr[0] = nextMonthDaysCounter
                dateArr[1] = chosenDateMonth + 1
                dateArr[2] = chosenDateYear
            } else {
                if (currentDateMonth == 0 && currentDateYear == chosenDateYear + 1 && nextMonthDaysCounter == currentDateDay) {
                    days[i]!!.setBackgroundResource(R.drawable.btn_bg)
                } else {
                    days[i]!!.setBackgroundColor(Color.TRANSPARENT)
                }
                dateArr[0] = nextMonthDaysCounter
                dateArr[1] = 0
                dateArr[2] = chosenDateYear + 1
            }
            days[i]!!.tag = dateArr
            days[i]!!.setTextColor(Color.parseColor(CUSTOM_GREY))
            days[i]!!.text = nextMonthDaysCounter++.toString()
            days[i]!!.setOnClickListener { getNextMonth() }
        }

        calendar[chosenDateYear, chosenDateMonth] = chosenDateDay

    }

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM,", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }

    private fun onDayClick(view: View) {
        mListener?.onDayClick(view)
        if (selectedDayButton != null) {
            if (chosenDateYear == currentDateYear && chosenDateMonth == currentDateMonth && pickedDateDay == currentDateDay) {
                selectedDayButton!!.setBackgroundColor(Color.TRANSPARENT)
                selectedDayButton!!.setTextColor(Color.RED)
            } else {
                selectedDayButton!!.setBackgroundColor(Color.TRANSPARENT)
                if (selectedDayButton!!.currentTextColor != Color.RED) {
                    selectedDayButton!!.setTextColor(Color.BLACK)
                }
            }
        }
        selectedDayButton = view as Button
        if (selectedDayButton?.tag != null) {
            val dateArray = selectedDayButton!!.tag as IntArray
            pickedDateDay = dateArray[0]
            pickedDateMonth = dateArray[1] + 1
            pickedDateYear = dateArray[2]
            fullDate = "$pickedDateDay-$pickedDateMonth-$pickedDateYear"
            binding.currentDate.text = formatDate(fullDate)
        }
        selectedDayButton!!.setBackgroundResource(R.drawable.btn_bg)
        selectedDayButton!!.setTextColor(Color.WHITE)
    }

    interface DayClickListener {
        fun onDayClick(view: View?)
    }


}