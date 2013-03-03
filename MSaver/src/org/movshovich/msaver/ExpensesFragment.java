package org.movshovich.msaver;

import java.sql.SQLException;
import java.util.Date;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ExpensesFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.expenses, container, false);
		addListeners(view);
		updateBalance(view);
		return view;
	}

	private void addListeners(final View view) {
		final Button addButton = (Button)  view.findViewById(R.id.expenseAdd);
		if (addButton != null) {
			addButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onAddClick(view);
				}
			});
		}

	}
	
	private void onAddClick(View view) {
		Product p = new Product();
		EditText producttext = (EditText) view.findViewById(R.id.expenseProductEnter);
		p.setName(producttext.getText().toString());
		
		Expense e = new Expense();
		Date currentDate = new Date();
		e.setDate(currentDate);
		e.setProduct(p);
		EditText pricetext = (EditText) view.findViewById(R.id.expensePriceEnter);
		e.setPrice (Integer.parseInt(pricetext.getText().toString())); //TODO: support numbers with decimal parts 
		
		try {
			MainActivity.databaseHelper.getProductDao().create(p);
			MainActivity.databaseHelper.getExpenseDao().create(e);
		} catch (SQLException e1) {
			// TODO: process exception DB
			e1.printStackTrace();
		}
		producttext.getText().clear();
		pricetext.getText().clear();
		
		updateBalance(view);

	}

	private void updateBalance(View view) {
		String sum = "0";
		try {
			GenericRawResults<String[]> qRes = MainActivity.databaseHelper.getExpenseDao().queryRaw("select sum(price) from expenses");
			sum = qRes.getFirstResult()[0];
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TextView sumview = (TextView) view.findViewById(R.id.expenseBalance);
		sumview.setText(sum);
	}

}
