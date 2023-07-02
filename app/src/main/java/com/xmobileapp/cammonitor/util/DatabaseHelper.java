package com.xmobileapp.cammonitor.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xmobileapp.cammonitor.R;
import com.xmobileapp.cammonitor.config.CamMonitorParameter;

public class DatabaseHelper{

	
	static class Helper extends SQLiteOpenHelper {
		protected final static String TAG ="DatabaseHelper";
		private final static String DATABASE_NAME="CAMMONITOR_CLIENT";
		private final static int DATABASE_VERSION = 1;
		private Context context;
		public Helper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);  
			this.context = context;
		}

		
		
		public void onCreate(SQLiteDatabase db) {
			try{
				String sql =context.getString(R.string.table_sql);
				db.execSQL(sql);
				Log.i(TAG, sql);
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = context.getString(R.string.drop_sql);
			db.execSQL(sql);
			this.onCreate(db);
			
		}
		
	}

	private Context context;
	protected SQLiteDatabase db;
	public DatabaseHelper(Context context) {
		this.context = context;
		db = new Helper(context).getWritableDatabase();;
	}

	
	public static CamMonitorParameter query(Context context,int id) throws Exception{
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();
			String whereClause = " _id = ?";  
			String[] whereArgs = new String[] {String.valueOf(id)}; 
			String[] columns = new String[]{
				"name",	"ip","port","username","password","client_dir","connect_type"
			};
			
			Cursor cursor =	db.query("tb_cammonitor_configs", columns, whereClause, whereArgs, null, null, null);
			if(cursor.getCount()==0){
				throw new Exception("没有找到ID"+id+"的数据");
			}
			cursor.moveToFirst();
			CamMonitorParameter param = new CamMonitorParameter();
			param.setId(id);
			param.setName(cursor.getString(0));
			param.setIp(cursor.getString(1));
			param.setPort(cursor.getInt(2));
			param.setUsername(cursor.getString(3));
			param.setPassword(cursor.getString(4));
			param.setLocal_dir(cursor.getString(5));
			return param;
		}catch (Exception e) {
			throw e;
		}finally{
			if(db!=null){
				db.close();
			}
		}
	}
	public static long testInsert(Context context){
		
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();
			ContentValues values = new ContentValues();
	//		values.put("_id", -1);
			values.put("name", "test1");
			values.put("port", 21);
			values.put("ip", "192.168.18.3");
			values.put("username", "test");
			values.put("password", "test");
			values.put("client_dir", "test");
			long num = db.insert("tb_cammonitor_configs", null, values);
			return num;
		}finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	

	public static long insert(Context context,String table,ContentValues values) throws Exception{
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();
			long num = db.insert(table, null, values);
			return num;
		}catch (Exception e) {
			throw e;
		}
		finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	public static long update(Context context,String table,ContentValues values,int id) throws Exception{
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();
			String whereClause = " _id = ?";  
			String[] whereArgs = new String[] {String.valueOf(id)}; 
			long num = db.update(table, values, whereClause, whereArgs);
			return num;
		}catch (Exception e) {
			throw e;
		}
		finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	
	
	public static void testDelete(Context context){
		SQLiteDatabase db = null;
		try{
			Helper helper = new Helper(context);
			db  = helper.getWritableDatabase();
			db.execSQL("delete from tb_cammonitor_configs;");
		}finally{
			if(db!=null){
				db.close();
			}
		}
		
	}
	
	public static void delete(Context context,int id) throws Exception{
		SQLiteDatabase db = null;
		try{
			Helper helper = new Helper(context);
			db  = helper.getWritableDatabase();
			String table = "tb_cammonitor_configs";  
			String whereClause = " _id = ?";  
			String[] whereArgs = new String[] {String.valueOf(id)};  
			db.delete(table, whereClause, whereArgs);
		}catch (Exception e) {
			throw e;
		}
		finally{
			if(db!=null){
				db.close();
			}
		}
		
	}
	
	public static void drop(Context context){
		SQLiteDatabase db = null;
		try{
			db = new Helper(context).getWritableDatabase();
			String sql =context.getString(R.string.drop_sql);
			db.execSQL(sql);
		}finally{
			if(db!=null){
				db.close();
			}
		}
		
	}
	
	public static int getCount(Context context ,String table) throws Exception{
		SQLiteDatabase db =  null;
		try{
			db = new Helper(context).getReadableDatabase();
			Cursor cur = db.query(table, new String[]{"_id","name"}, null, null, null, null, null);
			return cur.getCount();
		}catch (Exception e) {
			throw e;
		}finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	public Cursor loadAllName() throws Exception{
		try{
			Cursor cur = db.query("tb_cammonitor_configs", new String[]{"_id","name"}, null, null, null, null, "_id DESC");
			return cur;
		}catch (Exception e) {
			throw e;
		}
	}
	
	
	public void close(){
		if(this.db!=null){
			this.db.close();
		}
	}
	
	
	public static List<String> loadName(Context context) throws Exception{
		SQLiteDatabase db =  null;
		List<String> rst = new ArrayList<String>();
		try{
			db = new Helper(context).getReadableDatabase();
			Cursor cur =db.query("tb_cammonitor_configs", new String[]{"_id","name"}, null, null, null, null, null);
			cur.moveToFirst();  
			for (int i = 0; i < cur.getCount(); i++) {  
				String s = cur.getString(1);  
				rst.add(s);
				cur.moveToNext();  
			}  
			return rst;
		}catch (Exception e) {
			throw e;
		}finally{
			if(db!=null){
				db.close();
			}
		}
	}
	
	public Cursor query(int id) throws Exception{
		try{
			String whereClause = " _id = ?";  
			String[] whereArgs = new String[] {String.valueOf(id)};  
			Cursor cur = db.query("tb_cammonitor_configs", new String[]{"_id","name","ip","port","username","password","client_dir"}, whereClause, whereArgs, null, null, "_id DESC");
			return cur;
		}catch (Exception e) {
			throw e;
		}
	}
}
