package com.wuxing.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.wuxing.bean.HistoryBean;
import com.wuxing.dbHelper.DBHelper;

public class DbUtil {
	private Context context;
	private DBHelper helper;
	public DbUtil(Context context) {
		super();
		this.context = context;
		helper = new DBHelper(context);
	}
	// 插入历史记录
	public void insertHistory(HistoryBean history) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("start", history.getStart());
		values.put("startJingWei", history.getStartJingWei());
		values.put("end", history.getEnd());
		values.put("endJingWei", history.getEndJingWei());
		db.insert("history", null, values);
		db.close();

	}

	// 通过id查询History实体
	public HistoryBean queryHistory(int id) {
		HistoryBean history = new HistoryBean();
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("select * from history where id=?",
				new String[] { id + "" });
		if (c.moveToNext()) {
			history.setId("" + c.getInt(c.getColumnIndex("id")));
			history.setStart(c.getString(c.getColumnIndex("start")));
			history.setStart(c.getString(c.getColumnIndex("startJingWei")));
			history.setStart(c.getString(c.getColumnIndex("end")));
			history.setStart(c.getString(c.getColumnIndex("endJingWei")));
		}
		return history;

	}
	//删除其中一个历史记录
	public void deleteHistory(int id) {
		SQLiteDatabase db = helper.getWritableDatabase();
		//ContentValues values = new ContentValues();
		String whereClause = "id = ?";
		String[] whereArgs = new String[] { id + "" };
		int in = db.delete("history", whereClause, whereArgs);
		if (in > 0) {
			Log.i("删除成功", "deleteHistory");
		} else {
			Log.i("删除失败", "deleteHistory");
		}
		db.close();
	}
	//获取所有
	public List<HistoryBean> selectAllHistory() {
		List<HistoryBean> list = new ArrayList<HistoryBean>();
		List<HistoryBean> lists = new ArrayList<HistoryBean>();
		SQLiteDatabase db = helper.getWritableDatabase();
		String sql = "select * from history";
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) {
			HistoryBean history = new HistoryBean();
			history.setId("" + c.getInt(c.getColumnIndex("id")));
			history.setStart(c.getString(c.getColumnIndex("start")));
			history.setStartJingWei(c.getString(c.getColumnIndex("startJingWei")));
			history.setEnd(c.getString(c.getColumnIndex("end")));
			history.setEndJingWei(c.getString(c.getColumnIndex("endJingWei")));
			list.add(history);
		}
		if (list.size()>5) {
			
			HistoryBean his = list.get(0);
			deleteHistory(Integer.parseInt(his.getId()));
			
			for (int i = list.size()-1; i >= 1; i--) {
				lists.add(list.get(i));
			}
		}else {
			for (int i = list.size()-1; i >= 0; i--) {
				lists.add(list.get(i));
			}
		}
		
		
		c.close();
		db.close();
		return lists;
	}
	/**
	 * �ж��Ƿ����bill
	 * 
	 * @return
	 */
	public boolean isExist(String start , String end) {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = null;
		c = db.rawQuery("select * from History where start = ? and end = ?",
				new String[] { start,end });
		if (c.getCount() > 0) {
			c.close();
			return true;
		}
		c.close();
		db.close();
		return false;
	}

}
