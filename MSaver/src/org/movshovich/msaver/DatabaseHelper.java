package org.movshovich.msaver;


import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "helloAndroid.db";
	
	private static final int DATABASE_VERSION = 1;

	private Dao<Expense, Integer> expenseDao;

	private Dao<Product, Integer> productDao;

	private Dao<Category, Integer> categoryDao;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Product.class);
			TableUtils.createTable(connectionSource, Expense.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, Product.class, true);
			TableUtils.dropTable(connectionSource, Expense.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}	
	}
	
	public Dao<Expense, Integer> getExpenseDao() {
		if (expenseDao == null) {
			try {
				expenseDao = getDao(Expense.class);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return expenseDao;
	}
	public Dao<Product, Integer> getProductDao() {
		if (productDao == null) {
			try {
				productDao = getDao(Product.class);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return productDao;
	}

	public Dao<Category, Integer> getCategoryDao() {
		if (categoryDao == null) {
			try {
				categoryDao = getDao(Category.class);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return categoryDao;
	}


}
