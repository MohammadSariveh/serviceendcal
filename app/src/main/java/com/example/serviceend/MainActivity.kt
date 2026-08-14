package com.example.serviceend

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.util.Calendar
import java.util.Locale
import kotlin.math.floor

class MainActivity : Activity() {

    private lateinit var startDateText: TextView
    private lateinit var extraDeductionInput: EditText
    private lateinit var resultText: TextView

    private lateinit var localButton: Button
    private lateinit var nonLocalButton: Button

    private var selectedStartDate: Calendar? = null

    // بومی = ۵ روز کسری به ازای هر ماه
    // غیربومی = ۱۲ روز کسری به ازای هر ماه
    private var deductionDaysPerMonth = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createUserInterface()
    }

    private fun createUserInterface() {

        val root = LinearLayout(this)

        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER_HORIZONTAL
        root.setPadding(32, 40, 32, 40)

        val title = TextView(this)

        title.text = "محاسبه‌گر پایان خدمت"
        title.textSize = 27f
        title.gravity = Gravity.CENTER
        title.setPadding(0, 0, 0, 12)

        root.addView(title)

        val subtitle = TextView(this)

        subtitle.text = "مدت پایه خدمت: ۲۱ ماه"
        subtitle.textSize = 17f
        subtitle.gravity = Gravity.CENTER
        subtitle.setPadding(0, 0, 0, 15)

        root.addView(subtitle)

        val typeTitle = TextView(this)

        typeTitle.text = "نوع خدمت را انتخاب کنید:"
        typeTitle.textSize = 18f
        typeTitle.gravity = Gravity.RIGHT

        root.addView(typeTitle)

        val typeLayout = LinearLayout(this)

        typeLayout.orientation = LinearLayout.HORIZONTAL
        typeLayout.gravity = Gravity.CENTER
        typeLayout.setPadding(0, 8, 0, 15)

        localButton = Button(this)

        localButton.text = "بومی\n۵ روز"
        localButton.isAllCaps = false

        nonLocalButton = Button(this)

        nonLocalButton.text = "غیربومی\n۱۲ روز"
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

        localButton.setOnClickListener {

            deductionDaysPerMonth = 5

            Toast.makeText(
                this,
                "خدمت بومی انتخاب شد؛ ۵ روز کسری به ازای هر ماه",
                Toast.LENGTH_SHORT
            ).show()
        }

        nonLocalButton.setOnClickListener {

            deductionDaysPerMonth = 12

            Toast.makeText(
                this,
                "خدمت غیربومی انتخاب شد؛ ۱۲ روز کسری به ازای هر ماه",
                Toast.LENGTH_SHORT
            ).show()
        }

        val dateTitle = TextView(this)

        dateTitle.text = "تاریخ شروع خدمت:"
        dateTitle.textSize = 18f
        dateTitle.gravity = Gravity.RIGHT
        dateTitle.setPadding(0, 10, 0, 8)

        root.addView(dateTitle)

        startDateText = TextView(this)

        startDateText.text = "برای انتخاب تاریخ کلیک کنید"
        startDateText.textSize = 17f
        startDateText.gravity = Gravity.CENTER
        startDateText.setPadding(20, 22, 20, 22)

        startDateText.setOnClickListener {

            showDatePicker()
        }

        root.addView(startDateText)

        val extraTitle = TextView(this)

        extraTitle.text = "کسری‌های اضافه:"
        extraTitle.textSize = 18f
        extraTitle.gravity = Gravity.RIGHT
        extraTitle.setPadding(0, 18, 0, 8)

        root.addView(extraTitle)

        val extraHint = TextView(this)

        extraHint.text = "مثلاً اگر ۶ ماه کسری اضافه دارید، عدد ۶ را وارد کنید."
        extraHint.textSize = 13f
        extraHint.gravity = Gravity.RIGHT

        root.addView(extraHint)

        extraDeductionInput = EditText(this)

        extraDeductionInput.hint = "تعداد ماه کسری اضافه"
        extraDeductionInput.inputType =
            InputType.TYPE_CLASS_NUMBER or
                    InputType.TYPE_NUMBER_FLAG_DECIMAL

        root.addView(extraDeductionInput)

        val calculateButton = Button(this)

        calculateButton.text = "محاسبه پایان خدمت"
        calculateButton.textSize = 18f
        calculateButton.isAllCaps = false
        calculateButton.setPadding(0, 15, 0, 15)

        calculateButton.setOnClickListener {

            calculateService()
        }

        root.addView(calculateButton)

        resultText = TextView(this)

        resultText.textSize = 16f
        resultText.gravity = Gravity.RIGHT
        resultText.setPadding(0, 25, 0, 10)

        root.addView(resultText)

        setContentView(root)
    }

    private fun showDatePicker() {

        val today = Calendar.getInstance()

        val initial =
            selectedStartDate ?: today

        val dialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->

                selectedStartDate =
                    Calendar.getInstance()

                selectedStartDate!!.set(
                    year,
                    month,
                    dayOfMonth,
                    0,
                    0,
                    0
                )

                selectedStartDate!!.set(
                    Calendar.MILLISECOND,
                    0
                )

                startDateText.text =
                    formatJalali(selectedStartDate!!)
            },
            initial.get(Calendar.YEAR),
            initial.get(Calendar.MONTH),
            initial.get(Calendar.DAY_OF_MONTH)
        )

        dialog.show()
    }

    private fun calculateService() {

        val start =
            selectedStartDate

        if (start == null) {

            Toast.makeText(
                this,
                "لطفاً ابتدا تاریخ شروع خدمت را انتخاب کنید.",
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

        /*
         * محاسبه:
         *
         * مدت پایه خدمت = ۲۱ ماه
         * هر ماه = ۳۰ روز
         *
         * بومی:
         * ۵ روز کسری برای هر ماه خدمت
         *
         * غیربومی:
         * ۱۲ روز کسری برای هر ماه خدمت
         *
         * کسری اضافه:
         * هر ماه = ۳۰ روز
         *
         * اگر X مدت واقعی خدمت بر حسب ماه باشد:
         *
         * ۳۰X + rateX + کسری اضافه = ۶۳۰
         *
         * بنابراین:
         *
         * X = (۶۳۰ - کسری اضافه) / (۳۰ + rate)
         */

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
            "مدت پایه خدمت: ۲۱ ماه\n"
        )

        result.append(
            "کسری ماهانه: "
        )

        result.append(
            deductionDaysPerMonth
        )

        result.append(
            " روز به ازای هر ماه\n\n"
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
            "محاسبات:\n"
        )

        result.append(
            "──────────────────\n"
        )

        result.append(
            "مدت پایه:\n"
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
                english.indexOf(
                    character
                )

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
                31, 28, 31, 30,
                31, 30, 31, 31,
                30, 31, 30, 31
            )

        val jDaysInMonth =
            intArrayOf(
                31, 31, 31, 31,
                31, 31, 30, 30,
                30, 30, 30, 29
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
                    4 * (
                    jDayNo / 1461
                    )

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

