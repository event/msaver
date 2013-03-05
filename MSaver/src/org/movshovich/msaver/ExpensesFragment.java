package org.movshovich.msaver;

import java.sql.SQLException;
import java.util.Date;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
		//TODO: show and update list of last N expenses
		//TODO: make balance field wider (add some padding)
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
		//TODO: show hint list when typing in product name 
	}
	
	private void onAddClick(View view) {
		// TODO: integer numbers must be multiplied by 100
		// TODO: reuse old products - don't create new products on every save
		// TODO: when adding new product it should ask for some properties (i.e. category, shmategorey, etc.)
		EditText producttext = (EditText) view.findViewById(R.id.expenseProductEnter);
		EditText pricetext = (EditText) view.findViewById(R.id.expensePriceEnter);
			
		String coinPrice = pricetext.getText().toString();
		String price = coinPrice;
		//Log.w("MSaver", price);
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
			e.setPrice (-Integer.parseInt(coinPrice) * factor); 					

			pricetext.setTextColor(Color.BLACK);
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
		producttext.getText().clear();
		pricetext.getText().clear();
		
		updateBalance(view);
	}
	
	private boolean isNumeric(String str) {
	  return str.matches("\\d*\\.?\\d{1,2}"); 
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
		Log.w("MSaver", "Sum is '" + sum + "'");
		TextView sumview = (TextView) view.findViewById(R.id.expenseBalance);
		Integer lengthSum = sum.length();
		String preSum = sum.substring(0, lengthSum-2); 
		String postSum = sum.substring(lengthSum - 2);
		sum = preSum + "." + postSum;
		sumview.setText(sum);
		if (Float.parseFloat(sum) < 0 ){
			sumview.setBackgroundColor(Color.RED);
		} else {
			sumview.setBackgroundColor(Color.GREEN);
		}
			
	}
	
}
