package com.mobisys.android.ibp.database;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.mobisys.android.ibp.models.Category;

import android.annotation.SuppressLint;
import android.content.Context;

public class CategoriesTable {

	public static int createOrUpdateCategory(Context context, Category category){
		CreateOrUpdateStatus status=null;
		int count = -1;
		try {
			status = DatabaseHelper.getInstance(context).getCategoryDao().createOrUpdate(category);
		} catch (SQLException e) {
		}

		if(status!=null) count = status.getNumLinesChanged();
		return count;
	}
	
	@SuppressLint("UseValueOf")
	public static List<Category> getAddedCategories(Context context){
		List<Category> categories = null;
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("added", new Boolean(true));
			categories = DatabaseHelper.getInstance(context).getCategoryDao().queryForFieldValues(map);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return categories;
	}
	
	@SuppressLint("UseValueOf")
	public static List<Category> getParticularCategory(Context context, int cat_id){
		List<Category> categories = null;
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", new Integer(cat_id));
			categories = DatabaseHelper.getInstance(context).getCategoryDao().queryForFieldValues(map);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return categories;
	}
	
	public static List<Category> getAllCategories(Context context){
		List<Category> categories = null;
		try {
			categories = DatabaseHelper.getInstance(context).getCategoryDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categories;
	}
}
