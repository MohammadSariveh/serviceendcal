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
        subtitle.setPadding(0, 0, 0, 20)
        root.addView(subtitle)

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
        startDateInput.inputType = InputType.TYPE_CLASS_TEXT

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
            "مثال: ۵ ماه و ۱۲ روز  یا  ۵/۱۲  یا  ۱۲ روز"
        extraHint.textSize = 13f
        extraHint.gravity = Gravity.RIGHT
        root.addView(extraHint)

        extraDeductionInput = EditText(this)
        extraDeductionInput.hint = "مثلاً ۵ ماه و ۱۲ روز"
        extraDeductionInput.inputType = InputType.TYPE_CLASS_TEXT

        root.addView(extraDeductionInput)

        // دکمه محاسبه
        val calculateButton = Button(this)
        calculateButton.text = "محاسبه پایان خدمت"
        calculateButton.textSize = 18f
        calculateButton.isAllCaps = false
        calculateButton.setPadding(0, 20, 0, 20)

        val calculateParams = LinearLayout.LayoutParams(
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

    // =====================================================
    // محاسبه اصلی
    // =====================================================

    private fun calculateService() {

        val startText =
            startDateInput.text.toString().trim()

        if (startText.isEmpty()) {

            Toast.makeText(
                this,
                "لطفاً تاریخ شروع خدمت را وارد کنید.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val start =
            parseJalaliDate(startText)

        if (start == null) {

            Toast.makeText(
                this,
                "تاریخ صحیح نیست.\nمثال: ۱۴۰۵/۰۵/۲۳",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        // کسری اضافه
        val extraText =
            extraDeductionInput.text.toString().trim()

        val extra =
            parseExtraDeduction(extraText)

        if (extra == null) {

            Toast.makeText(
                this,
                "مقدار کسری صحیح نیست.\nمثال: ۵ ماه و ۱۲ روز",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val extraMonths = extra.first
        val extraDays = extra.second

        // -------------------------------------------------
        // پایان پایه:
        // دقیقاً ۲۱ ماه تقویمی بعد از شروع
        // -------------------------------------------------

        val baseFinish =
            addJalaliMonthsAndDays(
                start,
                21,
                0
            )

        // -------------------------------------------------
        // ابتدا کسری اضافه را اعمال می‌کنیم
        // -------------------------------------------------

        var finish =
            subtractJalaliMonthsAndDays(
                baseFinish,
                extraMonths,
                extraDays
            )

        // -------------------------------------------------
        // کسری ماهانه
        //
        // تعداد ماه‌ها = تعداد ماه‌های تقویمی گذشته
        // هر ماه فقط یک واحد محسوب می‌شود.
        //
        // مثال:
        // ۱۰ ماه × ۵ روز = ۵۰ روز
        // -------------------------------------------------

        var previousFinish: Calendar? = null

        repeat(100) {

            val monthsServed =
                completedCalendarMonths(
                    start,
                    finish
                )

            val monthlyDeduction =
                monthsServed *
                        deductionDaysPerMonth

            val newFinish =
                subtractDays(
                    finish,
                    monthlyDeduction
                )

            if (
                previousFinish != null &&
                        newFinish.timeInMillis ==
                        previousFinish!!.timeInMillis
            ) {
                finish = newFinish
                return@repeat
            }

            previousFinish =
                finish.clone() as Calendar

            finish =
                newFinish
        }

        // تعداد ماه‌های واقعی خدمت
        val actualMonths =
            completedCalendarMonths(
                start,
                finish
            )

        val monthlyDeduction =
            actualMonths *
                    deductionDaysPerMonth

        val totalExtraDays =
            daysBetween(
                subtractJalaliMonthsAndDays(
                    baseFinish,
                    extraMonths,
                    extraDays
                ),
                baseFinish
            )

        val totalDeduction =
            monthlyDeduction +
                    totalExtraDays

        val actualCalendarDays =
            daysBetween(
                start,
                finish
            )

        val serviceType =
            if (deductionDaysPerMonth == 5) {
                "بومی"
            } else {
                "غیربومی"
            }

        // =================================================
        // نتیجه
        // =================================================

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

        result.append("\n")

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

        result.append("\n\n")

        result.append(
            "مدت پایه خدمت:\n"
        )

        result.append(
            "۲۱ ماه تقویمی\n"
        )

        result.append(
            formatJalali(start)
        )

        result.append(
            " ← "
        )

        result.append(
            formatJalali(baseFinish)
        )

        result.append("\n\n")

        result.append(
            "کسری اضافه:\n"
        )

        result.append(
            extraMonths
        )

        result.append(
            " ماه و "
        )

        result.append(
            extraDays
        )

        result.append(
            " روز\n\n"
        )

        result.append(
            "تعداد ماه‌های خدمت:\n"
        )

        result.append(
            actualMonths
        )

        result.append(
            " ماه\n\n"
        )

        result.append(
            "کسری ماهانه:\n"
        )

        result.append(
            actualMonths
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
            monthlyDeduction
        )

        result.append(
            " روز\n\n"
        )

        result.append(
            "مجموع کسری:\n"
        )

        result.append(
            totalDeduction
        )

        result.append(
            " روز\n\n"
        )

        result.append(
            "مدت حضور واقعی در تقویم:\n"
        )

        result.append(
            actualCalendarDays
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
            formatJalali(finish)
        )

        result.append("\n")

        result.append(
            "━━━━━━━━━━━━━━━━━━\n\n"
        )

        result.append(
            "مبنای محاسبه: ماه‌ها به صورت ماه "
        )

        result.append(
            "تقویمی شمسی محاسبه می‌شوند؛ "
        )

        result.append(
            "هر ماه صرف‌نظر از تعداد روزهای آن، "
        )

        result.append(
            "یک ماه کامل محسوب می‌شود."
        )

        resultText.text =
            result.toString()

        resultText.post {

            val scrollView =
                resultText.parent.parent
                        as? ScrollView

            scrollView?.post {

                scrollView.fullScroll(
                    ScrollView.FOCUS_DOWN
                )
            }
        }
    }

    // =====================================================
    // تعداد ماه‌های کامل تقویمی بین دو تاریخ
    // =====================================================

    private fun completedCalendarMonths(
        start: Calendar,
        end: Calendar
    ): Int {

        if (!end.after(start)) {
            return 0
        }

        val s =
            gregorianToJalali(
                start.get(Calendar.YEAR),
                start.get(Calendar.MONTH) + 1,
                start.get(Calendar.DAY_OF_MONTH)
            )

        val e =
            gregorianToJalali(
                end.get(Calendar.YEAR),
                end.get(Calendar.MONTH) + 1,
                end.get(Calendar.DAY_OF_MONTH)
            )

        var months =
            (e[0] - s[0]) * 12 +
                    (e[1] - s[1])

        if (e[2] < s[2]) {
            months--
        }

        if (months < 0) {
            return 0
        }

        return months
    }

    // =====================================================
    // اضافه کردن ماه و روز شمسی
    // =====================================================

    private fun addJalaliMonthsAndDays(
        source: Calendar,
        months: Int,
        days: Int
    ): Calendar {

        val j =
            gregorianToJalali(
                source.get(Calendar.YEAR),
                source.get(Calendar.MONTH) + 1,
                source.get(Calendar.DAY_OF_MONTH)
            )

        var jy = j[0]
        var jm = j[1]
        var jd = j[2]

        var totalMonths =
            jy * 12 +
                    (jm - 1) +
                    months

        jy =
            totalMonths / 12

        jm =
            totalMonths % 12 + 1

        val maxDay =
            jalaliMonthLength(
                jy,
                jm
            )

        if (jd > maxDay) {
            jd = maxDay
        }

        val result =
            jalaliToGregorian(
                jy,
                jm,
                jd
            )

        return addDays(
            result,
            days
        )
    }

    // =====================================================
    // کم کردن ماه و روز شمسی
    // =====================================================

    private fun subtractJalaliMonthsAndDays(
        source: Calendar,
        months: Int,
        days: Int
    ): Calendar {

        val j =
            gregorianToJalali(
                source.get(Calendar.YEAR),
                source.get(Calendar.MONTH) + 1,
                source.get(Calendar.DAY_OF_MONTH)
            )

        var jy = j[0]
        var jm = j[1]
        var jd = j[2]

        var totalMonths =
            jy * 12 +
                    (jm - 1) -
                    months

        jy =
            totalMonths / 12

        jm =
            totalMonths % 12 + 1

        if (jm <= 0) {
            jy--
            jm += 12
        }

        val maxDay =
            jalaliMonthLength(
                jy,
                jm
            )

        if (jd > maxDay) {
            jd = maxDay
        }

        val result =
            jalaliToGregorian(
                jy,
                jm,
                jd
            )

        return subtractDays(
            result,
            days
        )
    }

    // =====================================================
    // طول ماه شمسی
    // =====================================================

    private fun jalaliMonthLength(
        year: Int,
        month: Int
    ): Int {

        return when {

            month in 1..6 ->
                31

            month in 7..11 ->
                30

            isJalaliLeap(year) ->
                30

            else ->
                29
        }
    }

    // =====================================================
    // کبیسه شمسی
    // =====================================================

    private fun isJalaliLeap(
        year: Int
    ): Boolean {

        val mod =
            year % 33

        return mod == 1 ||
                mod == 5 ||
                mod == 9 ||
                mod == 13 ||
                mod == 17 ||
                mod == 22 ||
                mod == 26 ||
                mod == 30
    }

    // =====================================================
    // اضافه کردن روز
    // =====================================================

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

    // =====================================================
    // کم کردن روز
    // =====================================================

    private fun subtractDays(
        source: Calendar,
        days: Int
    ): Calendar {

        val result =
            Calendar.getInstance()

        result.timeInMillis =
            source.timeInMillis

        result.add(
            Calendar.DAY_OF_MONTH,
            -days
        )

        return result
    }

    // =====================================================
    // اختلاف روز
    // =====================================================

    private fun daysBetween(
        start: Calendar,
        end: Calendar
    ): Int {

        val diff =
            end.timeInMillis -
                    start.timeInMillis

        return (
            diff /
                    (24L * 60L * 60L * 1000L)
            ).toInt()
    }

    // =====================================================
    // تبدیل کسری اضافه
    // =====================================================

    private fun parseExtraDeduction(
        input: String
    ): Pair<Int, Int>? {

        if (input.isEmpty()) {
            return Pair(0, 0)
        }

        try {

            var text =
                input
                    .replace('۰', '0')
                    .replace('۱', '1')
                    .replace('۲', '2')
                    .replace('۳', '3')
                    .replace('۴', '4')
                    .replace('۵', '5')
                    .replace('۶', '6')
                    .replace('۷', '7')
                    .replace('۸', '8')
                    .replace('۹', '9')
                    .trim()

            // 5/12
            if (text.contains("/")) {

                val parts =
                    text.split("/")

                if (parts.size == 2) {

                    val months =
                        parts[0]
                            .trim()
                            .toIntOrNull()

                    val days =
                        parts[1]
                            .trim()
                            .toIntOrNull()

                    if (
                        months != null &&
                        days != null &&
                        months >= 0 &&
                        days >= 0 &&
                        days < 31
                    ) {

                        return Pair(
                            months,
                            days
                        )
                    }
                }
            }

            text =
                text.replace("ماه", " ")

            text =
                text.replace("روز", " ")

            text =
                text.replace("و", " ")

            val numbers =
                Regex("\\d+")
                    .findAll(text)
                    .map {
                        it.value.toInt()
                    }
                    .toList()

            if (numbers.isEmpty()) {
                return null
            }

            if (numbers.size == 1) {

                return Pair(
                    numbers[0],
                    0
                )
            }

            val months =
                numbers[0]

            val days =
                numbers[1]

            if (
                months < 0 ||
                days < 0 ||
                days >= 31
            ) {
                return null
            }

            return Pair(
                months,
                days
            )

        } catch (e: Exception) {

            return null
        }
    }

    // =====================================================
    // تبدیل شمسی به میلادی
    // =====================================================

    private fun jalaliToGregorian(
        jy: Int,
        jm: Int,
        jd: Int
    ): Calendar {

        var jYear =
            jy - 979

        var jDayNo =
            365 * jYear +
                    (jYear / 33) * 8 +
                    ((jYear % 33) + 3) / 4

        var i = 0

        while (i < jm - 1) {

            jDayNo +=
                if (i < 6) 31
                else 30

            i++
        }

        jDayNo +=
            jd - 1

        var gDayNo =
            jDayNo + 79

        var gy =
            1600 +
                    400 *
                    (gDayNo / 146097)

        gDayNo %=
            146097

        var leap = true

        if (gDayNo >= 36525) {

            gDayNo--

            gy +=
                100 *
                (gDayNo / 36524)

            gDayNo %=
                36524

            if (gDayNo >= 365) {
                gDayNo++
            } else {
                leap = false
            }
        }

        gy +=
            4 *
            (gDayNo / 1461)

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

    // =====================================================
    // تبدیل میلادی به شمسی
    // =====================================================

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
                    4 *
                    (jDayNo / 1461)

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

    // =====================================================
    // اعتبارسنجی تاریخ
    // =====================================================

    private fun parseJalaliDate(
        input: String
    ): Calendar? {

        try {

            val normalized =
                input
                    .replace('۰', '0')
                    .replace('۱', '1')
                    .replace('۲', '2')
                    .replace('۳', '3')
                    .replace('۴', '4')
                    .replace('۵', '5')
                    .replace('۶', '6')
                    .replace('۷', '7')
                    .replace('۸', '8')
                    .replace('۹', '9')
                    .replace('-', '/')
                    .replace('.', '/')
                    .trim()

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
                jalaliMonthLength(
                    jy,
                    jm
                )

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

    // =====================================================
    // فرمت عدد
    // =====================================================

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
            .replace(".00", "")
            .replace('.', '/')
    }

    // =====================================================
    // فرمت تاریخ
    // =====================================================

    private fun formatJalali(
        calendar: Calendar
    ): String {

        val result =
            gregorianToJalali(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )

        val year =
            result[0].toString()

        val month =
            result[1]
                .toString()
                .padStart(2, '0')

        val day =
            result[2]
                .toString()
                .padStart(2, '0')

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

        return value.map { c ->

            val index =
                english.indexOf(c)

            if (index >= 0)
                persian[index]
            else
                c

        }.joinToString("")
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
