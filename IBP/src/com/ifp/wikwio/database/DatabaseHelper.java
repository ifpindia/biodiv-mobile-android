package com.ifp.wikwio.database;

import java.sql.SQLException;

import com.ifp.wikwio.models.Category;
import com.ifp.wikwio.models.ObservationInstance;
import com.ifp.wikwio.models.Resource;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{

    private static DatabaseHelper mInstance;

	private static final int DATABASE_VERSION = 4;
	public static final String DATABASE_NAME = "data";

	public static final String KEY_ID = "_id";
	
	public static final String TYPE_TEXT = " text";
	public static final String TYPE_INT = " integer";
	public static final String TYPE_REAL = " real";
	public static final String CREATE_TABLE = "create table ";
	public static final String PRIMARY_KEY = " (_id integer primary key autoincrement, ";
	
	private Dao<Category, Integer> mCategoryDao;
	//private Dao<ObservationParams, Integer> mSaveParamsDao;
	private Dao<ObservationInstance, Integer> mSaveParamsDao;
	
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    public static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = OpenHelperManager.getHelper(context, DatabaseHelper.class);;
            mInstance.createTablesIfNotExists();
        }
        return mInstance;
    }
 
    public Dao<Category, Integer> getCategoryDao() throws java.sql.SQLException {
    	if(mCategoryDao==null){
    		mCategoryDao = getDao(Category.class);
    	}
    	return mCategoryDao;
    }
    
    /*public Dao<ObservationParams, Integer> getSaveParamsDao() throws java.sql.SQLException {
    	if(mSaveParamsDao==null){
    		mSaveParamsDao = getDao(ObservationParams.class);
    	}
    	return mSaveParamsDao;
    }*/
    
    public Dao<ObservationInstance, Integer> getSaveParamsDao() throws java.sql.SQLException {
    	if(mSaveParamsDao==null){
    		mSaveParamsDao = getDao(ObservationInstance.class);
    	}
    	return mSaveParamsDao;
    }
    
    @Override
    public void close(){
    	super.close();
    	mCategoryDao = null;
    	mSaveParamsDao = null;
    }
	
	public boolean isTableExist(SQLiteDatabase db, String table_name){
	    return false;
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		createTablesIfNotExists();
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion,int newVersion) {
		switch(oldVersion+1){
		case 4:
			try {
				mSaveParamsDao = getDao(ObservationInstance.class);
				
				if(mSaveParamsDao != null)
					mSaveParamsDao.executeRaw("ALTER TABLE observations ADD COLUMN userGroupsList TEXT;");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void createTablesIfNotExists(){
		try {
			TableUtils.createTableIfNotExists(connectionSource, Category.class);
			TableUtils.createTableIfNotExists(connectionSource, ObservationInstance.class);
			TableUtils.createTableIfNotExists(connectionSource, Resource.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
