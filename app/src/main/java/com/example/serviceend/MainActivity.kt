package com.example.serviceend

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.*
import java.util.*

data class JDate(val y:Int,val m:Int,val d:Int)

class MainActivity : Activity() {
    lateinit var start: EditText
    lateinit var extraMonths: EditText
    lateinit var extraDays: EditText
    lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28,28,28,28)
        }
        fun label(t:String)=TextView(this).apply {
            text=t; textSize=15f; setPadding(0,10,0,4)
        }
        fun input(h:String)=EditText(this).apply {
            hint=h; textSize=16f; setPadding(12,10,12,10)
        }
        box.addView(TextView(this).apply {
            text="🎖️ محاسبه‌گر پایان خدمت"; textSize=25f
            gravity=Gravity.CENTER; setPadding(0,10,0,20)
        })
        box.addView(label("تاریخ شروع خدمت"))
        start=input("۱۴۰۴/۱۱/۰۱"); box.addView(start)
        box.addView(label("کسری اضافی (ماه)"))
        extraMonths=input("۰"); extraMonths.inputType=2; box.addView(extraMonths)
        box.addView(label("کسری اضافی (روز)"))
        extraDays=input("۰"); extraDays.inputType=2; box.addView(extraDays)
        box.addView(Button(this).apply {
            text="محاسبه پایان خدمت"; setOnClickListener { calculate() }
        })
        box.addView(Button(this).apply {
            text="امروز"; setOnClickListener { start.setText(format(todayJ())); calculate() }
        })
        result=TextView(this).apply {
            textSize=18f; setTextColor(Color.rgb(4,120,87)); setPadding(8,25,8,8)
        }
        box.addView(result)
        setContentView(ScrollView(this).apply { addView(box) })
    }

    fun fa(s:String)=s.map{ when(it){
        '۰'->'0';'۱'->'1';'۲'->'2';'۳'->'3';'۴'->'4';'۵'->'5';'۶'->'6';'۷'->'7';'۸'->'8';'۹'->'9';else->it
    }}.joinToString("")

    fun parse(s:String):JDate? {
        val a=fa(s.trim()).split("/", "-", ".").mapNotNull{it.toIntOrNull()}
        if(a.size!=3) return null
        val y=a[0]; val m=a[1]; val d=a[2]
        val max=if(m<=6)31 else if(m<=11)30 else if(leap(y))30 else 29
        if(y<1300 || m !in 1..12 || d !in 1..max) return null
        return JDate(y,m,d)
    }

    fun leap(y:Int):Boolean {
        val r=((y-474)%2820+2820)%2820
        return r in 0..1
    }

    fun dim(y:Int,m:Int)=if(m<=6)31 else if(m<=11)30 else if(leap(y))30 else 29

    fun serial(j:JDate):Long {
        var n=0L
        for(y in 1 until j.y) n += 365L + if(leap(y))1 else 0
        for(m in 1 until j.m) n += dim(j.y,m)
        return n+j.d
    }

    fun addDays(j:JDate, n0:Int):JDate {
        var y=j.y; var m=j.m; var d=j.d; var n=n0
        while(n>0) {
            d++
            if(d>dim(y,m)){ d=1; if(m==12){m=1;y++}else m++ }
            n--
        }
        return JDate(y,m,d)
    }

    fun addMonths(j:JDate,n:Int):JDate {
        var y=j.y; var m=j.m; var d=j.d
        repeat(n) {
            if(m==12){m=1;y++}else m++
            d=minOf(d,dim(y,m))
        }
        return JDate(y,m,d)
    }

    fun format(j:JDate)=String.format(Locale.US,"%04d/%02d/%02d",j.y,j.m,j.d)

    fun todayJ():JDate {
        val c=Calendar.getInstance()
        val gy=c.get(Calendar.YEAR); val gm=c.get(Calendar.MONTH)+1; val gd=c.get(Calendar.DAY_OF_MONTH)
        // Gregorian -> Jalali (common 33-year conversion for current dates)
        var jy=gy-621
        val md=intArrayOf(0,31,59,90,120,151,181,212,243,273,304,334)
        var gdnum=365*(gy-1600)+(gy-1600+3)/4-(gy-1600+99)/100+(gy-1600+399)/400+gd-1+md[gm-1]
        if(gm>2 && (gy%4==0 && gy%100!=0 || gy%400==0)) gdnum++
        var jd=gdnum-79
        val jnp=jd/12053
        jd%=12053
        jy=979+33*jnp+4*(jd/1461)
        jd%=1461
        if(jd>=366){jy+=(jd-1)/365;jd=(jd-1)%365}
        val jm=if(jd<186)1+jd/31 else 7+(jd-186)/30
        val day=1+if(jd<186)jd%31 else (jd-186)%30
        return JDate(jy,jm,day)
    }

    fun calculate() {
        val s=parse(start.text.toString())
        if(s==null){ result.text="❌ تاریخ را مثل ۱۴۰۴/۱۱/۰۱ وارد کن."; return }
        val em=extraMonths.text.toString().toDoubleOrNull()?:0.0
        val ed=extraDays.text.toString().toDoubleOrNull()?:0.0
        val extra=em+ed/30.0
        val actual=(21.0-extra)/(1.0+5.0/30.0)
        val months=kotlin.math.floor(actual).toInt()
        val days=kotlin.math.round((actual-months)*30.0).toInt()
        val finish=addDays(addMonths(s,months),days-1)
        result.text="🎯 پایان خدمت: ${format(finish)}\n\nمدت خدمت واقعی: $months ماه و $days روز\nکسری اضافی: $em ماه و $ed روز"
    }
}
