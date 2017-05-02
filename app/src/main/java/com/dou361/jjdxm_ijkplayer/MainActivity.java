package com.dou361.jjdxm_ijkplayer;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;

import android.widget.EditText;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String requestUrl = "";
    String requestBody = "";
    String deviceId = null;
    int msgSeq = 0;
    int streamType = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_h, R.id.btn_v, R.id.btn_vod, R.id.btn_origin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_h:
                /**半屏播放器*/
                streamType = 1;
                startActivity(HPlayerActivity.class);
                break;
            case R.id.btn_v:
                /**竖屏播放器*/
                streamType = 2;
                startActivity(PlayerActivity.class);
                break;
            case R.id.btn_vod:
                /**竖屏直播播放器*/
                EditText editTextVodUrl = (EditText)findViewById(R.id.vod_url);
                Intent intent = new Intent(MainActivity.this, OriginPlayerActivity.class);
                //用Bundle携带数据
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putString("rtspUrl",editTextVodUrl.getText().toString()); // "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.btn_origin:
                /**ijkplayer原生的播放器*/
                //startActivity(OriginPlayerActivity.class);

                break;
        }
    }

    //创建一个Handler
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    break;
                case 200:
                    String rtspUrl = (String)msg.obj;
                    Intent intent = new Intent(MainActivity.this, OriginPlayerActivity.class);
                    //用Bundle携带数据
                    Bundle bundle=new Bundle();
                    //传递name参数为tinyphp
                    bundle.putString("rtspUrl",rtspUrl); // "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4");
                    intent.putExtras(bundle);

                    startActivity(intent);
                    System.out.println(rtspUrl);
                    break;
                default:
                    break;
            }
        }

    };

    private void startActivity(Class<?> cls) {
        EditText editTextCagAddr = (EditText)findViewById(R.id.cag_addr);
        EditText editTextCagPort = (EditText)findViewById(R.id.cag_port);
        EditText editTextDid = (EditText)findViewById(R.id.did);
        deviceId = editTextDid.getText().toString();
        requestUrl = "http://"+ editTextCagAddr.getText().toString() +":" + editTextCagPort.getText().toString() + "/spi/mediaplay";

        requestBody = "";
        requestBody += "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\" ?>";
        requestBody += "<Message Version=\"2.0\">";
        requestBody += "<Header MsgSeq=\"" + String.valueOf ( msgSeq ) +"\" MsgType=\"MediaPlaytRequest\" />";
        requestBody += "<MediaPlaytRequest>";
        requestBody += "<DevSN>" + editTextDid.getText().toString() + "</DevSN>";
        requestBody += "<CameraId>1</CameraId>";
        requestBody += "<StreamType>" + String.valueOf ( streamType) + "</StreamType>";
        requestBody += "</MediaPlaytRequest>";
        requestBody += "</Message>";

        System.out.println("requestUrl:" +  requestUrl + "\n" + requestBody);
        System.out.println("requestBodySize:" + requestBody.length() + "   msgSeq:" + msgSeq);
        msgSeq++;

        //HttpURLConnection_Post();
        Thread requestThread = new Thread(new requestThread());
        requestThread.start();
        //Intent intent = new Intent(MainActivity.this, cls);
        //startActivity(intent);
    }

    HttpURLConnection urlConn = null;
    String resultData = "";
    public class requestThread implements  Runnable{
        public void run() {
            try {
                byte[] xmlbyte = requestBody.toString().getBytes("UTF-8");
                URL url = new URL(requestUrl);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setDoOutput(true);// 允许输出
                conn.setDoInput(true);
                conn.setUseCaches(false);// 不使用缓存
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("Content-Length",
                        String.valueOf(xmlbyte.length));
                conn.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
                //conn.setRequestProperty("X-ClientType", "2");//发送自定义的头信息

                conn.getOutputStream().write(xmlbyte);
                conn.getOutputStream().flush();
                conn.getOutputStream().close();

                if (conn.getResponseCode() != 200)
                    throw new RuntimeException("请求url失败");

                InputStream is = conn.getInputStream();// 获取返回数据
                // 使用输出流来输出字符(可选)
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[32 * 1024];
                int len;
                while ((len = is.read(buf)) != -1) {
                    out.write(buf, 0, len);
                }
                String string = out.toString("UTF-8");
                System.out.println(string);
                out.close();

                // xml解析
                String version = null;
                String retCode = null;
                String serverAddr = null;
                String serverPort = null;
                XmlPullParser parser = Xml.newPullParser();
                try {
                    parser.setInput(new ByteArrayInputStream(string.getBytes("UTF-8")), "UTF-8");
                    //parser.setInput(is, "UTF-8");
                    int eventType = parser.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            if ("Message".equals(parser.getName())) {
                                //version = parser.getAttributeValue(0);
                            } else if ("Result".equals(parser.getName())) {
                                retCode = parser.getAttributeValue(0);
                            } else if ("StreamIP".equals(parser.getName())) {
                                serverAddr = parser.nextText();
                            } else if ("StreamPort".equals(parser.getName())) {
                                serverPort = parser.nextText();
                            }
                        }

                        eventType = parser.next();
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    System.out.println(e);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
                System.out.println("retCode = " + retCode);
                System.out.println("serverAddr = " + serverAddr);
                System.out.println("serverPort = " + serverPort);

                // rtsp://42.123.110.60:554/86000123456884846111000129211111_1?stype=live&streamid=1
                resultData = "rtsp://" + serverAddr + ":" + serverPort + "/" + deviceId + "_1?stype=live&streamid=" + String.valueOf ( streamType) ;
                Message mg = Message.obtain();
                mg.what =  Integer.parseInt(retCode);
                mg.obj = resultData;
                handler.sendMessage(mg);
            } catch (Exception e) {
                resultData = "连接超时";
                Message mg = Message.obtain();
                mg.what = 0;
                mg.obj = resultData;
                handler.sendMessage(mg);
                e.printStackTrace();
            }
        }
    }

}
