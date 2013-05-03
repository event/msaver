package org.movshovich.msaver;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "products")
public class Product {
	@DatabaseField(generatedId = true)
	private int id;
	@DatabaseField
	private String name;
	@DatabaseField
	private boolean inShoppingList;
	@DatabaseField(canBeNull=false, foreign = true)
	private Category category;
	
	
	public Product() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public boolean isInShoppingList() {
		return inShoppingList;
	}
	public void setInShoppingList(boolean inShoppingList) {
		this.inShoppingList = inShoppingList;
	}

}
