package com.clara.hellosqlite;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ProductsActivity extends AppCompatActivity {

	//Simple app which uses a SQLite Database
	//Product info database
	//User types in a product name (string) and quantity in stock (int) and presses a button to save
	//App records these in a database
	//App has a search button to look for a specific product
	//TODO keep list up to date
	//TODO delete a product - long press on list
	//TODO update a product quantity - add button

	EditText productNameET;
	EditText productQuantityET;
	EditText searchNameET;
	EditText updateProductQuantityET;

	TextView productSearchTV;
	ListView allProductsListView;
	ArrayAdapter<Product> allProductsListAdapter;
	
	Button addProductButton;
	Button searchProductsButton;
	Button updateQuantityButton;  //TODO add to ppt and template


	private DatabaseManager dbManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_products);

		//Create database manager
		dbManager = new DatabaseManager(this);

		productNameET = (EditText)findViewById(R.id.add_new_product_name_et);
		productQuantityET = (EditText)findViewById(R.id.add_new_product_quantity_et);
		searchNameET = (EditText)findViewById(R.id.search_et);
		updateProductQuantityET = (EditText)findViewById(R.id.update_quantity_et);

		addProductButton = (Button)findViewById(R.id.add_product_button);
		searchProductsButton = (Button)findViewById(R.id.search_products_button);
		updateQuantityButton = (Button)findViewById(R.id.update_quantity_button);

		allProductsListView = (ListView)findViewById(R.id.all_products_listview);
		allProductsListAdapter = new ArrayAdapter<Product>(this, R.layout.list_item);
		allProductsListView.setAdapter(allProductsListAdapter);

		updateProductsListView();


		addProductButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String newName = productNameET.getText().toString();
				String newQuantity = productQuantityET.getText().toString();

				if ( newName.length() == 0  || !newQuantity.matches("^\\d+$")) {   //regex validation
					Toast.makeText(ProductsActivity.this, "Please enter a product name and numerical quantity",
							Toast.LENGTH_LONG).show();
					return;
				}

				int quantity = Integer.parseInt(newQuantity);

				if (dbManager.addProduct(newName, quantity)) {
					Toast.makeText(ProductsActivity.this, "Product added to database", Toast.LENGTH_LONG).show();

					//Clear form and update ListView
					productNameET.getText().clear();
					productQuantityET.getText().clear();
					updateProductsListView();
				} else {
					//Duplicate product name
					Toast.makeText(ProductsActivity.this, newName +" is already in the database",
							Toast.LENGTH_LONG).show();
				}
			}
		});


		searchProductsButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String searchName = searchNameET.getText().toString();
				if ( searchName.equals("")) {
					Toast.makeText(ProductsActivity.this, "Please enter a product to search for",
							Toast.LENGTH_LONG).show();
					return;
				}

				int quantity = dbManager.getQuantityForProduct(searchName);

				if (quantity == -1) {
					//Product not found
					Toast.makeText(ProductsActivity.this, "Product " + searchName + " not found",
							Toast.LENGTH_LONG).show();
				} else {
					updateProductQuantityET.setText(Integer.toString(quantity));
				}
			}
		});

		updateQuantityButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO. Ensure a product is selected and new quantity provided

				int newquantity = Integer.parseInt(updateProductQuantityET.getText().toString());
				String productName = searchNameET.getText().toString();

				//todo

				//todo method in DBmanager to do the update. Case sensitive.
			}
		});


		//TODO listview's OnItemLongPressListener to delete product.
		allProductsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				//TODO show confirmation, then delete item
				return false;
			}
		});

	}

	private void updateProductsListView() {

		ArrayList<Product> allProd = dbManager.fetchAllProducts();
		allProductsListAdapter.clear();
		allProductsListAdapter.addAll(allProd);
		allProductsListAdapter.notifyDataSetChanged();
	}


	//override onPause method to close database as Activity pauses
	@Override
	protected void onPause(){
		super.onPause();
		dbManager.close();
	}


}
