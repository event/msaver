package org.movshovich.msaver;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class IncomeFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.income, container, false);
		addListeners(view);
		updateBalance(view);
		showList(view);
		//TODO: choose  date
		TextView dateView = (TextView) view.findViewById(R.id.incomeDate);		
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");		
		dateView.setText(sdf.format(new Date()));
		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateBalance(getView());
		Log.w("Msaver", "preved");
	}

	private void addListeners(final View view) {
		Button addButton = (Button) view.findViewById(R.id.incomeAdd);
		if (addButton != null) {
			addButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onAddClick(view);
				}
			});

		}
		final EditText price = (EditText) view
				.findViewById(R.id.incomeSumEnter);
		price.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				price.setTextColor(Color.BLACK);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		AutoCompleteTextView textView = (AutoCompleteTextView) view
				.findViewById(R.id.incomeProductEnter);
		SimpleCursorAdapter sca = new SimpleCursorAdapter(view.getContext(),
				android.R.layout.simple_dropdown_item_1line, null,
				new String[] { "name" }, new int[] { android.R.id.text1 }, 0);
		textView.setAdapter(sca);

		sca.setCursorToStringConverter(new CursorToStringConverter() {
			public String convertToString(android.database.Cursor cursor) {
				// Get the label for this row out of the "state" column
				final int columnIndex = cursor.getColumnIndexOrThrow("name");
				final String str = cursor.getString(columnIndex);
				return str;
			}
		});
		sca.setFilterQueryProvider(new FilterQueryProvider() {
			public Cursor runQuery(CharSequence constraint) {
				// Search for states whose names begin with the specified letters
				// build your query
				if (constraint == null) {
					return null;
				}
				Dao<Product, Integer> dao = MainActivity.databaseHelper
						.getProductDao();
				QueryBuilder<Product, Integer> qb = dao.queryBuilder();
				// when you are done, prepare your query and build an iterator
				CloseableIterator<String[]> iterator = null;
				try {
					qb.selectRaw("`id` as `_id`", "`name`");
					qb.where().like("name", constraint.toString() + "%");
					String prepareStatementString = qb.prepareStatementString();
					Log.d("MSaver", qb.prepareStatementString());
					GenericRawResults<String[]> rawRes = dao
							.queryRaw(prepareStatementString);
					iterator = rawRes.closeableIterator();
				} catch (SQLException e) {
					Log.w("MSaver", e);
					return null;
				}
				AndroidDatabaseResults results = (AndroidDatabaseResults) iterator
						.getRawResults();
				return results.getRawCursor();
			}
		});

	}
	
	private void showList(View view) {
		QueryBuilder<Expense, Integer> qb = MainActivity.databaseHelper
				.getExpenseDao().queryBuilder();
		List<Expense> expenses;
		try {
			qb.where().gt("price", 0);
			expenses = qb.orderBy("date", false).limit(5L).query();
			for (Expense e : expenses) {
				MainActivity.databaseHelper.getProductDao().refresh(e.getProduct());
			}
		} catch (SQLException e1) {
			Log.w("MSaver", e1);
			return;
		}
		TableLayout tl = (TableLayout) view.findViewById(R.id.last_incomes);

		String sum;
		int rowIdx = 0;
		for (Expense e : expenses) {
			TableRow row = (TableRow) tl.getChildAt(rowIdx);
			TextView productText = (TextView) row.getChildAt(0);
			TextView priceText = (TextView) row.getChildAt(1);
			productText.setText(e.getProduct().getName());
			sum = addingDotToString(Integer.toString(e.getPrice()));
			priceText.setText(sum);
			rowIdx += 1;

		}

	}


	private void onAddClick(View view) {
		// TODO: reuse old products - don't create new products on every save
				// TODO: when adding new product it should ask for some properties (i.e. category, shmategorey, etc.)
				EditText producttext = (EditText) view.findViewById(R.id.incomeProductEnter);
				EditText pricetext = (EditText) view.findViewById(R.id.incomeSumEnter);
					
				String coinPrice = pricetext.getText().toString();
				String price = coinPrice;
				if (producttext.getText().length() == 0  || price.isEmpty()){
					return;
				}
				Product p = new Product();
				p.setName(producttext.getText().toString());
				
				Expense e = new Expense();
				Date currentDate = new Date();
				e.setDate(currentDate);
				e.setProduct(p);
				if (isNumeric(coinPrice)) {
					
					int position = coinPrice.indexOf(".");
					int length = coinPrice.length();
					int factor = 1;
					if (position == -1 ){
						factor = 100;
					}else if (length - position == 2){
						factor = 10;
					}else if (length - position == 3){
						factor = 1;
					} 
					coinPrice = coinPrice.replaceAll("\\.", ""); 
					e.setPrice (Integer.parseInt(coinPrice) * factor); 					

				}else{
					pricetext.setTextColor(Color.RED);
					return;
				}	
				
				try {
					MainActivity.databaseHelper.getProductDao().create(p);
					MainActivity.databaseHelper.getExpenseDao().create(e);
				} catch (SQLException e1) {
					// TODO: process exception DB
					e1.printStackTrace();
				}
				
				updateBalance(view);
				showList(view);
				producttext.getText().clear();
				pricetext.getText().clear();
				

	}
	private boolean isNumeric(String str) {
		  return str.matches("\\d*\\.?\\d{1,2}"); 
		}

	public void updateBalance(View view) {
		String sum = "0";
		try {
			GenericRawResults<String[]> qRes = MainActivity.databaseHelper.getExpenseDao().queryRaw("select sum(price) from expenses");
			sum = qRes.getFirstResult()[0];
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.w("MSaver", "Sum is '" + sum + "'");
		// TODO: Income update only after pressing  button Add in expense  - income window 
		TextView sumview = (TextView) view.findViewById(R.id.incomeBalance);
		sum = addingDotToString(sum);
		sumview.setText(sum);
		if ( sum.charAt(0) == '-' ){
			sumview.setBackgroundColor(Color.RED);
		} else {
			sumview.setBackgroundColor(Color.GREEN);
		}
			
	}

	public String addingDotToString(String num) {
		if (num == "0" || num.isEmpty()) {
			return num;
		}
		boolean neg = num.charAt(0) == '-';
		String prepend = "";
		if (neg) {
			num = num.substring(1);
			prepend = "-";
		}
		int lenNum = num.length();
		if (lenNum == 1) {
			return new StringBuilder().append(prepend).append("0.0").append(num).toString();
		} else if (lenNum == 2) {
			return new StringBuilder().append(prepend).append("0.").append(num).toString();
		} else {
			return new StringBuilder().append(prepend).append(num.substring(0, lenNum-2))
						.append('.').append(num.substring(lenNum - 2)).toString();
		}
	}


}
