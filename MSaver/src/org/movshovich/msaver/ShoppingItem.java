package org.movshovich.msaver;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "shopping")
public class ShoppingItem {
	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(canBeNull=false, foreign = true)
	private Product product;
	
	@DatabaseField
	private boolean checked;
	
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public ShoppingItem() {
		super();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
