package org.movshovich.msaver;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "transactions")
public class Transaction {
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(dataType = DataType.DATE_LONG)
	private Date date;
	
	@DatabaseField(canBeNull=false, foreign = true)
	private Product product;
	
	@DatabaseField
	private int price;
	
	public Transaction() {
		super();
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
