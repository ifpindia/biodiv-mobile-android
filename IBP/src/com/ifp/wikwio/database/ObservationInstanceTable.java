package com.ifp.wikwio.database;

import java.sql.SQLException;
import java.util.List;

import com.ifp.wikwio.Preferences;
import com.ifp.wikwio.models.ObservationInstance;
import com.ifp.wikwio.models.ObservationInstance.StatusType;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.table.TableUtils;

import android.content.Context;
import android.util.Log;

public class ObservationInstanceTable {
	public static int createEntryInTable(Context context, ObservationInstance sp){
		int status=-1;
		try {
			status = DatabaseHelper.getInstance(context).getSaveParamsDao().create(sp);
		} catch (SQLException e) {
		}
		return status;
	}
	
	
	public static List<ObservationInstance> getAllRecords(Context context){
		List<ObservationInstance> records = null;
		try {
			records = DatabaseHelper.getInstance(context).getSaveParamsDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return records;
	}
	
	public static ObservationInstance getFirstRecord(Context context){
		ObservationInstance record = null;
		try {
			QueryBuilder<ObservationInstance, Integer> query = DatabaseHelper.getInstance(context).getSaveParamsDao().queryBuilder();
			query.where().eq("status", StatusType.PENDING);
			record = DatabaseHelper.getInstance(context).getSaveParamsDao().queryForFirst(query.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return record;
	}

	public static boolean isRecordAvailable(Context context,ObservationInstance sp){
		List<ObservationInstance> record = null;
		try {
			QueryBuilder<ObservationInstance, Integer> query = DatabaseHelper.getInstance(context).getSaveParamsDao().queryBuilder();
			if(sp.getId()==-1)
				query.where().eq("server_id", sp.getServer_id()).and().eq("id", sp.getId());//Obv id
			else
				query.where().eq("id", sp.getId());//Obv id
			record = DatabaseHelper.getInstance(context).getSaveParamsDao().query(query.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return record.size()>0;
	}

	public static void deleteRowFromTable(Context context, ObservationInstance sp) {
		try {
			DatabaseHelper.getInstance(context).getSaveParamsDao().delete(sp);
			if(Preferences.DEBUG) Log.d("SaveParamsTable", "******Record deleted from table");
			if(Preferences.DEBUG) Log.d("SaveParamsTable", "********No of records in database"+ObservationInstanceTable.getNoOfRowsInTable(context));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static long getNoOfRowsInTable(Context context) throws SQLException{
		return DatabaseHelper.getInstance(context).getSaveParamsDao().queryBuilder().countOf();
	}


	public static void updateRowFromTable(Context context, ObservationInstance sp) {
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
			Dao<ObservationInstance, Integer> dao = DatabaseHelper.getInstance(context).getSaveParamsDao();
			TableUtils.clearTable(dao.getConnectionSource(), ObservationInstance.class);
		} catch (SQLException e) {
			count=-1;
		}
		return count;
	}

	public static void updateRowFromTable2(Context context, ObservationInstance sp) {
		Dao<ObservationInstance, Integer> dao;
		try {
			dao =DatabaseHelper.getInstance(context).getSaveParamsDao();
			UpdateBuilder<ObservationInstance, Integer> query = dao.updateBuilder();
			//query.updateColumnValue("group_id", sp.getGroupId());
			query.updateColumnValue("group", sp.getGroup());
			query.updateColumnValue("habitat_id", sp.getHabitatId());
			query.updateColumnValue("fromDate", sp.getFromDate());
			query.updateColumnValue("placeName", sp.getPlaceName());
			query.updateColumnValue("areas", sp.getAreas());
			query.updateColumnValue("maxVotedReco", sp.getMaxVotedReco());
			//query.updateColumnValue("recoName", sp.getRecoName());
			
			query.updateColumnValue("resource", sp.getResource());
			//query.updateColumnValue("image_type", sp.getImageType());
			query.updateColumnValue("status", sp.getStatus());
			query.updateColumnValue("message", sp.getMessage());
			query.updateColumnValue("notes", sp.getNotes());
			query.updateColumnValue("userGroupsList", sp.getUserGroupsList());
			if(sp.getId()==-1)
				query.where().eq("server_id", sp.getServer_id()).and().eq("id", sp.getId());
			else
				query.where().eq("id", sp.getId());
			int count=query.update();
			Log.d("ObservationParamTable", "Updated "+count+" orders");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static long getNoOfIncompleteObservations(Context context ){
		/*List<ObservationInstance> record = null;
		try {
			QueryBuilder<ObservationInstance, Integer> query = DatabaseHelper.getInstance(context).getSaveParamsDao().queryBuilder();
			query.where().eq("status", StatusType.INCOMPLETE);
			record = DatabaseHelper.getInstance(context).getSaveParamsDao().query(query.prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(record!=null && record.size()>0)
			return record.size();
		else 
			return 0;*/
		long count=0;
		try {
			count = DatabaseHelper.getInstance(context).getSaveParamsDao().queryBuilder().where().eq("status", StatusType.INCOMPLETE).countOf();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
}
