package com.example.acyncsample;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

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
        city.put("id","280000");
        cityList.add(city);

        city = new HashMap<>();
        city.put("name","大阪");
        city.put("id","270000");
        cityList.add(city);

        city = new HashMap<>();
        city.put("name","熊本");
        city.put("id","290000");
        cityList.add(city);

        city = new HashMap<>();
        city.put("name","豊岡");
        city.put("id","300000");
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
            String urlStr =""+ id;
            //天気情報サービスから取得したJSON文字列。天気情報が格納されている。
            String result="";
            //ここに上記URLから取得したJSON文字列を取得する処理を書く。

            //JSON文字列を返す
            return result;
        }
        @Override
        public void onPostExecute(String result){
            //天気情報用文字列を用意
            String telop ="";
            String desc="";

            //ここに天気情報JSON文字列を解析するい処理を記述

            //天気情報用文字列をテキストビューにセット
            _tvWeatherTelop.setText(telop);
            _tvWeatherDesc.setText(desc);
        }
    }

}
