package com.my.volley20160521;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * YJH
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity------>";
    //    private String url = "http://www.weather.com.cn/data/sk/101010100.html";
    private String url = "https://www.baidu.com/";
    private String url_xml = "http://flash.weather.com.cn/wmaps/xml/china.xml";     //XML数据
    private String one = "http://www.mengxianyi.net/one/question.json";       //老孟的one接口
    //Get请求
    private String url_Get = "http://apis.juhe.cn/idcard/index?key=bb97bfce9edee938aeac99cb503b76db&cardno=430524199106158690";
    //Post请求
    private String url_Post = "http://apis.juhe.cn/idcard/index?";
    private Button btn_loadNet_MainAct, btn_loadNetGet_MainAct, btn_loadNetXML_MainAct, btn_loadNetPost_MainAct;
    private TextView tv_netData_MainAct;
    private Dialog dialog;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initView();
        initData();
    }

    private void initView() {
        btn_loadNet_MainAct = (Button) findViewById(R.id.btn_loadNetUTF_MainAct);
        tv_netData_MainAct = (TextView) findViewById(R.id.tv_netData_MainAct);
        btn_loadNetXML_MainAct = (Button) findViewById(R.id.btn_loadNetXML_MainAct);
        btn_loadNetGet_MainAct = (Button) findViewById(R.id.btn_loadNetGet_MainAct);
        btn_loadNetPost_MainAct = (Button) findViewById(R.id.btn_loadNetPost_MainAct);

        btn_loadNetPost_MainAct.setOnClickListener(this);
        btn_loadNetXML_MainAct.setOnClickListener(this);
        btn_loadNet_MainAct.setOnClickListener(this);
        btn_loadNetGet_MainAct.setOnClickListener(this);
        dialog = new ProgressDialog(this);
    }

    private void initData() {
        //不能在主线程中更新UI
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        Toast.makeText(MainActivity.this, "刷新！！！", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        /**
         * 这个线程为了实现定时访问网络，可以理解成一个定时器
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                        //mHandler.sendEmptyMessage(0);
                        //addNetDataPost();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_loadNetUTF_MainAct:
                dialog.setTitle("load...");
                dialog.show();
                addNetDataUTF();
                break;

            case R.id.btn_loadNetXML_MainAct:
                dialog.setTitle("load...");
                dialog.show();
                addDataNetXML();
                break;

            case R.id.btn_loadNetGet_MainAct:
                dialog.setTitle("load...");
                dialog.show();
                addNetGet();
                break;

            case R.id.btn_loadNetPost_MainAct:
                dialog.setTitle("load...");
                dialog.show();
                addNetDataPost();
                break;
        }
    }

    /**
     * 加载网络数据UTF-8
     */
    private void addNetDataUTF() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequestUtf stringRequest = new StringRequestUtf(one,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        tv_netData_MainAct.setText(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tv_netData_MainAct.setText(error.getNetworkTimeMs() + "");
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this, "网络错误!!!", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(stringRequest);
    }

    /**
     * 加载XML数据
     */
    private void addDataNetXML() {
        RequestQueue queue = Volley.newRequestQueue(this);
        XMLRequest xmlRequest = new XMLRequest(url_xml,
                new Response.Listener<XmlPullParser>() {
                    @Override
                    public void onResponse(XmlPullParser response) {
                        try {
                            int eventType = response.getEventType();
                            while (eventType != XmlPullParser.END_DOCUMENT) {
                                switch (eventType) {
                                    case XmlPullParser.START_TAG:
                                        String nodeName = response.getName();
                                        if ("city".equals(nodeName)) {
                                            String pName = response.getAttributeValue(0);
                                            Log.d("TAG", "pName is " + pName);
//                                            tv_netData_MainAct.setText("地名:"+pName);
                                        }
                                        break;
                                }
                                eventType = response.next();
                            }
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        dialog.dismiss();
//                        tv_netData_MainAct.setText(response+"");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tv_netData_MainAct.setText(error + "");
                    }
                });

        queue.add(xmlRequest);
    }

    /**
     * Get网络请求
     */
    private void addNetGet() {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url_Get,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        tv_netData_MainAct.setText(response + "");
                        int i = 0;
                        Toast.makeText(MainActivity.this, "Get请求成功！！！" + i++, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        tv_netData_MainAct.setText(error + "");
                        Toast.makeText(MainActivity.this, "Get请求失败！！！", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);
    }

    /**
     * Post请求
     */
    private void addNetDataPost() {
        String str = "http://apis.juhe.cn/mobile/get";    //查询手机号码归属地的API
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, str,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        tv_netData_MainAct.setText(response + "");
                        Toast.makeText(MainActivity.this, "Post请求成功！！！", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        tv_netData_MainAct.setText(error + "");
                        Toast.makeText(MainActivity.this, "Post请求失败！！!", Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
//                map.put("key", "bb97bfce9edee938aeac99cb503b76db");
//                map.put("cardno", "210283199409245532");
                // map.put("tel","15164054795");
                //这个顺序可以改变比一定要按照顺序写
                map.put("key", "5b20adf6f27bd3e78c9e5b05ffdabac2");
                map.put("dtype", "xml");
                map.put("phone", "15164054795");
                return map;
            }
        };

        queue.add(request);
    }
}
