package org.movshovich.msaver;

import java.sql.SQLException;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.j256.ormlite.stmt.QueryBuilder;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class StatsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.stats, container, false);
		super.onCreate(savedInstanceState);
		View chart = makeChart(view);
		LinearLayout viewChart = (LinearLayout) view.findViewById(R.id.statsLayout);
		viewChart.addView(chart);
		addListeners(view);
		return view;
	}

	private GraphicalView makeChart(View view) {
		//int[] values = valuesPieChartToday(view);
		//TODO: 3 piecharts  1.cat for today 2. cat for  week 3. cat for month
		int[] values = new int[] { 5, 15, 25, 50, 75 }; // шаг 2
		String[] bars = new String[] { "Francesca's", "King of Clubs",
				"Zen Lounge", "Tied House", "Molly Magees" };
		int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA,
				Color.CYAN, Color.RED };

		CategorySeries series = new CategorySeries("Pie Chart"); // шаг 3
		DefaultRenderer dr = new DefaultRenderer(); // шаг 4

		for (int v = 0; v < 5; v++) { // шаг 5
			series.add(bars[v], values[v]);
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[v]);
			dr.addSeriesRenderer(r);
		}
		dr.setZoomButtonsVisible(true);
		dr.setZoomEnabled(true);
		dr.setChartTitle("Today pie Chart!");
		dr.setChartTitleTextSize(40);
		dr.setPanEnabled(false);
		dr.setLabelsTextSize(20.0f);
		dr.setLabelsColor(Color.BLACK);
		dr.setShowLegend(false);
		return ChartFactory.getPieChartView(view.getContext(), series, dr);
	}

	private void addListeners(View view) {
		LinearLayout viewChart = (LinearLayout) view.findViewById(R.id.statsLayout);
	}
	
	private int[] valuesPieChartToday(View view) {
		QueryBuilder<Expense, Integer> qb = MainActivity.databaseHelper
				.getExpenseDao().queryBuilder();
		int[] values = new int[] {};
		List<Expense> expenses;
		try {
			expenses = qb.orderBy("date", false).limit(5L).query();
			for (Expense e : expenses) {
				MainActivity.databaseHelper.getProductDao().refresh(e.getProduct());				
				}
		} catch (SQLException e1) {
			Log.w("MSaver", e1);
			return null;
		}
		int rowIdx = 0;
		for (Expense e : expenses) {
			values[rowIdx] = e.getPrice();	
			rowIdx += 1;
		}
		return values;
		
	}

}
