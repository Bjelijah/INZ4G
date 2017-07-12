package com.inz.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.inz.bean.Custom;
import com.inz.bean.UserLoginDBBean;

import java.util.ArrayList;
import java.util.List;


public class UserLoginDao {
	DBHelper dbHelper;
	SQLiteDatabase db;
	
	
	//创建数据库
	public UserLoginDao(Context context,String name,int version){
		dbHelper = new DBHelper(context, name, null, version);
	}
	
	public void insert(UserLoginDBBean info){
		db = dbHelper.getWritableDatabase();
		int custom = info.getC().isCustom()?1:0;
		int ssl = info.getC().isSSL()?1:0;
		String sql = "insert into userinfo (num,username,useremail,userpassword,custom,ip,port,ssl)values(?,?,?,?,?,?,?,?);";
		db.execSQL(sql,new Object[]{info.getUserNum(),info.getUserName(),info.getUserEmail(),info.getUserPassword(),
				custom,info.getC().getCustomIP(),info.getC().getCustomPort(),ssl});
	}
	
	public void updataById(int id,UserLoginDBBean info){
		db = dbHelper.getWritableDatabase();
		String sql = "update userinfo set num=?,username=?,useremail = ?,userpassword= ? where id=?;";
		db.execSQL(sql, new Object[]{info.getUserNum(),info.getUserName(),info.getUserEmail(),info.getUserPassword(),id});
	}
	
	public void updataByNum(UserLoginDBBean info){
		int custom = info.getC().isCustom()?1:0;
		int ssl = info.getC().isSSL()?1:0;
		db = dbHelper.getWritableDatabase();
		String sql = "update userinfo set num=?,username=?,useremail=?,userpassword=?,custom=?,ip=?,port=?,ssl=? where num=?;";
		db.execSQL(sql, new Object[]{info.getUserNum(),info.getUserName(),info.getUserEmail(),info.getUserPassword()
				,custom,info.getC().getCustomIP(),info.getC().getCustomPort(),ssl
				,info.getUserNum()});
	}

	public void updataByName(UserLoginDBBean info){
		int custom = info.getC().isCustom()?1:0;
		int ssl = info.getC().isSSL()?1:0;
		db = dbHelper.getWritableDatabase();
		String sql = "update userinfo set num=?,username=?,useremail=?,userpassword=?,custom=?,ip=?,port=?,ssl=? where username=?;";
		db.execSQL(sql, new Object[]{info.getUserNum(),info.getUserName(),info.getUserEmail(),info.getUserPassword()
				,custom,info.getC().getCustomIP(),info.getC().getCustomPort(),ssl
				,info.getUserNum()});
	}


	public void deleteById(int id){
		db = dbHelper.getWritableDatabase();
		String sql = "delete from userinfo where id=?;";
		db.execSQL(sql, new Object[]{id});
	}
	
	public void deleteByNum(int num){
		db = dbHelper.getWritableDatabase();
		String sql = "delete from userinfo where num=?;";
		db.execSQL(sql, new Object[]{num});
	}
	
	public void deleteAll(){
		db = dbHelper.getWritableDatabase();
		String sql = "delete  from userinfo;";
		db.execSQL(sql, new Object[]{});
	}
	
	
	public List<UserLoginDBBean> queryAll(){
		List<UserLoginDBBean> data = new ArrayList<UserLoginDBBean>();
		db = dbHelper.getWritableDatabase();
		String sql = "select * from userinfo order by id asc";
		Cursor cursor = db.rawQuery(sql, null);
		while(cursor.moveToNext()){
			Custom c = new Custom();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int num = cursor.getInt(cursor.getColumnIndex("num"));
			String userName = cursor.getString(cursor.getColumnIndex("username"));
			String userEmail = cursor.getString(cursor.getColumnIndex("useremail"));
			String userPassword = cursor.getString(cursor.getColumnIndex("userpassword"));
			c.setCustom(cursor.getInt(cursor.getColumnIndex("custom"))==1?true:false);
			c.setCustomIP(cursor.getString(cursor.getColumnIndex("ip")));
			c.setCustomPort(cursor.getInt(cursor.getColumnIndex("port")));
			c.setSSL(cursor.getInt(cursor.getColumnIndex("ssl"))==1?true:false);
			UserLoginDBBean info = new UserLoginDBBean(num, userName, userPassword,userEmail,c);
			data.add(info);
		}
		return data;
	}
	
	public boolean findByNum(int userNum){
		boolean result = false;
		db = dbHelper.getWritableDatabase();
		String sql = "select * from userinfo where num=?;";
		Cursor cursor = db.rawQuery(sql, new String[]{userNum+""});
		if (cursor.moveToNext()) {
			result = true;
		}
		return result;
	}




	public boolean findByName(String userName){
		Log.e("123","dbHelper="+dbHelper+"  username="+userName);
		if (dbHelper==null){
			Log.e("123","dbHelp==null");
		}
		db = dbHelper.getWritableDatabase();

		if (db == null){
			Log.e("123","db==null");
		}
		String sql = "select * from userinfo where username=?;";
		Cursor cursor = db.rawQuery(sql, new String[]{userName+""});
		return cursor.moveToNext()?true:false;
	}

	public boolean findByNameAndIP(String userName,String ip){
		db = dbHelper.getWritableDatabase();
		String sql = "select * from userinfo where username=? and ip=?;";
		Cursor cursor = db.rawQuery(sql, new String[]{userName+"",ip+""});
		return cursor.moveToNext()?true:false;
	}





	public List<UserLoginDBBean> queryByNum(int userNum){
		db = dbHelper.getWritableDatabase();
		List<UserLoginDBBean> data = new ArrayList<UserLoginDBBean>();
		String sql = "select * from userinfo where num=?;";
		Cursor cursor = db.rawQuery(sql, new String[]{userNum+""});
		while(cursor.moveToNext()){
			Custom c = new Custom();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int num = cursor.getInt(cursor.getColumnIndex("num"));
			String userName = cursor.getString(cursor.getColumnIndex("username"));
			String userPassword = cursor.getString(cursor.getColumnIndex("userpassword"));
			String userEmail = cursor.getString(cursor.getColumnIndex("useremail"));
			c.setCustom(cursor.getInt(cursor.getColumnIndex("custom"))==1?true:false);
			c.setCustomIP(cursor.getString(cursor.getColumnIndex("ip")));
			c.setCustomPort(cursor.getInt(cursor.getColumnIndex("port")));
			c.setSSL(cursor.getInt(cursor.getColumnIndex("ssl"))==1?true:false);
			UserLoginDBBean info = new UserLoginDBBean(num, userName, userPassword,userEmail,c);
			data.add(info);
		}
		return data;
	}

	public List<UserLoginDBBean> queryByName(String userName){
		db = dbHelper.getWritableDatabase();
		List<UserLoginDBBean> data = new ArrayList<UserLoginDBBean>();
		String sql = "select * from userinfo where username=?;";
		Cursor cursor = db.rawQuery(sql, new String[]{userName+""});
		while (cursor.moveToNext()){
			Custom c = new Custom();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int num = cursor.getInt(cursor.getColumnIndex("num"));
			String name = cursor.getString(cursor.getColumnIndex("username"));
			String pwd = cursor.getString(cursor.getColumnIndex("userpassword"));
			String email = cursor.getString(cursor.getColumnIndex("useremail"));
			c.setCustom(cursor.getInt(cursor.getColumnIndex("custom"))==1?true:false);
			c.setCustomIP(cursor.getString(cursor.getColumnIndex("ip")));
			c.setCustomPort(cursor.getInt(cursor.getColumnIndex("port")));
			c.setSSL(cursor.getInt(cursor.getColumnIndex("ssl"))==1?true:false);
			UserLoginDBBean info = new UserLoginDBBean(num,name,pwd,email,c);
			data.add(info);

		}
		return data;
	}

	public List<UserLoginDBBean> queryByNameAndIP(String userName,String ip) {
		db = dbHelper.getWritableDatabase();
		List<UserLoginDBBean> data = new ArrayList<UserLoginDBBean>();
		String sql = "select * from userinfo where username=? and ip=?;";
		Cursor cursor = db.rawQuery(sql, new String[]{userName + "", ip + ""});
		while (cursor.moveToNext()) {
			Custom c = new Custom();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int num = cursor.getInt(cursor.getColumnIndex("num"));
			String name = cursor.getString(cursor.getColumnIndex("username"));
			String pwd = cursor.getString(cursor.getColumnIndex("userpassword"));
			String email = cursor.getString(cursor.getColumnIndex("useremail"));
			c.setCustom(cursor.getInt(cursor.getColumnIndex("custom")) == 1 ? true : false);
			c.setCustomIP(cursor.getString(cursor.getColumnIndex("ip")));
			c.setCustomPort(cursor.getInt(cursor.getColumnIndex("port")));
			c.setSSL(cursor.getInt(cursor.getColumnIndex("ssl")) == 1 ? true : false);
			UserLoginDBBean info = new UserLoginDBBean(num, name, pwd, email, c);
			data.add(info);
		}
		return data;
	}

	public List<UserLoginDBBean> queryByNameAndEmail(String userName,String email){
		db = dbHelper.getWritableDatabase();
		List<UserLoginDBBean> data = new ArrayList<UserLoginDBBean>();
		String sql = "select * from userinfo where username=? and useremail=?;";
		Cursor cursor = db.rawQuery(sql, new String[]{userName + "", email + ""});
		while (cursor.moveToNext()) {
			Custom c = new Custom();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			int num = cursor.getInt(cursor.getColumnIndex("num"));
			String name = cursor.getString(cursor.getColumnIndex("username"));
			String pwd = cursor.getString(cursor.getColumnIndex("userpassword"));
			String _email = cursor.getString(cursor.getColumnIndex("useremail"));
			c.setCustom(cursor.getInt(cursor.getColumnIndex("custom")) == 1 ? true : false);
			c.setCustomIP(cursor.getString(cursor.getColumnIndex("ip")));
			c.setCustomPort(cursor.getInt(cursor.getColumnIndex("port")));
			c.setSSL(cursor.getInt(cursor.getColumnIndex("ssl")) == 1 ? true : false);
			UserLoginDBBean info = new UserLoginDBBean(num, name, pwd, _email, c);
			data.add(info);
		}
		return data;
	}



	public void close(){
		if(null!=db){
			db.close();
			db = null;
		}
		if (dbHelper!=null) {
			dbHelper.close();
			dbHelper = null;
		}
	}
	
}
