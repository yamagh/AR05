package jp.ygmh.ar05.client;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ARDbOpenHelper extends SQLiteOpenHelper {

	static final String _ARDB = "ar.db";
	static final String _TABLE = "marker_table";
	static final String _COL_DISP_IMG = "image";
	static final int _DB_VERSION = 1;
	static final String CREATE_TABLE = "CREATE TABLE " + _TABLE + " ( _id integer primary key autoincrement, " + _COL_DISP_IMG + " blob);";
	static final String DROP_TABLE = "DROP TABLE marker_table;";
	
	public ARDbOpenHelper(Context c){
		super(c, _ARDB, null, _DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DROP_TABLE);
		onCreate(db);
	}
}
