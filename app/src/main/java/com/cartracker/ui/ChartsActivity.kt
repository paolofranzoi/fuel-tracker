package com.cartracker.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cartracker.R
import com.cartracker.data.FuelEntry
import com.cartracker.databinding.ActivityChartsBinding
import com.cartracker.viewmodel.FuelEntryViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class ChartsActivity : AppCompatActivity() {
    private lateinit var b: ActivityChartsBinding
    private val vm: FuelEntryViewModel by viewModels()
    private var targa: String? = null
    private var allEntries: List<FuelEntry> = emptyList()
    private var selectedMonths: Int = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityChartsBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        b.toolbar.setNavigationOnClickListener { finish() }

        targa = intent.getStringExtra("targa") ?: run { finish(); return }

        val opts = resources.getStringArray(R.array.period_array)
        val vals = intArrayOf(1, 3, 6, 12, 24, 36, -1)
        b.dropdownPeriod.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, opts))
        b.dropdownPeriod.setText(opts[2], false)
        b.dropdownPeriod.onItemClickListener = AdapterView.OnItemClickListener { _, _, pos, _ ->
            selectedMonths = vals[pos]; updateCharts()
        }

        setupCharts()
        vm.getFuelEntriesByTarga(targa!!).observe(this) {
            allEntries = it; updateCharts()
        }
    }

    private fun setupCharts() {
        b.lineChart.apply {
            description.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
            setTouchEnabled(true); setPinchZoom(false)
        }
        b.barChart.apply {
            description.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
        }
    }

    private fun updateCharts() {
        val since = if (selectedMonths == -1) 0L else {
            Calendar.getInstance().apply { add(Calendar.MONTH, -selectedMonths) }.timeInMillis
        }
        val filtered = allEntries.filter { it.data >= since }.sortedBy { it.data }

        val empty = filtered.isEmpty()
        b.emptyState.visibility = if (empty) View.VISIBLE else View.GONE
        b.lineChart.visibility = if (empty) View.GONE else View.VISIBLE
        b.barChart.visibility = if (empty) View.GONE else View.VISIBLE
        b.statsCard.visibility = if (empty) View.GONE else View.VISIBLE
        if (empty) return

        updatePriceChart(filtered)
        updateConsumptionChart(filtered)
        updateStats(filtered)
    }

    private fun updatePriceChart(entries: List<FuelEntry>) {
        val fmt = SimpleDateFormat("dd/MM", Locale.getDefault())
        val pts = entries.mapIndexed { i, e -> Entry(i.toFloat(), e.prezzoLitro.toFloat()) }
        val labels = entries.map { fmt.format(Date(it.data)) }
        val ds = LineDataSet(pts, getString(R.string.price_per_liter)).apply {
            color = Color.parseColor("#1976D2")
            setCircleColor(Color.parseColor("#1976D2"))
            circleRadius = 4f; lineWidth = 2f
            setDrawFilled(true); fillColor = Color.parseColor("#1976D2"); fillAlpha = 50
            setDrawValues(false)
        }
        b.lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        b.lineChart.xAxis.granularity = 1f
        b.lineChart.data = LineData(ds); b.lineChart.invalidate()
    }

    private fun updateConsumptionChart(entries: List<FuelEntry>) {
        val fmt = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        val grouped = entries.groupBy { fmt.format(Date(it.data)) }
        val keys = grouped.keys.sortedBy { fmt.parse(it)?.time ?: 0L }
        val bars = keys.mapIndexed { i, k ->
            BarEntry(i.toFloat(), grouped[k]!!.sumOf { it.litri }.toFloat())
        }
        val ds = BarDataSet(bars, getString(R.string.monthly_liters)).apply {
            color = Color.parseColor("#FF5722")
        }
        b.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(keys)
        b.barChart.xAxis.granularity = 1f
        b.barChart.data = BarData(ds); b.barChart.invalidate()
    }

    private fun updateStats(entries: List<FuelEntry>) {
        val avgPrice = entries.map { it.prezzoLitro }.average()
        val totalLiters = entries.sumOf { it.litri }
        val withKm = entries.filter { it.kmAttuali != null }.sortedBy { it.data }
        val cons = if (withKm.size >= 2) {
            val km = (withKm.last().kmAttuali!! - withKm.first().kmAttuali!!).toDouble()
            val l = withKm.drop(1).sumOf { it.litri }
            if (km > 0) (l / km) * 100 else null
        } else null

        b.textAvgPrice.text = getString(R.string.avg_price_value, avgPrice)
        b.textAvgConsumption.text = if (cons != null)
            getString(R.string.avg_consumption_value, cons)
        else getString(R.string.not_available)
        b.textTotalLiters.text = getString(R.string.total_liters_value, totalLiters)
        b.textCountEntries.text = getString(R.string.count_value, entries.size)
    }
}
