package com.qingfengweather.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qingfengweather.app.model.City;
import com.qingfengweather.app.model.County;
import com.qingfengweather.app.model.Province;

public class QingfengWeatherDB {
	
	/**
	 * ��ݿ����
	 */
	public static final String DB_NAME="qingfeng_weather";
	/**
	 * ��ݿ�汾
	 * 
	 */
	public static final int VERSION=1;
	
	
	public static QingfengWeatherDB qingfengWeatherDB;
	
	private SQLiteDatabase db;
	
	/**
	 * �����췽��˽�л���
	 * Ҳ�ǹ���ģ���Ȼ�Ҳ����˽�
	 * Ϊë��˽�л�
	 */
	private QingfengWeatherDB (Context context) {
		QingfengWeatherOpenHelper dbHelper=new QingfengWeatherOpenHelper(context, DB_NAME, null, VERSION);
		db=dbHelper.getWritableDatabase();
	}
	/**
	 * ��ȡ���Weather��ʵ��
	 */
	public synchronized static QingfengWeatherDB getInstance(Context context){
		
		if(qingfengWeatherDB==null){
			qingfengWeatherDB=new QingfengWeatherDB(context);//..........................
		}
		return qingfengWeatherDB;
	}
	
	/**
	 * ��province�洢����ݿ�
	 */
	public void saveProvince(Province province){
		if (province!=null) {
			ContentValues values =new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code",province.getProvinceCode());
			/**
			 * ���ࡪ�����ģ�ͣ�һ�оͽ������
			 */
			db.insert("Province", null, values);
		}
	}
	/**
	 * ����ݿ��ȡȫ����Ϣ
	 */
	public List<Province> loadProvinces(){
		List<Province> list =new ArrayList<Province>();
		Cursor cursor =db.query("province", null, null, null, null,null,null);
		if (cursor.moveToFirst()) {
			do{
				Province province=new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		
		return list;
	}
	
	/**
	 * �洢City
	 */
	public void saveCity(City city){
		if (city!=null) {
			ContentValues values =new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	/**
	 * ��ݿ��ȡʡ�³���
	 */
	public List<City> loadCity(int provinceId) {
		List<City> list=new ArrayList<City>();
		Cursor cursor =db.query("City", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null,null,null);
		
		if (cursor.moveToFirst()) {
			do {
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setProvinceId(provinceId);
				list.add(city);
			} while (cursor.moveToNext());
		}
		
	return list;
		}
	
	public void saveCounty(County county) {
		if (county!=null) {
			ContentValues values =new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code",county.getCountyCode());
			values.put("city_id", county.getCityId());
			db.insert("County", null, values);
		}
	}
	
	public List<County> loadCounties(int cityId){
		List<County> list=new ArrayList<County>();
		
		Cursor cursor=db.query("County", null, "city_id=?", new String[]{String.valueOf(cityId)}, null, null, null);
		
		if (cursor.moveToFirst()) {
			do{
				County county=new County();
				county.setCityId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCityId(cityId);
				
				list.add(county);
				
			}while(cursor.moveToNext());
		}
		return list;
	}
	
		
	}
	
		

