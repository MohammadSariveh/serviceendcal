package com.example.serviceend

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import java.util.Calendar
import java.util.Locale
import kotlin.math.floor

class MainActivity : Activity() {

    private lateinit var startDateInput: EditText
    private lateinit var extraDeductionInput: EditText
    private lateinit var resultCard: LinearLayout

    private lateinit var localButton: TextView
    private lateinit var nonLocalButton: TextView

    private var deductionDaysPerMonth = 5

    private val bgColor = Color.rgb(246, 248, 252)
    private val primaryColor = Color.rgb(32, 85, 150)
    private val primaryDark = Color.rgb(24, 65, 118)
    private val successColor = Color.rgb(35, 150, 100)
    private val textColor = Color.rgb(35, 43, 55)
    private val secondaryText = Color.rgb(105, 115, 130)
    private val borderColor = Color.rgb(220, 225, 233)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = primaryDark
        window.navigationBarColor = bgColor

        createUserInterface()
    }

    // =========================================================
    // UI
    // =========================================================

    private fun createUserInterface() {

        val scrollView = ScrollView(this)
        scrollView.setBackgroundColor(bgColor)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.gravity = Gravity.CENTER_HORIZONTAL
        root.setPadding(dp(20), dp(20), dp(20), dp(40))

        scrollView.addView(root)

        // Header
        val header = LinearLayout(this)
        header.orientation = LinearLayout.VERTICAL
        header.gravity = Gravity.CENTER
        header.setPadding(dp(20), dp(25), dp(20), dp(25))
        header.background = roundedBackground(
            primaryColor,
            24
        )

        val icon = TextView(this)
        icon.text = "🎖️"
        icon.textSize = 34f
        icon.gravity = Gravity.CENTER

        header.addView(icon)

        val title = TextView(this)
        title.text = "محاسبه‌گر پایان خدمت"
        title.textSize = 25f
        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER
        title.setTypeface(null, android.graphics.Typeface.BOLD)
        title.setPadding(0, dp(8), 0, dp(5))

        header.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "محاسبه دقیق مدت خدمت و تاریخ پایان"
        subtitle.textSize = 14f
        subtitle.setTextColor(Color.WHITE)
        subtitle.gravity = Gravity.CENTER

        header.addView(subtitle)

        root.addView(
            header,
            matchParams().apply {
                setMargins(0, 0, 0, dp(18))
            }
        )

        // Base service card
        val baseCard = createCard()

        val baseTitle = createSectionTitle("مدت پایه خدمت")
        baseCard.addView(baseTitle)

        val baseText = TextView(this)
        baseText.text = "۲۱ ماه"
        baseText.textSize = 22f
        baseText.setTextColor(primaryColor)
        baseText.setTypeface(null, android.graphics.Typeface.BOLD)
        baseText.gravity = Gravity.CENTER
        baseText.setPadding(0, dp(8), 0, dp(5))

        baseCard.addView(baseText)

        val baseHint = TextView(this)
        baseHint.text = "مبنای محاسبه: ماه‌های تقویمی شمسی"
        baseHint.textSize = 13f
        baseHint.setTextColor(secondaryText)
        baseHint.gravity = Gravity.CENTER

        baseCard.addView(baseHint)

        root.addView(
            baseCard,
            matchParams().apply {
                setMargins(0, 0, 0, dp(15))
            }
        )

        // Service type
        val typeCard = createCard()

        typeCard.addView(
            createSectionTitle("نوع خدمت")
        )

        val typeHint = TextView(this)
        typeHint.text = "نوع خدمت را انتخاب کنید"
        typeHint.textSize = 13f
        typeHint.setTextColor(secondaryText)
        typeHint.gravity = Gravity.RIGHT

        typeCard.addView(
            typeHint,
            matchParams().apply {
                setMargins(0, 0, 0, dp(12))
            }
        )

        val typeLayout = LinearLayout(this)
        typeLayout.orientation = LinearLayout.HORIZONTAL
        typeLayout.gravity = Gravity.CENTER
        typeLayout.setPadding(0, 0, 0, 0)

        localButton = createTypeButton(
            "بومی",
            "۵ روز کسری"
        )

        nonLocalButton = createTypeButton(
            "غیربومی",
            "۱۲ روز کسری"
        )

        typeLayout.addView(
            localButton,
            LinearLayout.LayoutParams(
                0,
                dp(70),
                1f
            ).apply {
                setMargins(0, 0, dp(6), 0)
            }
        )

        typeLayout.addView(
            nonLocalButton,
            LinearLayout.LayoutParams(
                0,
                dp(70),
                1f
            ).apply {
                setMargins(dp(6), 0, 0, 0)
            }
        )

        typeCard.addView(typeLayout)

        root.addView(
            typeCard,
            matchParams().apply {
                setMargins(0, 0, 0, dp(15))
            }
        )

        updateTypeButtons()

        localButton.setOnClickListener {
            deductionDaysPerMonth = 5
            updateTypeButtons()
        }

        nonLocalButton.setOnClickListener {
            deductionDaysPerMonth = 12
            updateTypeButtons()
        }

        // Start date
        val dateCard = createCard()

        dateCard.addView(
            createSectionTitle("تاریخ شروع خدمت")
        )

        val dateHint = TextView(this)
        dateHint.text = "تاریخ را به صورت ۱۴۰۵/۰۵/۲۳ وارد کنید"
        dateHint.textSize = 13f
        dateHint.setTextColor(secondaryText)
        dateHint.gravity = Gravity.RIGHT

        dateCard.addView(
            dateHint,
            matchParams().apply {
                setMargins(0, 0, 0, dp(10))
            }
        )

        startDateInput = createInput(
            "مثلاً ۱۴۰۵/۰۵/۲۳"
        )

        dateCard.addView(startDateInput)

        root.addView(
            dateCard,
            matchParams().apply {
                setMargins(0, 0, 0, dp(15))
            }
        )

        // Extra deduction
        val extraCard = createCard()

        extraCard.addView(
            createSectionTitle("کسری‌های اضافه")
        )

        val extraHint = TextView(this)
        extraHint.text =
            "می‌توانید مثلاً «۵ ماه و ۱۲ روز» یا «۵/۱۲» یا «۱۲ روز» وارد کنید."

        extraHint.textSize = 13f
        extraHint.setTextColor(secondaryText)
        extraHint.gravity = Gravity.RIGHT

        extraCard.addView(
            extraHint,
            matchParams().apply {
                setMargins(0, 0, 0, dp(10))
            }
        )

        extraDeductionInput = createInput(
            "مثلاً ۵ ماه و ۱۲ روز"
        )

        extraCard.addView(extraDeductionInput)

        root.addView(
            extraCard,
            matchParams().apply {
                setMargins(0, 0, 0, dp(18))
            }
        )

        // Calculate button
        val calculateButton = TextView(this)

        calculateButton.text = "محاسبه تاریخ پایان خدمت"
        calculateButton.textSize = 17f
        calculateButton.setTextColor(Color.WHITE)
        calculateButton.gravity = Gravity.CENTER
        calculateButton.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )
        calculateButton.background = roundedBackground(
            successColor,
            18
        )
        calculateButton.setPadding(
            dp(15),
            dp(16),
            dp(15),
            dp(16)
        )
        calculateButton.elevation = dp(4).toFloat()

        calculateButton.setOnClickListener {
            calculateService()
        }

        root.addView(
            calculateButton,
            matchParams().apply {
                setMargins(0, 0, 0, dp(20))
            }
        )

        // Result card
        resultCard = createCard()
        resultCard.visibility = View.GONE

        root.addView(
            resultCard,
            matchParams()
        )

        setContentView(scrollView)
    }

    private fun createCard(): LinearLayout {

        val card = LinearLayout(this)

        card.orientation = LinearLayout.VERTICAL
        card.setPadding(
            dp(18),
            dp(18),
            dp(18),
            dp(18)
        )

        card.background = roundedBorderBackground(
            Color.WHITE,
            borderColor,
            20
        )

        card.elevation = dp(2).toFloat()

        return card
    }

    private fun createSectionTitle(
        text: String
    ): TextView {

        val title = TextView(this)

        title.text = text
        title.textSize = 18f
        title.setTextColor(textColor)
        title.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )
        title.gravity = Gravity.RIGHT

        title.setPadding(
            0,
            0,
            0,
            dp(10)
        )

        return title
    }

    private fun createTypeButton(
        title: String,
        subtitle: String
    ): TextView {

        val view = TextView(this)

        view.text =
            "$title\n$subtitle"

        view.textSize = 15f
        view.gravity = Gravity.CENTER
        view.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )
        view.isClickable = true
        view.setPadding(
            dp(5),
            dp(5),
            dp(5),
            dp(5)
        )

        return view
    }

    private fun createInput(
        hint: String
    ): EditText {

        val input = EditText(this)

        input.hint = hint
        input.textSize = 16f
        input.gravity = Gravity.CENTER
        input.setTextColor(textColor)
        input.setHintTextColor(secondaryText)
        input.setPadding(
            dp(14),
            dp(12),
            dp(14),
            dp(12)
        )

        input.background = roundedBorderBackground(
            Color.rgb(250, 251, 253),
            borderColor,
            14
        )

        input.inputType =
            InputType.TYPE_CLASS_TEXT

        return input
    }

    private fun updateTypeButtons() {

        if (deductionDaysPerMonth == 5) {

            localButton.background =
                roundedBackground(
                    primaryColor,
                    15
                )

            localButton.setTextColor(
                Color.WHITE
            )

            nonLocalButton.background =
                roundedBorderBackground(
                    Color.WHITE,
                    borderColor,
                    15
                )

            nonLocalButton.setTextColor(
                textColor
            )

        } else {

            nonLocalButton.background =
                roundedBackground(
                    primaryColor,
                    15
                )

            nonLocalButton.setTextColor(
                Color.WHITE
            )

            localButton.background =
                roundedBorderBackground(
                    Color.WHITE,
                    borderColor,
                    15
                )

            localButton.setTextColor(
                textColor
            )
        }
    }

    // =========================================================
    // Calculation
    // =========================================================

    private fun calculateService() {

        val startDateText =
            startDateInput.text
                .toString()
                .trim()

        if (startDateText.isEmpty()) {

            showError(
                "لطفاً تاریخ شروع خدمت را وارد کنید."
            )

            return
        }

        val start =
            parseJalaliDate(startDateText)

        if (start == null) {

            showError(
                "تاریخ واردشده صحیح نیست.\nمثال: ۱۴۰۵/۰۵/۲۳"
            )

            return
        }

        val extraText =
            extraDeductionInput.text
                .toString()
                .trim()

        val extraDuration =
            parseExtraDeduction(extraText)

        if (extraDuration == null) {

            showError(
                "مقدار کسری صحیح نیست.\nمثال: ۵ ماه و ۱۲ روز"
            )

            return
        }

        val extraMonths =
            extraDuration.first

        val extraDays =
            extraDuration.second

        val extraEnd =
            addJalaliMonthsAndDays(
                start,
                extraMonths,
                extraDays
            )

        val extraDeductionDays =
            daysBetween(
                start,
                extraEnd
            )

        val baseFinish =
            addJalaliMonthsAndDays(
                start,
                21,
                0
            )

        val baseServiceDays =
            daysBetween(
                start,
                baseFinish
            )

        /*
         * به جای بررسی تک‌تک روزها با یک حلقه طولانی،
         * از Binary Search استفاده می‌کنیم.
         */

        var low = 0
        var high = baseServiceDays

        while (low < high) {

            val mid =
                (low + high) / 2

            val testDate =
                addDays(
                    start,
                    mid
                )

            val monthlyDeduction =
                calculateMonthlyDeduction(
                    start,
                    testDate
                )

            val totalUsed =
                mid +
                        monthlyDeduction +
                        extraDeductionDays

            if (totalUsed >= baseServiceDays) {
                high = mid
            } else {
                low = mid + 1
            }
        }

        val actualServiceDays = low

        val finishDate =
            addDays(
                start,
                actualServiceDays
            )

        val serviceDeductionDays =
            calculateMonthlyDeduction(
                start,
                finishDate
            )

        val totalDeductionDays =
            serviceDeductionDays +
                    extraDeductionDays

        val serviceType =
            if (deductionDaysPerMonth == 5) {
                "بومی"
            } else {
                "غیربومی"
            }

        showResult(
            serviceType = serviceType,
            start = start,
            baseFinish = baseFinish,
            baseServiceDays = baseServiceDays,
            extraMonths = extraMonths,
            extraDays = extraDays,
            extraDeductionDays = extraDeductionDays,
            actualServiceDays = actualServiceDays,
            serviceDeductionDays = serviceDeductionDays,
            totalDeductionDays = totalDeductionDays,
            finishDate = finishDate
        )
    }

    // =========================================================
    // Result
    // =========================================================

    private fun showResult(
        serviceType: String,
        start: Calendar,
        baseFinish: Calendar,
        baseServiceDays: Int,
        extraMonths: Int,
        extraDays: Int,
        extraDeductionDays: Int,
        actualServiceDays: Int,
        serviceDeductionDays: Double,
        totalDeductionDays: Double,
        finishDate: Calendar
    ) {

        resultCard.removeAllViews()
        resultCard.visibility = View.VISIBLE

        val title = TextView(this)

        title.text = "🎯 نتیجه محاسبه"
        title.textSize = 21f
        title.setTextColor(primaryColor)
        title.gravity = Gravity.CENTER
        title.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        resultCard.addView(
            title,
            matchParams().apply {
                setMargins(0, 0, 0, dp(18))
            }
        )

        addResultRow(
            "نوع خدمت",
            serviceType
        )

        addResultRow(
            "کسری ماهانه",
            "${toPersianDigits(deductionDaysPerMonth.toString())} روز"
        )

        addDivider()

        addResultRow(
            "تاریخ شروع",
            formatJalali(start)
        )

        addResultRow(
            "پایان ۲۱ ماه بدون کسری",
            formatJalali(baseFinish)
        )

        addResultRow(
            "مدت پایه",
            "${formatNumber(baseServiceDays.toDouble())} روز"
        )

        addDivider()

        addResultRow(
            "کسری اضافه",
            "${toPersianDigits(extraMonths.toString())} ماه و " +
                    "${toPersianDigits(extraDays.toString())} روز"
        )

        addResultRow(
            "معادل کسری اضافه",
            "${formatNumber(extraDeductionDays.toDouble())} روز"
        )

        addResultRow(
            "کسری نوع خدمت",
            "${formatNumber(serviceDeductionDays)} روز"
        )

        addResultRow(
            "مجموع کسری",
            "${formatNumber(totalDeductionDays)} روز"
        )

        addDivider()

        val finishBox = LinearLayout(this)

        finishBox.orientation =
            LinearLayout.VERTICAL

        finishBox.gravity = Gravity.CENTER

        finishBox.setPadding(
            dp(15),
            dp(18),
            dp(15),
            dp(18)
        )

        finishBox.background =
            roundedBackground(
                successColor,
                18
            )

        val finishTitle = TextView(this)

        finishTitle.text =
            "تاریخ پایان خدمت"

        finishTitle.textSize = 15f
        finishTitle.setTextColor(Color.WHITE)
        finishTitle.gravity = Gravity.CENTER

        finishBox.addView(finishTitle)

        val finishText = TextView(this)

        finishText.text =
            formatJalali(finishDate)

        finishText.textSize = 27f
        finishText.setTextColor(Color.WHITE)
        finishText.gravity = Gravity.CENTER
        finishText.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        finishBox.addView(
            finishText,
            matchParams().apply {
                setMargins(0, dp(5), 0, 0)
            }
        )

        resultCard.addView(
            finishBox,
            matchParams().apply {
                setMargins(0, dp(15), 0, dp(12))
            }
        )

        val note = TextView(this)

        note.text =
            "مبنای محاسبه بر اساس تقویم شمسی و ماه‌های تقویمی است."

        note.textSize = 12f
        note.setTextColor(secondaryText)
        note.gravity = Gravity.CENTER
        note.setPadding(
            dp(5),
            dp(8),
            dp(5),
            0
        )

        resultCard.addView(note)
    }

    private fun addResultRow(
        label: String,
        value: String
    ) {

        val row = LinearLayout(this)

        row.orientation =
            LinearLayout.HORIZONTAL

        row.gravity =
            Gravity.CENTER_VERTICAL

        row.setPadding(
            0,
            dp(7),
            0,
            dp(7)
        )

        val labelView = TextView(this)

        labelView.text = label
        labelView.textSize = 14f
        labelView.setTextColor(secondaryText)
        labelView.gravity = Gravity.RIGHT

        val valueView = TextView(this)

        valueView.text = value
        valueView.textSize = 15f
        valueView.setTextColor(textColor)
        valueView.gravity = Gravity.LEFT
        valueView.setTypeface(
            null,
            android.graphics.Typeface.BOLD
        )

        row.addView(
            labelView,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        row.addView(
            valueView,
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        )

        resultCard.addView(row)
    }

    private fun addDivider() {

        val divider = View(this)

        divider.setBackgroundColor(
            borderColor
        )

        resultCard.addView(
            divider,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)
            ).apply {
                setMargins(
                    0,
                    dp(8),
                    0,
                    dp(8)
                )
            }
        )
    }

    // =========================================================
    // Monthly deduction
    // =========================================================

    private fun calculateMonthlyDeduction(
        start: Calendar,
        end: Calendar
    ): Double {

        if (!end.after(start)) {
            return 0.0
        }

        var current =
            start.clone() as Calendar

        var totalDeduction = 0.0

        while (true) {

            val nextMonth =
                addJalaliMonthsAndDays(
                    current,
                    1,
                    0
                )

            if (nextMonth.after(end)) {

                val remainingDays =
                    daysBetween(
                        current,
                        end
                    )

                val monthDays =
                    daysBetween(
                        current,
                        nextMonth
                    )

                if (monthDays > 0) {

                    totalDeduction +=
                        deductionDaysPerMonth *
                                remainingDays.toDouble() /
                                monthDays.toDouble()
                }

                break
            }

            totalDeduction +=
                deductionDaysPerMonth.toDouble()

            current = nextMonth
        }

        return totalDeduction
    }

    // =========================================================
    // Extra deduction parser
    // =========================================================

    private fun parseExtraDeduction(
        input: String
    ): Pair<Int, Int>? {

        if (input.isEmpty()) {
            return Pair(0, 0)
        }

        return try {

            var text =
                normalizeDigits(input)
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
                text
                    .replace("ماه", " ")
                    .replace("روز", " ")
                    .replace("و", " ")

            val numbers =
                Regex("\\d+")
                    .findAll(text)
                    .map {
                        it.value.toInt()
                    }
                    .toList()

            if (numbers.isEmpty()) {
                null
            } else if (numbers.size == 1) {

                // اگر فقط «۱۲ روز» باشد
                if (
                    input.contains("روز")
                ) {
                    Pair(
                        0,
                        numbers[0]
                    )
                } else {
                    Pair(
                        numbers[0],
                        0
                    )
                }

            } else {

                val months =
                    numbers[0]

                val days =
                    numbers[1]

                if (
                    months < 0 ||
                    days < 0 ||
                    days >= 31
                ) {
                    null
                } else {
                    Pair(
                        months,
                        days
                    )
                }
            }

        } catch (e: Exception) {
            null
        }
    }

    // =========================================================
    // Jalali date
    // =========================================================

    private fun addJalaliMonthsAndDays(
        source: Calendar,
        months: Int,
        days: Int
    ): Calendar {

        val jalali =
            gregorianToJalali(
                source.get(Calendar.YEAR),
                source.get(Calendar.MONTH) + 1,
                source.get(Calendar.DAY_OF_MONTH)
            )

        var jy = jalali[0]
        var jm = jalali[1]
        var jd = jalali[2]

        val totalMonths =
            jy * 12 +
                    (jm - 1) +
                    months

        jy = totalMonths / 12
        jm = totalMonths % 12 + 1

        val maxDay =
            jalaliMonthLength(
                jy,
                jm
            )

        if (jd > maxDay) {
            jd = maxDay
        }

        var result =
            jalaliToGregorian(
                jy,
                jm,
                jd
            )

        result =
            addDays(
                result,
                days
            )

        return result
    }

    private fun jalaliMonthLength(
        year: Int,
        month: Int
    ): Int {

        return when {
            month <= 6 -> 31
            month <= 11 -> 30
            isJalaliLeap(year) -> 30
            else -> 29
        }
    }

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

    // =========================================================
    // Calendar helpers
    // =========================================================

    private fun daysBetween(
        start: Calendar,
        end: Calendar
    ): Int {

        val diff =
            end.timeInMillis -
                    start.timeInMillis

        return floor(
            diff.toDouble() /
                    (24.0 * 60.0 * 60.0 * 1000.0)
        ).toInt()
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

    // =========================================================
    // Jalali -> Gregorian
    // =========================================================

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
                if (i < 6) 31 else 30

            i++
        }

        jDayNo += jd - 1

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
        var gd = gDayNo + 1

        while (
            gm < 12 &&
            gd > gDays[gm]
        ) {

            gd -= gDays[gm]
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

    // =========================================================
    // Gregorian -> Jalali
    // =========================================================

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

        val gyTemp = gy - 1600
        val gmTemp = gm - 1
        val gdTemp = gd - 1

        var gDayNo =
            365 * gyTemp +
                    (gyTemp + 3) / 4 -
                    (gyTemp + 99) / 100 +
                    (gyTemp + 399) / 400

        var i = 0

        while (i < gmTemp) {
            gDayNo += gDaysInMonth[i]
            i++
        }

        if (
            gmTemp > 1 &&
            isGregorianLeap(gy)
        ) {
            gDayNo++
        }

        gDayNo += gdTemp

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
            jDayNo >= jDaysInMonth[jm]
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

    // =========================================================
    // Parse Jalali
    // =========================================================

    private fun parseJalaliDate(
        input: String
    ): Calendar? {

        return try {

            val normalized =
                normalizeDigits(input)
                    .replace("-", "/")
                    .replace(".", "/")
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

            if (
                jy < 1300 ||
                jy > 1500
            ) {
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

            jalaliToGregorian(
                jy,
                jm,
                jd
            )

        } catch (e: Exception) {
            null
        }
    }

    // =========================================================
    // Formatting
    // =========================================================

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

    private fun normalizeDigits(
        value: String
    ): String {

        return value
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

    private fun isGregorianLeap(
        year: Int
    ): Boolean {

        return year % 4 == 0 &&
                (
                    year % 100 != 0 ||
                            year % 400 == 0
                    )
    }

    // =========================================================
    // UI Helpers
    // =========================================================

    private fun roundedBackground(
        color: Int,
        radius: Int
    ): GradientDrawable {

        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun roundedBorderBackground(
        color: Int,
        border: Int,
        radius: Int
    ): GradientDrawable {

        return GradientDrawable().apply {
            setColor(color)
            setStroke(
                dp(1),
                border
            )
            cornerRadius = dp(radius).toFloat()
        }
    }

    private fun matchParams():
            LinearLayout.LayoutParams {

        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private fun dp(
        value: Int
    ): Int {

        return (
                value *
                        resources.displayMetrics.density
                ).toInt()
    }

    private fun showError(
        message: String
    ) {

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}
