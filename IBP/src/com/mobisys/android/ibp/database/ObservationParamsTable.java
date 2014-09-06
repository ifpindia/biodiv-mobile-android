package com.mobisys.android.ibp.database;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import com.mobisys.android.ibp.Preferences;
import com.mobisys.android.ibp.models.ObservationParams;
import com.mobisys.android.ibp.models.ObservationParams.StatusType;

import android.content.Context;
import android.util.Log;

public class ObservationParamsTable {
	public static int createEntryInTable(Context context, ObservationParams sp){
		int status=-1;
		try {
			status = DatabaseHelper.getInstance(context).getSaveParamsDao().create(sp);
		} catch (SQLException e) {
		}
		return status;
	}
	
	
	public static List<ObservationParams> getAllRecords(Context context){
		List<ObservationParams> records = null;
		try {
			records = DatabaseHelper.getInstance(context).getSaveParamsDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return records;
	}
	
	public static ObservationParams getFirstRecord(Context context){
		ObservationParams record = null;
		try {
			QueryBuilder<ObservationParams, Integer> query = DatabaseHelper.getInstance(context).getSaveParamsDao().queryBuilder();
			query.where().eq("status", StatusType.PENDING);
			record = DatabaseHelper.getInstance(context).getSaveParamsDao().queryForFirst(query.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return record;
	}


	public static void deleteRowFromTable(Context context, ObservationParams sp) {
		try {
			DatabaseHelper.getInstance(context).getSaveParamsDao().delete(sp);
			if(Preferences.DEBUG) Log.d("SaveParamsTable", "******Record deleted from table");
			if(Preferences.DEBUG) Log.d("SaveParamsTable", "********No of records in database"+ObservationParamsTable.getNoOfRowsInTable(context));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static long getNoOfRowsInTable(Context context) throws SQLException{
		return DatabaseHelper.getInstance(context).getSaveParamsDao().queryBuilder().countOf();
	}


	public static void updateRowFromTable(Context context, ObservationParams sp) {
		try {
			int count = DatabaseHelper.getInstance(context).getSaveParamsDao().update(sp);
			Log.d("OrderTable", "Updated "+count+" orders");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int deleteAllOrders(Context context){
		int count = -1;
		try {
			Dao<ObservationParams, Integer> dao = DatabaseHelper.getInstance(context).getSaveParamsDao();
			TableUtils.clearTable(dao.getConnectionSource(), ObservationParams.class);
		} catch (SQLException e) {
			count=-1;
		}
		return count;
	}
	
}
