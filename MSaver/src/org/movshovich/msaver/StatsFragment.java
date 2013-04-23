package org.movshovich.msaver;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import android.app.DatePickerDialog.OnDateSetListener;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class StatsFragment extends Fragment implements OnItemSelectedListener,
		OnDateSetListener {

	private static final List<Integer> PIE_CHART_COLORS = Arrays.asList(
			Color.BLUE, Color.GREEN, Color.MAGENTA, Color.CYAN, Color.RED);
	private static final int CATPIE_IDX = 0;
	private static final int WEEKLYSPEND_IDX = 1;
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.stats, container, false);
		super.onCreate(savedInstanceState);
		addListeners(view);
		start();
		return view;
	}

	private void start() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		DateSelector datePicker = (DateSelector) view
				.findViewById(R.id.catPieStartDate);
		datePicker.setDate(cal.getTime());
		View chart;
		try {
			chart = makeCatPie(view, cal.getTime());
		} catch (SQLException e) {
			Log.w("MSaver", e);
			return;
		}
		LinearLayout viewChart = (LinearLayout) view
				.findViewById(R.id.statsLayout);
		viewChart.addView(chart);
	}

	private void updateCatPie(Date newDate) {
		View chart;
		try {
			chart = makeCatPie(view, newDate);
		} catch (SQLException e) {
			Log.w("MSaver", e);
			return;
		}
		LinearLayout viewChart = (LinearLayout) view
				.findViewById(R.id.statsLayout);
		viewChart.removeViewAt(viewChart.getChildCount() - 1);
		viewChart.addView(chart);
	}

	private GraphicalView makeCatPie(View view, Date fromDate)
			throws SQLException {
		String fromDateTxt = DateFormat.format("yyyy-MM-dd", fromDate)
				.toString();
		String q = "select `categories`.`id`, `categories`.`name`, sum(`transactions`.`price`) as total"
				+ " from `categories` inner join `products` inner join `transactions`"
				+ " where `categories`.`id` != 1 and `categories`.`id` = `products`.`category_id`"
				+ " and `products`.`id` = `transactions`.`product_id`"
				+ " and `transactions`.`date` > " + fromDateTxt
				+ " group by `categories`.`id` order by total desc limit 5";
		GenericRawResults<String[]> rawRes = MainActivity.databaseHelper
				.getTransactionDao().queryRaw(q);

		CategorySeries series = new CategorySeries("Pie Chart");
		DefaultRenderer dr = new DefaultRenderer();

		Iterator<Integer> colorIter = PIE_CHART_COLORS.iterator();
		for (String[] r : rawRes.getResults()) {
			series.add(r[1], Double.valueOf(r[2]));
			SimpleSeriesRenderer ssr = new SimpleSeriesRenderer();
			ssr.setColor(colorIter.next());
			dr.addSeriesRenderer(ssr);
		}
		dr.setZoomButtonsVisible(true);
		dr.setZoomEnabled(true);
		dr.setChartTitle("Expense by Categories");
		dr.setChartTitleTextSize(40);
		dr.setPanEnabled(false);
		dr.setLabelsTextSize(20.0f);
		dr.setLabelsColor(Color.BLACK);
		dr.setShowLegend(false);
		return ChartFactory.getPieChartView(view.getContext(), series, dr);
	}

	private GraphicalView makeWeeklySpend(View view, Date fromDate)
			throws SQLException {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		start.setTime(fromDate);
		end.setTime(start.getTime());
		end.add(Calendar.WEEK_OF_YEAR, 1);

		Dao<Transaction, Integer> txDao = MainActivity.databaseHelper
				.getTransactionDao();
		Dao<Product, Integer> prodDao = MainActivity.databaseHelper
				.getProductDao();
		Dao<Category, Integer> catDao = MainActivity.databaseHelper
				.getCategoryDao();
		XYSeries series = new XYSeries("Weekly Spend");
		XYMultipleSeriesRenderer dr = new XYMultipleSeriesRenderer();
		int resIdx = 0;
		while (start.before(now)) {
			QueryBuilder<Category, Integer> catQb = catDao.queryBuilder();
			QueryBuilder<Product, Integer> prodQb = prodDao.queryBuilder();
			catQb.where().not().idEq(MainActivity.INCOME_CAT_ID);
			prodQb.join(catQb);
			QueryBuilder<Transaction, Integer> qb = txDao.queryBuilder();
			qb.selectRaw("sum(`transactions`.`price`)");
			qb.join(prodQb);
			qb.where().between("date", start.getTime(), end.getTime());
			String[] result = qb.queryRawFirst();
			String valStr = result[0];
			double val;
			if (valStr != null && !valStr.isEmpty()) {
				val = -Double.valueOf(valStr);
			} else {
				val = 0.0;
			}
			series.add(resIdx, val/100.0);
			resIdx += 1;
			start.add(Calendar.WEEK_OF_YEAR, 1);
			end.add(Calendar.WEEK_OF_YEAR, 1);
		}
		
//		series.add(1, 720);
//		series.add(2, 1000);
//		series.add(3, 200);
//		series.add(4, 386);
//		series.add(5, 720);
//		series.add(6, 1000);
//		series.add(7, 200);
//		series.add(8, 386);
//		series.add(9, 720);
//		series.add(10, 1000);
//		series.add(11, 200);
//		series.add(12, 386);

		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);
		XYSeriesRenderer ssr = new XYSeriesRenderer();
		ssr.setColor(Color.BLACK);
		dr.addSeriesRenderer(ssr);
		dr.setZoomButtonsVisible(true);
		dr.setZoomEnabled(true);
		dr.setChartTitle("Total Weekly Expense");
		dr.setChartTitleTextSize(30);
		dr.setPanEnabled(false);
		dr.setLabelsTextSize(20.0f);
		dr.setLabelsColor(Color.BLACK);
		dr.setShowLegend(false);
		dr.setMargins(new int[]{30, 30, 10, 10});
		dr.setMarginsColor(Color.LTGRAY);
		dr.setLabelsColor(Color.BLACK);
		dr.setYLabelsColor(0, Color.BLACK);
		dr.setXLabelsColor(Color.BLACK);
		return ChartFactory.getLineChartView(view.getContext(), dataset, dr);
	}

	private void addListeners(final View view) {
		Spinner s = (Spinner) view.findViewById(R.id.chartType);
		s.setOnItemSelectedListener(this);
		DateSelector datePicker = (DateSelector) view
				.findViewById(R.id.catPieStartDate);
		datePicker.setOnDateSetListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View viewSelected,
			int position, long id) {
		View chart;
		DateSelector datePicker = (DateSelector) view
				.findViewById(R.id.catPieStartDate);
		Date date = datePicker.getDate();
		try {
			if (position == CATPIE_IDX) {
				chart = makeCatPie(view, date);
			} else if (position == WEEKLYSPEND_IDX) {
				chart = makeWeeklySpend(view, date);
			} else {
				Log.e("MSaver", "Bad position selected in spinner: " + position);
				return;
			}
		} catch (SQLException e) {
			Log.w("MSaver", e);
			return;
		}
		LinearLayout viewChart = (LinearLayout) view
				.findViewById(R.id.statsLayout);
		viewChart.removeViewAt(viewChart.getChildCount() - 1);
		viewChart.addView(chart);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onDateSet(DatePicker dp, int year, int monthOfYear,
			int dayOfMonth) {
		Spinner s = (Spinner) view.findViewById(R.id.chartType);
		onItemSelected(s, s.getSelectedView(), s.getSelectedItemPosition(), s.getSelectedItemId());
	}
}
