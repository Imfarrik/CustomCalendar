package com.imfarrik.customdatepicker

import android.annotation.SuppressLint
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

class CustomDatePickerYear
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private companion object {
        const val CUSTOM_GREY = "#a0a0a0"
        const val ALL_WEEKS = 6
        const val ALL_DAYS = 6 * 7
        const val FIRST_DAY_MONTH = 1
        const val FIVE_DAYS = 5
        const val TWO_DAYS = 2
        const val THREE_DAYS = 3
    }

    private var calendar = Calendar.getInstance()

    private var weeks: Array<LinearLayout?> = arrayOfNulls(ALL_WEEKS)
    private var days: Array<Button?> = arrayOfNulls(ALL_DAYS)

    private val defaultButtonParams: LayoutParams?
    private var userButtonParams: LayoutParams? = null

    private var mListener: DayClickListener? = null

    private var selectedDayButton: Button? = null

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

        initCalendarWithDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

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

        calendar.add(Calendar.MONTH, +1)

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

        calendar.set(year, month, FIRST_DAY_MONTH)

        val firstDayOfCurrentMonth = calendar.get(Calendar.DAY_OF_WEEK)

        var dayNumber = 1
        val daysLeftInFirstWeek: Int
        val indexOfDayAfterLastDayOfMonth: Int

        if (firstDayOfCurrentMonth == Calendar.SUNDAY) {
            daysLeftInFirstWeek = firstDayOfCurrentMonth + FIVE_DAYS
            indexOfDayAfterLastDayOfMonth = daysLeftInFirstWeek + daysInCurrentMonth

            for (i in firstDayOfCurrentMonth + FIVE_DAYS..daysInCurrentMonth + FIVE_DAYS) {
                if (currentDateMonth == chosenDateMonth && currentDateYear == chosenDateYear && dayNumber == currentDateDay) {
                    days[i]!!.setTextColor(Color.RED)
                } else {
                    days[i]!!.apply {
                        setTextColor(Color.BLACK)
                        setBackgroundColor(Color.TRANSPARENT)
                    }
                }
                val dateArr = listOf(dayNumber, chosenDateMonth, chosenDateYear)
                days[i]!!.apply {
                    tag = dateArr
                    text = dayNumber.toString()
                    setOnClickListener { onDayClick(it) }
                }

                ++dayNumber
            }

        } else {
            daysLeftInFirstWeek = firstDayOfCurrentMonth - TWO_DAYS
            indexOfDayAfterLastDayOfMonth = daysLeftInFirstWeek + daysInCurrentMonth
            for (i in firstDayOfCurrentMonth - TWO_DAYS..daysInCurrentMonth + (firstDayOfCurrentMonth - THREE_DAYS)) {
                if (currentDateMonth == chosenDateMonth && currentDateYear == chosenDateYear && dayNumber == currentDateDay) {
                    days[i]!!.setTextColor(Color.RED)
                } else {
                    days[i]!!.apply {
                        setTextColor(Color.BLACK)
                        setBackgroundColor(Color.TRANSPARENT)
                    }
                }
                val dateArr = listOf(dayNumber, chosenDateMonth, chosenDateYear)
                days[i]!!.apply {
                    tag = dateArr
                    text = dayNumber.toString()
                    setOnClickListener { onDayClick(it) }
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
            days[i]!!.apply {
                setTextColor(Color.parseColor(CUSTOM_GREY))
                text = daysInPreviousMonth--.toString()
                setOnClickListener { getPreMonth() }
            }
        }

        var nextMonthDaysCounter = 1

        for (i in indexOfDayAfterLastDayOfMonth until days.size) {
            days[i]!!.apply {
                setTextColor(Color.parseColor(CUSTOM_GREY))
                text = nextMonthDaysCounter++.toString()
                setOnClickListener { getNextMonth() }
            }

        }

        calendar.set(chosenDateYear, chosenDateMonth, chosenDateDay)

    }

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM,", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }

    @Suppress("UNCHECKED_CAST")
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
            val dateArray = selectedDayButton!!.tag as List<Int>
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