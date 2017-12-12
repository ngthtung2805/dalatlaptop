package com.tungnui.abccomputer.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.adapter.NotificationAdapter
import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.data.sqlite.NotificationDBController
import com.tungnui.abccomputer.model.NotificationModel
import com.tungnui.abccomputer.utils.ActivityUtils
import java.util.ArrayList
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.ValueDependentColor
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.GraphView
import com.tungnui.abccomputer.api.CategoryService
import com.tungnui.abccomputer.api.ProductService
import com.tungnui.abccomputer.api.ReportServices
import com.tungnui.abccomputer.api.ServiceGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class AdminReportActivity : AppCompatActivity() {
    private var mCompositeDisposable: CompositeDisposable
    val reportService: ReportServices
    init {
        mCompositeDisposable = CompositeDisposable()
        reportService = ServiceGenerator.createService(ReportServices::class.java)
    }
    private var mToolbar: Toolbar? = null
    private var emptyView: TextView? = null
    private var data:ArrayList<DataPoint>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVars()
        initialView()
        initFunctionality()
        initialListener()
    }

    private fun initVars() {
data = ArrayList<DataPoint>()
    }

    private fun initialView() {
        setContentView(R.layout.activity_admin_report)
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        emptyView = findViewById<View>(R.id.emptyView) as TextView
        setSupportActionBar(mToolbar)
        supportActionBar?.title = getString(R.string.notifications)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        val graph = findViewById<View>(R.id.graph) as GraphView
        val series = BarGraphSeries(arrayOf(DataPoint(0.0, -1.0), DataPoint(1.0, 5.0), DataPoint(2.0, 3.0), DataPoint(3.0, 2.0), DataPoint(4.0, 6.0)))
        graph.addSeries(series)
        // styling
        series.setValueDependentColor { data -> Color.rgb(data.x.toInt() * 255 / 4, Math.abs(data.y * 255 / 6).toInt(), 100) }
        series.spacing = 50
        // draw values on top
        series.isDrawValuesOnTop = true
        series.valuesOnTopColor = Color.RED
        //series.setValuesOnTopSize(50);

    }

    private fun initFunctionality() {

    }

    private fun initialListener() {
        mToolbar?.setNavigationOnClickListener { finish() }


    }

    fun getTopSallerReport(){
        // Load popular product
       var disposable = reportService.getTopSaller()
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response != null) {
                        for (item in response){
                            //data.add(DataPoint(item.title, item.quantity))
                        }
                    }
                },
                        { error ->

                        })
        mCompositeDisposable.add(disposable)
    }

    override fun onResume() {
        super.onResume()
    }
}