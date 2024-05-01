package com.krakendepp.heart_beat.GraphPlotter;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class GraphPlotter {

        private LineChart mChart;
        private boolean drawMarkers = false;

        public GraphPlotter(LineChart lineChart) {
            mChart = lineChart;
            configureChart();
        }

    private void configureChart() {
        Description description = new Description();
        description.setText("bpm");
        mChart.setDescription(description);

        mChart.getDescription().setEnabled(true);
        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(true);
        mChart.setBackgroundColor(Color.WHITE);
        mChart.setDrawMarkers(drawMarkers);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);

        Legend legend = mChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(true);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawGridLines(false);
        // Add a custom value formatter for the left axis
//        leftAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                // Your custom logic to format the value as needed
//                // For example, you can convert the float value to a string with the appropriate unit
//                return String.format("%.2f units", value);
//            }
//        });


        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(true);
    }


    public void addEntry(float value) {
            LineData data = mChart.getData();

            if (data != null) {
                ILineDataSet set = data.getDataSetByIndex(0);
                if (set == null) {
                    set = createSet();
                    data.addDataSet(set);
                }

                data.addEntry(new Entry(set.getEntryCount(), value), 0);
                data.notifyDataChanged();

                mChart.setDrawMarkers(drawMarkers);
                mChart.notifyDataSetChanged();
                mChart.setVisibleXRangeMaximum(40);
                mChart.moveViewToX(data.getEntryCount());
            }
        }

        public void setDrawMarkers(boolean drawMarkers) {
            this.drawMarkers = drawMarkers;
        }

        private LineDataSet createSet() {
            LineDataSet set = new LineDataSet(null, "Dynamic Data");
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setLineWidth(3f);
            set.setColor(Color.MAGENTA);
            set.setHighlightEnabled(false);
            set.setDrawValues(false);
            set.setDrawCircles(false);
            set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set.setCubicIntensity(0.2f);
            return set;
        }
    }


