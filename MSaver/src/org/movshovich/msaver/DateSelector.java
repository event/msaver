package org.movshovich.msaver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class DateSelector extends TextView {

	private static final java.text.DateFormat FORMAT = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT);
	private OnDateSetListener odsl = null;
	public DateSelector(Context context) {
		super(context);
		init();
	}

	private void init() {
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDateSelectorDialog(v);
			}
		});
		update(new Date());
	}

	public DateSelector(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public DateSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void update(Date date) {
		setText(FORMAT.format(date));
	}

	protected void showDateSelectorDialog(View v) {
		Calendar cal = Calendar.getInstance();
		DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				Calendar cal = Calendar.getInstance();
				cal.set(year, monthOfYear, dayOfMonth);
				update(cal.getTime());
				if (odsl != null) {
					odsl.onDateSet(view, year, monthOfYear, dayOfMonth);
				}
			}
		}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
		datePickerDialog.show();
	}

	public void setOnDateSetListener(OnDateSetListener odsl) {
		this.odsl = odsl;
	}
	
	

}
