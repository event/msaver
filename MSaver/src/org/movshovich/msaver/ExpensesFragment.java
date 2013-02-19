package org.movshovich.msaver;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ExpensesFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.expenses, container, false);
		addListeners(view);
		return view;
	}

	private void addListeners(View view) {
		final Button addButton = (Button)  view.findViewById(R.id.expenseAdd);
		if (addButton != null) {
			addButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					addButton.setText("OK");
				}
			});
		}

	}

}
