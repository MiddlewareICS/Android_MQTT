package com.example.drt_3axis_acc;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.lang.Math;
import java.math.BigDecimal;

import android.hardware.Sensor; 
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	private TextView textViewInfo = null;
	private TextView textViewX = null;
	private TextView textViewY = null;
	private TextView textViewZ = null;
	private TextView textViewA = null;
	private TextView textViewS = null;
	private TextView Angle = null;
	private SensorManager sensorManager = null;
	private Sensor sensor = null;
	private double pi=3.1415926;
	private float lowx = 0,lowy = 0,lowz = 0;
	private double anglex=0,angley=0,anglez=0;
	private final float FILTERING_VALAUE = 0.1f;
	private boolean Write=false;
	private boolean Calculate=false;
	private boolean fall=false;
	private int step=0,num=0;
	private Button mWriteButton, mStopButton;
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textViewInfo = (TextView) findViewById(R.id.TextView01);
		textViewX = (TextView) findViewById(R.id.TextView02);
		textViewY = (TextView) findViewById(R.id.TextView03);
		textViewZ = (TextView) findViewById(R.id.TextView04);
		textViewA = (TextView) findViewById(R.id.TextView05);
		Angle = (TextView) findViewById(R.id.TextView06);
		textViewS = (TextView) findViewById(R.id.TextView07);	
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		textViewInfo.setText("手机Accelerometer sensor详细信息：\n" +
				"设备名称：  " + sensor.getName() + "\n" +
				"设备供应商：  " + sensor.getVendor() + "\n" +
				"设备功率：  " + sensor.getPower()+ "\n");

	        mWriteButton = (Button) findViewById(R.id.button_write);
	        mWriteButton.setOnClickListener(this);
	        mStopButton = (Button) findViewById(R.id.button_stop);
	        mStopButton.setOnClickListener(this);
	        mWriteButton = (Button) findViewById(R.id.start_step);
	        mWriteButton.setOnClickListener(this);
	        mStopButton = (Button) findViewById(R.id.stop_step);
	        mStopButton.setOnClickListener(this);
	        
}
	
// 发送短信
	protected void sendMsg(){
	  String content = "老伯跌倒了，请大家及时前往救护！";
	  SmsManager smsManager = SmsManager.getDefault();
	  List<String> divideContents = smsManager.divideMessage(content);
	  for (String text : divideContents) {  
	    smsManager.sendTextMessage("18811478044", null, text, null, null);  
	  } 
	}
	
	@SuppressLint("SdCardPath")
	public void writeFileSdcard(String fileName,String message) {

		String sdStatus = Environment.getExternalStorageState();  
	    if(!sdStatus.equals(Environment.MEDIA_MOUNTED)) {  
	        Log.d("TestFile", "SD card is not avaiable/writeable right now.");  
	        return;  
	    }  
	    try {  
	        String pathName="/sdcard/test/";   
	        File path = new File(pathName);  
	        File file = new File(pathName + fileName);  
	        if( !path.exists()) {  
	            Log.d("TestFile", "Create the path:" + pathName);  
	            path.mkdir();  
	        }  
	        if( !file.exists()) {  
	            Log.d("TestFile", "Create the file:" + fileName);  
	            file.createNewFile();  
	        }  
	        FileOutputStream stream = new FileOutputStream(file,Boolean.TRUE);  
	        byte[] buf = message.getBytes();  
	        stream.write(buf);            
	        stream.close();  
	          
	    } catch(Exception e) {  
	        Log.e("TestFile", "Error on writeFilToSD.");  
	        e.printStackTrace();  
	    } 
    }
	
	public void onClick(View v) {
		if (v.getId() == R.id.button_write) {
			Write = true;
		}
		if (v.getId() == R.id.button_stop) {
			Write = false;
		}
		if (v.getId() == R.id.start_step) {
			Calculate = true;
		}
		if (v.getId() == R.id.stop_step) {
			Calculate = false;
			step=0;
		}
	}
	
	
	
	private SensorEventListener listener = new SensorEventListener() {
		
		public void onAccuracyChanged(Sensor arg0, int arg1) {
			
		}
		
		public void onSensorChanged(SensorEvent e) {
			DecimalFormat df = new DecimalFormat("#,##0.0");
			String message = new String();
			float x = e.values[0];
			float y = e.values[1];
			float z = e.values[2];
			lowx = x * FILTERING_VALAUE + lowx * (1.0f - FILTERING_VALAUE);
			lowy = y * FILTERING_VALAUE + lowy * (1.0f - FILTERING_VALAUE);
			lowz = z * FILTERING_VALAUE + lowz * (1.0f - FILTERING_VALAUE);
			float ax  = x - lowx;
	        float ay  = y - lowy;
	        float az  = z - lowz;
	        
	        message = df.format(ax) + "  ";
			message += df.format(ay) + "  ";
			message += df.format(az) + "  ";
			
			BigDecimal   b=new   BigDecimal(ax);     
		    ax=b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();  
		    b=new   BigDecimal(ay);
		    ay=b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue();
		    b=new   BigDecimal(az);
		    az=b.setScale(1,BigDecimal.ROUND_HALF_UP).floatValue(); 
		    double a =Math.sqrt(ax*ax+ay*ay+az*az);
		    b=new   BigDecimal(a);
		    a=b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
		    
			textViewX.setText("X方向上加速度：       " + ax + "m/s^2");
			textViewY.setText("Y方向上加速度：       " + ay + "m/s^2");
			textViewZ.setText("Z方向上加速度：       " + az + "m/s^2");
			textViewA.setText("合加速度：       " + df.format(a) + "m/s^2" + "\n");
			
			if(a>0)
			{
				double temp=ax/a;
				if(temp>1) temp=1;
				if(temp<-1) temp=-1;
				anglex=java.lang.Math.acos(temp);
				temp=ay/a;
				if(temp>1) temp=1;
				if(temp<-1) temp=-1;
				angley=java.lang.Math.acos(temp);
				temp=az/a;
				if(temp>1) temp=1;
				if(temp<-1) temp=-1;
				anglez=java.lang.Math.acos(temp);
			}else
			{
				anglex=0;angley=0;anglez=0;
			}
			Angle.setText("加速度方向为：       x: " + df.format(anglex * 180 / pi) + "°   y: "+ df.format(angley * 180 / pi) + "°   z: "+ df.format(anglez * 180 / pi) + "°\n");
			
			if (Write) {
				message += df.format(a) + "  " + df.format(anglex)  + "  "+ df.format(angley) + "  " +df.format(anglez) +  "  \n";
				writeFileSdcard("fall.txt", message);
			}
			if (Calculate)
			{
				if(ax>3&&ay>3||ax<-3&&ay<-3)
				{
					step++;
					textViewS.setText("步行 " + step + " 步");
				}
			}
			
			if (a>8)
			{
				fall=true;
			}
			else
			{
				fall=false;
			}
			if (fall&&num==0)
			{
				num++;
				query();
			}
		}
		
	};
	
	protected boolean giveUp=false,deceide=false;
	protected void query(){
		final AlertDialog isfall = new AlertDialog.Builder(this).create();
		isfall.setTitle("呼救"); 
		isfall.setMessage("是否需要呼救？");  
		isfall.setButton("确定", fallListener);  
		isfall.setButton2("取消", fallListener);  
		isfall.show();  
		Handler myHander = new Handler(); 
		myHander.postDelayed(new Runnable() 
			{
				@Override 
				public void run() 
				{ 
					if(!giveUp&&!deceide)
					{
						sendMsg();
						num=0;
						isfall.dismiss(); 
					}
					else
					{
						giveUp=false;
						deceide=false;
					}
				}
			},10000);
	}
	
	DialogInterface.OnClickListener fallListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which)  
        {  
            switch (which)  
            {  
            case AlertDialog.BUTTON_POSITIVE:// "确认"按钮发送短信 
            	sendMsg();
            	num=0;
            	deceide=true;
                break;  
            case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框  
            	num=0;
            	giveUp=true;
                break;  
            default:  
                break;  
            }  
        }  
	};
		
	protected void onResume() {
		super.onResume();
		sensorManager.registerListener(listener, sensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	protected void onStop() {
		super.onStop();
		sensorManager.unregisterListener(listener);
	}
		
}
