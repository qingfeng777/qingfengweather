package com.qingfengweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qingfengweather.app.R;
import com.qingfengweather.app.db.QingfengWeatherDB;
import com.qingfengweather.app.model.City;
import com.qingfengweather.app.model.County;
import com.qingfengweather.app.model.Province;
import com.qingfengweather.app.util.HttpCallbackListener;
import com.qingfengweather.app.util.HttpUtil;
import com.qingfengweather.app.util.Utility;

public class ChooseAreaActivity extends Activity{
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;//这玩意是进度条
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private QingfengWeatherDB qingfengWeatherDB;
	private List<String> dataList=new ArrayList<String>();
	
	//省列表
	private List<Province> provincesList;
	//是列表
	private List<City> cityList;
	//县列表
	private List<County> countyList;
	//选中的省份
	private Province selectedProvince;
	//选中的城市
	private City selectedCity;
	//当前选中的级别
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area); 
		listView=(ListView)findViewById(R.id.list_view);
		titleText=(TextView)findViewById(R.id.title_text);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);//上下文，view，ArrayList实例
		listView.setAdapter(adapter);
		qingfengWeatherDB=QingfengWeatherDB.getInstance(this);///..................................
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0	, View view,
					int index, long arg3) {
				
				
				Log.e("aa", "bbb1");
				
				
				
				if (currentLevel==LEVEL_PROVINCE) {
					selectedProvince=provincesList.get(index);//
					queryCities();
				}else if (currentLevel==LEVEL_CITY) {
					selectedCity=cityList.get(index);
					queryCounties();
				}
				
			}
			
		});
		queryProvince();
		
		
		
	}
	/**
	 * 查询全国的省，优先数据库
	 */
	private void queryProvince(){
		provincesList=qingfengWeatherDB.loadProvinces();
		if (provincesList.size()>0) {
			dataList.clear();
			for(Province province:provincesList){
				dataList.add(province.getProvinceName());
				
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);//使之从脑袋上显示
			titleText.setText("中国");
			currentLevel=LEVEL_PROVINCE;
			
		}else {
			queryFromSever(null,"province");
		}
	}
	/**
	 * 查询省内城市
	 */
	private void queryCities(){
		cityList=qingfengWeatherDB.loadCity(selectedProvince.getId());
		if (cityList.size()>0) {
			dataList.clear();
			for (City city:cityList) {
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}else {
			queryFromSever(selectedProvince.getProvinceCode(),"city");
		}
	}
	/**
	 * 查询县内市
	 */
	private void queryCounties(){
		countyList=qingfengWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size()>0) {
			dataList.clear();//为毛要这样呢，，，把以前的清理了，在哪加载的datalist啊？？
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}else {
			queryFromSever(selectedCity.getCityCode(),"county");
		}
	}
	/**
	 * 根据代号，类型从服务器上查询
	 */
	private void queryFromSever(final String code,final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
			
		}else {
			address="http://www.weather.com.cn/data/list3/city.xml";//
			
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			
			
			
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
				if ("province".equals(type)) {
					Log.e("aa", "bbb2.5");
					result=Utility.handleProvinceResponse(qingfengWeatherDB, response);
					Log.e("aa", "bbb2.6");
				}else if("city".equals(type)) {
					result=Utility.handleCitiesResponse(qingfengWeatherDB, response, selectedProvince.getId());
				}else if("county".equals(type)) {
					result=Utility.handleCountiesResponse(qingfengWeatherDB, response, selectedCity.getId());
					
				}
				Log.e("aa", "bbb2");
				if (result) {
					/**
					 * 通过runOnUiThread回到主线程处理
					 */
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							Log.e("aa", "bbb3");
							// TODO Auto-generated method stub
							closeProgressDialog();
							if("province".equals(type)){
								queryProvince();
								
							}else if ("city".equals(type)) {
								queryCities();
								
							}else if ("county".equals(type)) {
								queryCounties();
							}
							Log.e("aa", "bbb4");
						}
					});
				}
				
			}
			
			@Override
			public void onError(Exception e) {
				
				/**
				 * 通过runOnUiThread回到主线程处理
				 */
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", 0).show();
					}
				});
				
			}
		});
		
	}
	
	
	/**
	 * 显示进度条
	 */
	private void showProgressDialog(){
		if (progressDialog==null) {
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("稍等一下");
			progressDialog.setCanceledOnTouchOutside(false);//触摸屏幕其他区域，不会让框消失
			
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭进度条
	 */
	private void closeProgressDialog(){
		if (progressDialog!=null) {
			progressDialog.dismiss();
		}
	}
	/**
	 * 捕获Back建，根据当前的级别来判断，此时返回的是 ，城市，省，还是县，还是直接退出
	 */
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (currentLevel==LEVEL_COUNTY) {
				queryCities();
				
		}else if (currentLevel==LEVEL_CITY) {
			queryProvince();
		}else {
			finish();
		}
	}
	
	
}
