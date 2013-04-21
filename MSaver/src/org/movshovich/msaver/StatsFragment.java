package org.movshovich.msaver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.j256.ormlite.dao.GenericRawResults;
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

	private static final List<Integer> PIE_CHART_COLORS = Arrays.asList(Color.BLUE, Color.GREEN
				, Color.MAGENTA, Color.CYAN, Color.RED);
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.stats, container, false);
		super.onCreate(savedInstanceState);
		View chart;
		try {
			chart = makeCatPie(view);
		} catch (SQLException e) {
			Log.w("MSaver", e);
			return view;
		}
		LinearLayout viewChart = (LinearLayout) view.findViewById(R.id.statsLayout);
		viewChart.addView(chart);
		addListeners(view);
		return view;
	}

	private GraphicalView makeCatPie(View view) throws SQLException {
		String q = "select `categories`.`id`, `categories`.`name`, sum(`transactions`.`price`) as total"
				+ " from `categories` inner join `products` inner join `transactions`"
				+ " where `categories`.`id` != 1 and `categories`.`id` = `products`.`category_id`"
				+ " and `products`.`id` = `transactions`.`product_id`"
				+ " group by `categories`.`id` limit 5 order by total desc";
		GenericRawResults<String[]> queryRaw = MainActivity.databaseHelper
				.getTransactionDao().queryRaw(q);

		CategorySeries series = new CategorySeries("Pie Chart"); // шаг 3
		DefaultRenderer dr = new DefaultRenderer(); // шаг 4

		Iterator<Integer> colorIter = PIE_CHART_COLORS.iterator();
		for (String[] r : queryRaw.getResults()) {
			series.add(r[1], Double.valueOf(r[2]));
			SimpleSeriesRenderer ssr = new SimpleSeriesRenderer();
			ssr.setColor(colorIter.next());
			dr.addSeriesRenderer(ssr);
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
	
}
