package com.example.acyncsample;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //画面部品listviewを取得
        ListView lvCityList =findViewById(R.id.lvCityList);
        //SimpleAdapterで使用するListオブジェクトを用意
        List<Map<String,String>> cityList = new ArrayList<>();
        //都市データを格納するマップオブジェクトの用意とcitylistへのデータ登録
        Map<String,String> city = new HashMap<>();
        city.put("name","神戸");
        city.put("id","280010");
        cityList.add(city);

        city = new HashMap<>();
        city.put("name","大阪");
        city.put("id","270000");
        cityList.add(city);

        city = new HashMap<>();
        city.put("name","熊本");
        city.put("id","430010");
        cityList.add(city);

        city = new HashMap<>();
        city.put("name","網走");
        city.put("id","013010");
        cityList.add(city);

        //simpleadapterで使用するfrom-to要変数の用意
        String[] from ={"name"};
        int[] to ={android.R.id.text1};
        //simpleadapterを生成
        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, cityList,android.R.layout.simple_expandable_list_item_1, from, to);
        //ListViewにsimpleadapterを設定
        lvCityList.setAdapter(adapter);
        //listviewにリスナを設定
        lvCityList.setOnItemClickListener(new ListItemClickListener());



    }


    private class ListItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            //ListViewでタップされた行の都市名と都市IDを取得
            Map<String, String> item = (Map<String, String>) parent.getItemAtPosition(position);
            String cityName =item.get("name");
            String cityId = item.get("id");
            //取得した都市名をtcCityNameに設定
            TextView tvCityName = findViewById(R.id.tvCityName);
            tvCityName.setText(cityName + "の天気");

            //天気情報を表示するテキストビューを取得
            TextView tvWeatherTelop =findViewById(R.id.tvWeatherTelop);
            //天気詳細を表示するテキストビューを取得
            TextView tvWeatherDesc = findViewById(R.id.tvWeatherDesc);
            //weatherInfoReceiverをnew。引数として上で取得したテキストビューを渡す
            WeatherInfoReceiver receiver = new WeatherInfoReceiver(tvWeatherTelop, tvWeatherDesc);
            //WeatherInfoRecieverを実行
            receiver.execute(cityId);
        }
    }

    private class WeatherInfoReceiver extends AsyncTask<String,String,String> {
        //現在の天気を表示する画面部品フィールド
        private TextView _tvWeatherTelop;
        //天気の詳細を表示する画面部品フィールド
        private TextView _tvWeatherDesc;
        //コンストラクタ
        //お天気情報を表示する画面部品をあらかじめ取得してフィールドに格納している。
        public WeatherInfoReceiver(TextView tvWeatherTelop, TextView tvWeatherDesc){
            //引数をそれぞれのフィールドに格納
            _tvWeatherTelop = tvWeatherTelop;
            _tvWeatherDesc = tvWeatherDesc;
        }

        @Override
        public String doInBackground(String...params){
            //可変長引数の1個目（インデックス0）を取得。これが都市ID
            String id=params[0];
            //都市IDを使って接続URL文字列を作成
            String urlStr ="http://weather.livedoor.com/forecast/webservice/json/v1?city="+ id;
            //天気情報サービスから取得したJSON文字列。天気情報が格納されている。
            String result="";
            //ここに上記URLから取得したJSON文字列を取得する処理を書く
            //HTTP接続を行うhttpURLConnectionオブジェクトを宣言。finallyで確実に会報するためにtry外で宣言
            HttpURLConnection con =null;
            //HTTP接続のレスポンスデータとして取得するInputStreamオブジェクトを宣言。同じくtry外で宣言。
            InputStream is =null;
            try{
                //URLオブジェクトを生成。
                URL url = new URL(urlStr);
                //URLオブジェクトからHttpURLConnectionオブジェクトを取得
                con = (HttpURLConnection) url.openConnection();
                //HTTP接続メソッドを設定
                con.setRequestMethod("GET");
                //接続
                con.connect();
                //HTTPURLConnectionオブジェクトからレスポンスデータを取得
                is=con.getInputStream();
                //レスポンスデータであるInputStreamオブジェクトを文字列に変換
                result = is2String(is);
            }
            catch (MalformedURLException ex){
            }
            catch (IOException ex){
            }
            finally {
                //HttpURLConnectionオブジェクトがnullでないなら解放。
                if(con != null){
                    con.disconnect();
                }
                //InputStreamオブジェクトがnullでないなら解放。
                if(is != null){
                    try{
                        is.close();
                    }
                    catch (IOException ex){
                    }
                }
            }

            //JSON文字列を返す
            return result;
        }
        @Override
        public void onPostExecute(String result){
            //天気情報用文字列を用意
            String telop ="";
            String desc="";


            //ここに天気情報JSON文字列を解析するい処理を記述
            try{
                //JSON文字列からJSONObjectオブジェクトを生成。これをルートJSONオブジェクトとする。
                JSONObject rootJSON = new JSONObject(result);

                //ルートJSON直下のdescriptionJSONオブジェクトを取得
                JSONObject descriptionJSON = rootJSON.getJSONObject("description");
                //descriprionプロパティ直下のtext文字列（天気概要文）を取得
                desc = descriptionJSON.getString("text");
                //desc = rootJSON.getString("description");
                //ルートJSON直下のforecastsJSON配列を取得
                JSONArray forecasts = rootJSON.getJSONArray("forecasts");
                //forecastsJSON配列のひとつ目(インデックス０)のJSONオブジェクトを取得
                JSONObject forecastNow = forecasts.getJSONObject(0);
                //forecasts１つ目のJSONオブジェクトからtelop文字列（天気）を取得
                telop = forecastNow.getString("telop");
            }catch (JSONException ex){

            }
            //天気情報用文字列をテキストビューにセット
            _tvWeatherTelop.setText(telop);
            _tvWeatherDesc.setText(desc);
        }
        private String is2String(InputStream is)throws IOException{
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer sb = new StringBuffer();
            char[] b = new char[1024];
            int line;
            while(0<=(line = reader.read(b))){
                sb.append(b,0,line);
            }
            return sb.toString();
        }
    }

}
