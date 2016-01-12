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
	
	private ProgressDialog progressDialog;//�������ǽ�����
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private QingfengWeatherDB qingfengWeatherDB;
	private List<String> dataList=new ArrayList<String>();
	
	//ʡ�б�
	private List<Province> provincesList;
	//���б�
	private List<City> cityList;
	//���б�
	private List<County> countyList;
	//ѡ�е�ʡ��
	private Province selectedProvince;
	//ѡ�еĳ���
	private City selectedCity;
	//��ǰѡ�еļ���
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area); 
		listView=(ListView)findViewById(R.id.list_view);
		titleText=(TextView)findViewById(R.id.title_text);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);//�����ģ�view��ArrayListʵ��
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
	 * ��ѯȫ����ʡ���������ݿ�
	 */
	private void queryProvince(){
		provincesList=qingfengWeatherDB.loadProvinces();
		if (provincesList.size()>0) {
			dataList.clear();
			for(Province province:provincesList){
				dataList.add(province.getProvinceName());
				
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);//ʹ֮���Դ�����ʾ
			titleText.setText("�й�");
			currentLevel=LEVEL_PROVINCE;
			
		}else {
			queryFromSever(null,"province");
		}
	}
	/**
	 * ��ѯʡ�ڳ���
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
	 * ��ѯ������
	 */
	private void queryCounties(){
		countyList=qingfengWeatherDB.loadCounties(selectedCity.getId());
		if (countyList.size()>0) {
			dataList.clear();//ΪëҪ�����أ���������ǰ�������ˣ����ļ��ص�datalist������
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
	 * ���ݴ��ţ����ʹӷ������ϲ�ѯ
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
					 * ͨ��runOnUiThread�ص����̴߳���
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
				 * ͨ��runOnUiThread�ص����̴߳���
				 */
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "����ʧ��", 0).show();
					}
				});
				
			}
		});
		
	}
	
	
	/**
	 * ��ʾ������
	 */
	private void showProgressDialog(){
		if (progressDialog==null) {
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("�Ե�һ��");
			progressDialog.setCanceledOnTouchOutside(false);//������Ļ�������򣬲����ÿ���ʧ
			
		}
		progressDialog.show();
	}
	
	/**
	 * �رս�����
	 */
	private void closeProgressDialog(){
		if (progressDialog!=null) {
			progressDialog.dismiss();
		}
	}
	/**
	 * ����Back�������ݵ�ǰ�ļ������жϣ���ʱ���ص��� �����У�ʡ�������أ�����ֱ���˳�
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
