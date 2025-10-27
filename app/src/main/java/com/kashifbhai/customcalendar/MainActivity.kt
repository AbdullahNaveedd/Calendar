package com.kashifbhai.customcalendar

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.kashifbhai.customcalendar.databinding.ActivityMainBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var startDate: LocalDate? = null
    private var endDate: LocalDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupCalendar()
        setupQuickButtons()
        setupConfirmButton()

        binding.btnPrevMonth.setOnClickListener {
            binding.calendarView.findFirstVisibleMonth()?.let {
                binding.calendarView.smoothScrollToMonth(it.yearMonth.minusMonths(1))
            }
        }
        binding.btnNextMonth.setOnClickListener {
            binding.calendarView.findFirstVisibleMonth()?.let {
                binding.calendarView.smoothScrollToMonth(it.yearMonth.plusMonths(1))
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupCalendar() {
        val calendarView = binding.calendarView
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(12)
        val endMonth = currentMonth.plusMonths(12)
        val firstDayOfWeek = DayOfWeek.MONDAY
        calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)
        updateMonthYearText(currentMonth)
        calendarView.monthScrollListener = { month ->
            updateMonthYearText(month.yearMonth)
        }
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.textView
                textView.text = data.date.dayOfMonth.toString()
                textView.setBackgroundResource(0)
                textView.setTextColor(Color.parseColor("#FFFFFF"))
                textView.alpha = 1f

                if (data.date.dayOfWeek == DayOfWeek.SATURDAY ||
                    data.date.dayOfWeek == DayOfWeek.SUNDAY) {
                    textView.setTextColor(Color.parseColor("#A0A0A0"))
                }
                when (data.position) {
                    DayPosition.MonthDate -> {
                        textView.visibility = View.VISIBLE
                        if (data.date > LocalDate.now()) {
                            textView.alpha = 0.3f
                            textView.setTextColor(Color.parseColor("#666666"))
                            textView.isEnabled = false
                        } else {
                            textView.isEnabled = true
                            when {
                                startDate == data.date && endDate == data.date -> {
                                    textView.setBackgroundResource(R.drawable.bg_date_selected_single)
                                    textView.setTextColor(Color.WHITE)
                                }
                                startDate == data.date -> {
                                    textView.setBackgroundResource(R.drawable.bg_date_selected_start)
                                    textView.setTextColor(Color.WHITE)

                                }
                                endDate == data.date -> {
                                    textView.setBackgroundResource(R.drawable.bg_date_selected_end)
                                    textView.setTextColor(Color.WHITE)
                                }
                                startDate != null && endDate != null &&
                                        data.date > startDate && data.date < endDate -> {
                                    textView.setBackgroundResource(R.drawable.bg_date_in_range)
                                    textView.setTextColor(Color.parseColor("#000000"))
                                    if (data.date.dayOfWeek == DayOfWeek.SATURDAY ||
                                        data.date.dayOfWeek == DayOfWeek.SUNDAY) {
                                        textView.setTextColor(Color.parseColor("#A0A0A0"))
                                    }
                                }
                                data.date == LocalDate.now() -> {
                                    textView.setBackgroundResource(R.drawable.bg_date_today)
                                    textView.setTextColor(Color.parseColor("#4A9EFF"))
                                }
                            }
                            textView.setOnClickListener {
                                handleDateClick(data.date)
                            }
                        }
                    }
                    DayPosition.InDate, DayPosition.OutDate -> {
                        textView.visibility = View.INVISIBLE
                    }
                }
            }
        }

        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                if (container.titlesContainer.tag == null) {
                    container.titlesContainer.tag = data.yearMonth
                    val children = (0 until container.titlesContainer.childCount)
                        .map { container.titlesContainer.getChildAt(it) as TextView }
                    val customDays = listOf("Mo", "Tu", "We", "Th", "Fr", "Sa", "Su")
                    children.forEachIndexed { index, textView ->
                        textView.text = customDays[index]
                        textView.setTextColor(Color.parseColor("#999999"))
                        textView.textSize = 12f
                    }
                }
            }
        }
    }

    private fun updateMonthYearText(yearMonth: YearMonth) {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        binding.tvMonthYear.text = yearMonth.format(formatter)
    }

    private fun handleDateClick(date: LocalDate) {
        when {
            startDate == null -> {
                startDate = date
                endDate = date
            }
            startDate != null && endDate == startDate -> {
                if (date < startDate!!) {
                    endDate = startDate
                    startDate = date
                } else {
                    endDate = date
                }
            }
            else -> {
                startDate = date
                endDate = date
            }
        }
        binding.calendarView.notifyCalendarChanged()
        updateCustomRangeText()
    }

    private fun setupQuickButtons() {
        binding.btnLast7.setOnClickListener {
            selectLastDays(7)
            highlightButton(binding.btnLast7)
        }

        binding.btnLast30.setOnClickListener {
            selectLastDays(30)
            highlightButton(binding.btnLast30)
        }

        binding.btnLast3Months.setOnClickListener {
            selectLastDays(90)
            highlightButton(binding.btnLast3Months)
        }
    }

    private fun selectLastDays(days: Int) {
        endDate = LocalDate.now()
        startDate = endDate!!.minusDays(days.toLong() - 1)
        binding.calendarView.notifyCalendarChanged()
        updateCustomRangeText()
    }

    private fun highlightButton(selectedButton: View) {
        listOf(binding.btnLast7, binding.btnLast30, binding.btnLast3Months).forEach {
            it.setBackgroundResource(R.drawable.bg_quick_button_normal)
        }
        selectedButton.setBackgroundResource(R.drawable.bg_quick_button_selected)
    }

    private fun updateCustomRangeText() {
        if (startDate != null && endDate != null) {
            val formatter = DateTimeFormatter.ofPattern("MMM dd")
            binding.tvCustomRange.text = "${startDate!!.format(formatter)}"
            binding.tvCustomRangeEnd.text = "${endDate!!.format(formatter)}"
        }
    }

    private fun setupConfirmButton() {
        binding.btnConfirm.setOnClickListener {
            if (startDate != null && endDate != null) {
                val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
                val days = ChronoUnit.DAYS.between(startDate, endDate) + 1
                val message = "Selected: ${startDate!!.format(formatter)} to ${endDate!!.format(formatter)}\n($days days)"

                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                val intent = Intent (this, Swiper::class.java)
                startActivity(intent)

            } else {
                Toast.makeText(this, "Please select date range", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val textView: TextView = view.findViewById(R.id.calendarDayText)
        lateinit var day: CalendarDay
    }

    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val titlesContainer = view as android.view.ViewGroup
    }
}