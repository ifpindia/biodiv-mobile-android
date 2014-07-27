package com.mobisys.android.ibp.database;

import java.sql.SQLException;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mobisys.android.ibp.models.Category;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper{

    private static DatabaseHelper mInstance;

	private static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "data";

	public static final String KEY_ID = "_id";
	
	public static final String TYPE_TEXT = " text";
	public static final String TYPE_INT = " integer";
	public static final String TYPE_REAL = " real";
	public static final String CREATE_TABLE = "create table ";
	public static final String PRIMARY_KEY = " (_id integer primary key autoincrement, ";
	private Dao<Category, Integer> mCategoryDao;
	
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
    
    @Override
    public void close(){
    	super.close();
    	mCategoryDao = null;
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
	}
	
	public void createTablesIfNotExists(){
		try {
			TableUtils.createTableIfNotExists(connectionSource, Category.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
