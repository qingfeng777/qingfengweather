package com.qingfengweather.app.util;

import android.text.TextUtils;

import com.qingfengweather.app.db.QingfengWeatherDB;
import com.qingfengweather.app.model.City;
import com.qingfengweather.app.model.County;
import com.qingfengweather.app.model.Province;

public class Utility {
	/**
	 * �������������ص�ʡ�����
	 */
	public synchronized static boolean handleProvinceResponse(QingfengWeatherDB qingfengWeatherDB,String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces=response.split(",");
			if (allProvinces!=null&&allProvinces.length>0) {
				for (String p:allProvinces) {
					String[] array =p.split("\\|");//ת���ǵ���
					Province province=new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//����������ݴ洢������
					qingfengWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	//�������ص��м����
	public static boolean handleCitiesResponse(QingfengWeatherDB qingfengWeatherDB,String response,int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String [] allCities =response.split(",");
			
			if (allCities!=null&&allCities.length>0) {
				for(String c:allCities){
					String [] array =c.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					
					qingfengWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	//�������ص��ؼ����
	public static boolean handleCountiesResponse(QingfengWeatherDB qingfengWeatherDB,String response,int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties=response.split(",");
			if (allCounties!=null&&allCounties.length>0) {
				for (String c:allCounties) {
					
					String[] array=c.split("\\|");
					County county=new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					
					qingfengWeatherDB.saveCounty(county);
					
				}
				return true;
			}
		}
		return false;
	}
	
	
	
	
}
