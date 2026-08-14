package com.example.serviceend

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.util.Calendar
import java.util.Locale
import kotlin.math.floor

class MainActivity : Activity() {

    private lateinit var startDateInput: EditText
    private lateinit var extraDeductionInput: EditText
    private lateinit var resultText: TextView

    private lateinit var localButton: Button
    private lateinit var nonLocalButton: Button

    private var deductionDaysPerMonth = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createUserInterface()
    }

    private fun createUserInterface() {

        val scrollView = ScrollView(this)

        val root = LinearLayout(this)

        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER_HORIZONTAL
        root.setPadding(32, 40, 32, 60)

        scrollView.addView(root)

        // عنوان
        val title = TextView(this)

        title.text = "محاسبه‌گر پایان خدمت"
        title.textSize = 27f
        title.gravity = Gravity.CENTER
        title.setPadding(0, 0, 0, 12)

        root.addView(title)

        // مدت پایه
        val subtitle = TextView(this)

        subtitle.text = "مدت پایه خدمت: ۲۱ ماه"
        subtitle.textSize = 17f
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, 0, 0, 20)

        root.addView(subtitle)

        // نوع خدمت
        val typeTitle = TextView(this)

        typeTitle.text = "نوع خدمت را انتخاب کنید:"
        typeTitle.textSize = 18f
        typeTitle.gravity = Gravity.RIGHT

        root.addView(typeTitle)

        val typeLayout = LinearLayout(this)

        typeLayout.orientation = LinearLayout.HORIZONTAL
        typeLayout.gravity = Gravity.CENTER
        typeLayout.setPadding(0, 10, 0, 20)

        localButton = Button(this)

        localButton.text = "بومی\n۵ روز"
        localButton.textSize = 16f
        localButton.isAllCaps = false

        nonLocalButton = Button(this)

        nonLocalButton.text = "غیربومی\n۱۲ روز"
        nonLocalButton.textSize = 16f
        nonLocalButton.isAllCaps = false

        typeLayout.addView(
            localButton,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        typeLayout.addView(
            nonLocalButton,
            LinearLayout.LayoutParams(0, -2, 1f)
        )

        root.addView(typeLayout)

        updateTypeButtons()

        localButton.setOnClickListener {

            deductionDaysPerMonth = 5

            updateTypeButtons()

            Toast.makeText(
                this,
                "بومی انتخاب شد: ۵ روز کسری به ازای هر ماه",
                Toast.LENGTH_SHORT
            ).show()
        }

        nonLocalButton.setOnClickListener {

            deductionDaysPerMonth = 12

            updateTypeButtons()

            Toast.makeText(
                this,
                "غیربومی انتخاب شد: ۱۲ روز کسری به ازای هر ماه",
                Toast.LENGTH_SHORT
            ).show()
        }

        // تاریخ شروع
        val dateTitle = TextView(this)

        dateTitle.text = "تاریخ شروع خدمت:"
        dateTitle.textSize = 18f
        dateTitle.gravity = Gravity.RIGHT
        dateTitle.setPadding(0, 10, 0, 8)

        root.addView(dateTitle)

        startDateInput = EditText(this)

        startDateInput.hint = "مثلاً ۱۴۰۵/۰۵/۲۳"
        startDateInput.textSize = 17f
        startDateInput.gravity = Gravity.CENTER
        startDateInput.inputType = InputType.TYPE_CLASS_NUMBER

        root.addView(
            startDateInput,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        // کسری اضافه
        val extraTitle = TextView(this)

        extraTitle.text = "کسری‌های اضافه:"
        extraTitle.textSize = 18f
        extraTitle.gravity = Gravity.RIGHT
        extraTitle.setPadding(0, 20, 0, 8)

        root.addView(extraTitle)

        val extraHint = TextView(this)

        extraHint.text =
            "مثلاً اگر ۶ ماه کسری اضافه دارید، عدد ۶ را وارد کنید."

        extraHint.textSize = 13f
        extraHint.gravity = Gravity.RIGHT

        root.addView(extraHint)

        extraDeductionInput = EditText(this)

        extraDeductionInput.hint = "تعداد ماه کسری اضافه"
        extraDeductionInput.inputType =
            InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL

        root.addView(extraDeductionInput)

        // دکمه محاسبه
        val calculateButton = Button(this)

        calculateButton.text = "محاسبه پایان خدمت"
        calculateButton.textSize = 18f
        calculateButton.isAllCaps = false

        calculateButton.setPadding(0, 20, 0, 20)

        val calculateParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        calculateParams.setMargins(0, 25, 0, 20)

        root.addView(
            calculateButton,
            calculateParams
        )

        calculateButton.setOnClickListener {

            calculateService()
        }

        // نتیجه
        resultText = TextView(this)

        resultText.textSize = 16f
        resultText.gravity = Gravity.RIGHT
        resultText.setPadding(0, 25, 0, 40)

        root.addView(resultText)

        setContentView(scrollView)
    }

    private fun updateTypeButtons() {

        if (deductionDaysPerMonth == 5) {

            localButton.setBackgroundColor(
                Color.rgb(76, 175, 80)
            )

            localButton.setTextColor(Color.WHITE)

            nonLocalButton.setBackgroundColor(
                Color.LTGRAY
            )

            nonLocalButton.setTextColor(Color.BLACK)

        } else {

            nonLocalButton.setBackgroundColor(
                Color.rgb(76, 175, 80)
            )

            nonLocalButton.setTextColor(Color.WHITE)

            localButton.setBackgroundColor(
                Color.LTGRAY
            )

            localButton.setTextColor(Color.BLACK)
        }
    }

    private fun calculateService() {

        val startDateText =
            startDateInput.text.toString().trim()

        if (startDateText.isEmpty()) {

            Toast.makeText(
                this,
                "لطفاً تاریخ شروع خدمت را وارد کنید.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val start =
            parseJalaliDate(startDateText)

        if (start == null) {

            Toast.makeText(
                this,
                "تاریخ صحیح نیست.\nمثال: ۱۴۰۵/۰۵/۲۳",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val extraMonthsText =
            extraDeductionInput.text
                .toString()
                .trim()

        val extraMonths =
            if (extraMonthsText.isEmpty()) {
                0.0
            } else {
                extraMonthsText.toDoubleOrNull()
            }

        if (extraMonths == null || extraMonths < 0) {

            Toast.makeText(
                this,
                "مقدار کسری واردشده معتبر نیست.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val baseServiceDays =
            21.0 * 30.0

        val extraDeductionDays =
            extraMonths * 30.0

        val rate =
            deductionDaysPerMonth.toDouble()

        if (extraDeductionDays > baseServiceDays) {

            Toast.makeText(
                this,
                "مقدار کسری اضافه از مدت پایه خدمت بیشتر است.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        /*
         * 30X + rateX + کسری اضافه = 630
         *
         * X = (630 - کسری اضافه) / (30 + rate)
         */

        val actualServiceMonths =
            (
                baseServiceDays -
                        extraDeductionDays
                ) / (
                30.0 + rate
                )

        val actualServiceDays =
            actualServiceMonths * 30.0

        val serviceDeductionDays =
            actualServiceMonths * rate

        val totalDeductionDays =
            serviceDeductionDays +
                    extraDeductionDays

        val finishDate =
            addDays(
                start,
                floor(actualServiceDays).toInt()
            )

        val serviceType =
            if (deductionDaysPerMonth == 5) {
                "بومی"
            } else {
                "غیربومی"
            }

        val result =
            StringBuilder()

        result.append(
            "━━━━━━━━━━━━━━━━━━\n"
        )

        result.append(
            "نتیجه محاسبه\n"
        )

        result.append(
            "━━━━━━━━━━━━━━━━━━\n\n"
        )

        result.append(
            "نوع خدمت: "
        )

        result.append(
            serviceType
        )

        result.append(
            "\n"
        )

        result.append(
            "کسری ماهانه: "
        )

        result.append(
            deductionDaysPerMonth
        )

        result.append(
            " روز\n\n"
        )

        result.append(
            "تاریخ شروع:\n"
        )

        result.append(
            formatJalali(start)
        )

        result.append(
            "\n\n"
        )

        result.append(
            "مدت پایه خدمت: ۲۱ ماه\n"
        )

        result.append(
            "۲۱ × ۳۰ = "
        )

        result.append(
            formatNumber(baseServiceDays)
        )

        result.append(
            " روز\n\n"
        )

        result.append(
            "کسری‌های اضافه:\n"
        )

        result.append(
            formatNumber(extraMonths)
        )

        result.append(
            " ماه × ۳۰ = "
        )

        result.append(
            formatNumber(extraDeductionDays)
        )

        result.append(
            " روز\n\n"
        )

        result.append(
            "فرمول مدت خدمت واقعی:\n"
        )

        result.append(
            "(۶۳۰ - "
        )

        result.append(
            formatNumber(extraDeductionDays)
        )

        result.append(
            ") ÷ (۳۰ + "
        )

        result.append(
            deductionDaysPerMonth
        )

        result.append(
            ")\n\n"
        )

        result.append(
            "مدت خدمت واقعی:\n"
        )

        result.append(
            formatNumber(actualServiceDays)
        )

        result.append(
            " روز\n"
        )

        result.append(
            "تقریباً "
        )

        result.append(
            formatNumber(actualServiceMonths)
        )

        result.append(
            " ماه\n\n"
        )

        result.append(
            "کسری ناشی از نوع خدمت:\n"
        )

        result.append(
            formatNumber(actualServiceMonths)
        )

        result.append(
            " × "
        )

        result.append(
            deductionDaysPerMonth
        )

        result.append(
            " = "
        )

        result.append(
            formatNumber(serviceDeductionDays)
        )

        result.append(
            " روز\n\n"
        )

        result.append(
            "مجموع کسری:\n"
        )

        result.append(
            formatNumber(totalDeductionDays)
        )

        result.append(
            " روز\n\n"
        )

        result.append(
            "━━━━━━━━━━━━━━━━━━\n"
        )

        result.append(
            "تاریخ پایان خدمت:\n"
        )

        result.append(
            formatJalali(finishDate)
        )

        result.append(
            "\n"
        )

        result.append(
            "━━━━━━━━━━━━━━━━━━\n\n"
        )

        result.append(
            "مبنای محاسبه: هر ماه = ۳۰ روز."
        )

        resultText.text =
            result.toString()

        // بعد از نمایش نتیجه، صفحه را به پایین می‌برد
        resultText.post {

            val scrollView =
                resultText.parent.parent as? ScrollView

            scrollView?.post {

                scrollView.fullScroll(
                    ScrollView.FOCUS_DOWN
                )
            }
        }
    }

    private fun parseJalaliDate(
        input: String
    ): Calendar? {

        try {

            val normalized =
                input
                    .replace(
                        '۰',
                        '0'
                    )
                    .replace(
                        '۱',
                        '1'
                    )
                    .replace(
                        '۲',
                        '2'
                    )
                    .replace(
                        '۳',
                        '3'
                    )
                    .replace(
                        '۴',
                        '4'
                    )
                    .replace(
                        '۵',
                        '5'
                    )
                    .replace(
                        '۶',
                        '6'
                    )
                    .replace(
                        '۷',
                        '7'
                    )
                    .replace(
                        '۸',
                        '8'
                    )
                    .replace(
                        '۹',
                        '9'
                    )
                    .replace(
                        '-',
                        '/'
                    )
                    .replace(
                        '.',
                        '/'
                    )

            val parts =
                normalized.split("/")

            if (parts.size != 3) {
                return null
            }

            val jy =
                parts[0].toInt()

            val jm =
                parts[1].toInt()

            val jd =
                parts[2].toInt()

            if (jy < 1300 || jy > 1500) {
                return null
            }

            if (jm !in 1..12) {
                return null
            }

            val maxDay =
                if (jm <= 6) {
                    31
                } else if (jm <= 11) {
                    30
                } else {
                    30
                }

            if (jd !in 1..maxDay) {
                return null
            }

            return jalaliToGregorian(
                jy,
                jm,
                jd
            )

        } catch (e: Exception) {

            return null
        }
    }

    private fun jalaliToGregorian(
        jy: Int,
        jm: Int,
        jd: Int
    ): Calendar {

        var jyTemp =
            jy - 979

        val jDayNo =
            365 * jyTemp +
                    (jyTemp / 33) * 8 +
                    ((jyTemp % 33) + 3) / 4

        var dayNo =
            jDayNo + 78

        var gy =
            1600 + 400 * (dayNo / 146097)

        dayNo %= 146097

        var leap = true

        if (dayNo >= 36525) {

            dayNo--

            gy +=
                100 * (dayNo / 36524)

            dayNo %=
                36524

            if (dayNo >= 365) {
                dayNo++
            } else {
                leap = false
            }
        }

        gy +=
            4 * (dayNo / 1461)

        dayNo %=
            1461

        if (dayNo >= 366) {

            leap = false

            dayNo--

            gy +=
                dayNo / 365

            dayNo %=
                365
        }

        val gd =
            dayNo + 1

        val gDaysInMonth =
            intArrayOf(
                31,
                if (leap) 29 else 28,
                31,
                30,
                31,
                30,
                31,
                31,
                30,
                31,
                30,
                31
            )

        var remaining =
            gd

        var gm = 0

        while (
            gm < 12 &&
            remaining >
            gDaysInMonth[gm]
        ) {

            remaining -=
                gDaysInMonth[gm]

            gm++
        }

        val baseCalendar =
            Calendar.getInstance()

        baseCalendar.clear()

        baseCalendar.set(
            gy,
            gm,
            remaining,
            0,
            0,
            0
        )

        baseCalendar.set(
            Calendar.MILLISECOND,
            0
        )

        /*
         * تبدیل بالا بر اساس روز شمار میلادی است.
         * برای اطمینان از تاریخ شمسی، از تبدیل استاندارد
         * روز شماره‌ای استفاده می‌کنیم.
         */

        val g =
            jalaliToGregorianAccurate(
                jy,
                jm,
                jd
            )

        return g
    }

    private fun jalaliToGregorianAccurate(
        jy: Int,
        jm: Int,
        jd: Int
    ): Calendar {

        val jy2 =
            jy - 979

        var jDayNo =
            365 * jy2 +
                    (jy2 / 33) * 8 +
                    ((jy2 % 33) + 3) / 4

        var i = 0

        while (i < jm - 1) {

            jDayNo +=
                if (i < 6) {
                    31
                } else {
                    30
                }

            i++
        }

        jDayNo +=
            jd - 1

        var gDayNo =
            jDayNo + 79

        var gy =
            1600 +
                    400 * (gDayNo / 146097)

        gDayNo %=
            146097

        var leap = true

        if (gDayNo >= 36525) {

            gDayNo--

            gy +=
                100 * (gDayNo / 36524)

            gDayNo %=
                36524

            if (gDayNo >= 365) {
                gDayNo++
            } else {
                leap = false
            }
        }

        gy +=
            4 * (gDayNo / 1461)

        gDayNo %=
            1461

        if (gDayNo >= 366) {

            leap = false

            gDayNo--

            gy +=
                gDayNo / 365

            gDayNo %=
                365
        }

        val gDays =
            intArrayOf(
                31,
                if (leap) 29 else 28,
                31,
                30,
                31,
                30,
                31,
                31,
                30,
                31,
                30,
                31
            )

        var gm = 0

        var gd =
            gDayNo + 1

        while (
            gm < 12 &&
            gd > gDays[gm]
        ) {

            gd -=
                gDays[gm]

            gm++
        }

        val calendar =
            Calendar.getInstance()

        calendar.clear()

        calendar.set(
            gy,
            gm,
            gd,
            0,
            0,
            0
        )

        calendar.set(
            Calendar.MILLISECOND,
            0
        )

        return calendar
    }

    private fun addDays(
        source: Calendar,
        days: Int
    ): Calendar {

        val result =
            Calendar.getInstance()

        result.timeInMillis =
            source.timeInMillis

        result.add(
            Calendar.DAY_OF_MONTH,
            days
        )

        return result
    }

    private fun formatNumber(
        number: Double
    ): String {

        val rounded =
            String.format(
                Locale.US,
                "%.2f",
                number
            )

        return rounded
            .replace(
                ".00",
                ""
            )
            .replace(
                '.',
                '/'
            )
    }

    private fun formatJalali(
        calendar: Calendar
    ): String {

        val gy =
            calendar.get(
                Calendar.YEAR
            )

        val gm =
            calendar.get(
                Calendar.MONTH
            ) + 1

        val gd =
            calendar.get(
                Calendar.DAY_OF_MONTH
            )

        val result =
            gregorianToJalali(
                gy,
                gm,
                gd
            )

        val year =
            result[0].toString()

        val month =
            result[1]
                .toString()
                .padStart(
                    2,
                    '0'
                )

        val day =
            result[2]
                .toString()
                .padStart(
                    2,
                    '0'
                )

        return toPersianDigits(
            "$year/$month/$day"
        )
    }

    private fun toPersianDigits(
        value: String
    ): String {

        val english =
            "0123456789"

        val persian =
            "۰۱۲۳۴۵۶۷۸۹"

        return value.map { character ->

            val index =
                english.indexOf(character)

            if (index >= 0) {
                persian[index]
            } else {
                character
            }

        }.joinToString("")
    }

    private fun gregorianToJalali(
        gy: Int,
        gm: Int,
        gd: Int
    ): IntArray {

        val gDaysInMonth =
            intArrayOf(
                31,
                28,
                31,
                30,
                31,
                30,
                31,
                31,
                30,
                31,
                30,
                31
            )

        val jDaysInMonth =
            intArrayOf(
                31,
                31,
                31,
                31,
                31,
                31,
                30,
                30,
                30,
                30,
                30,
                29
            )

        val gyTemp =
            gy - 1600

        val gmTemp =
            gm - 1

        val gdTemp =
            gd - 1

        var gDayNo =
            365 * gyTemp +
                    (gyTemp + 3) / 4 -
                    (gyTemp + 99) / 100 +
                    (gyTemp + 399) / 400

        var i = 0

        while (i < gmTemp) {

            gDayNo +=
                gDaysInMonth[i]

            i++
        }

        if (
            gmTemp > 1 &&
            isGregorianLeap(gy)
        ) {

            gDayNo++
        }

        gDayNo +=
            gdTemp

        var jDayNo =
            gDayNo - 79

        val jNp =
            jDayNo / 12053

        jDayNo %=
            12053

        var jy =
            979 +
                    33 * jNp +
                    4 * (jDayNo / 1461)

        jDayNo %=
            1461

        if (jDayNo >= 366) {

            jy +=
                (jDayNo - 1) / 365

            jDayNo =
                (jDayNo - 1) % 365
        }

        var jm = 0

        while (
            jm < 11 &&
            jDayNo >=
            jDaysInMonth[jm]
        ) {

            jDayNo -=
                jDaysInMonth[jm]

            jm++
        }

        val jd =
            jDayNo + 1

        return intArrayOf(
            jy,
            jm + 1,
            jd
        )
    }

    private fun isGregorianLeap(
        year: Int
    ): Boolean {

        return (
                year % 4 == 0 &&
                        (
                                year % 100 != 0 ||
                                        year % 400 == 0
                                )
                )
    }
}

