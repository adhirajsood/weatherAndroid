package com.servicingly.phone.servicinglyweatherreport;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Home extends AppCompatActivity {

    @Bind(R.id.listView)
    ListView lv;
    @Bind(R.id.locationQuery)
    EditText locationQuery;
    @Bind(R.id.findLocation)
    Button findLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        new SearchHistory().execute();
    }

    class SearchHistory extends AsyncTask<Void, Void, List<Recordedtemp>> {

        @Override
        protected List<Recordedtemp> doInBackground(Void... params) {

            List<Recordedtemp> history = Recordedtemp.findWithQuery(Recordedtemp.class, "Select * from Recordedtemp order by locationtime desc LIMIT 0 , 10");

            return history;
        }

        @Override
        protected void onPostExecute(List<Recordedtemp> recordedtemps) {
            TempListAdapter adapter = new TempListAdapter(Home.this,R.layout.activity_list_weather,recordedtemps.toArray(new Recordedtemp[recordedtemps.size()]));
            lv.setAdapter(adapter);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        locationQuery.setText("");
        new SearchHistory().execute();
    }



    public void findWeather(View view){
        Intent intent = new Intent(Home.this,WeatherReport.class);
        String locationUser = locationQuery.getText().toString().trim();
        if (locationUser!=null && !locationUser.equals("")){
            intent.putExtra("location",locationUser);
            startActivity(intent);
        }else {
            Toast.makeText(this, "Please enter a location first!", Toast.LENGTH_SHORT).show();
        }
    }






    public class TempListAdapter extends ArrayAdapter<Recordedtemp> {
        private Context context;
        private Recordedtemp[] temps;
        int resource;
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        public TempListAdapter(Context context, int resource, Recordedtemp[] temps) {
            super(context, resource, temps);

            this.context = context;
            this.temps = temps;
            this.resource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = li.inflate(R.layout.activity_list_weather, parent, false);
            ViewHolder holder = new ViewHolder(v);

            Recordedtemp t = temps[position];

            if(t != null){
                holder.LocationHistory.setText(t.getLocationName());
                holder.temperature.setText(t.getTemperature());
            }

            return v;
        }

         class ViewHolder{

            @Bind(R.id.LocationHistory)TextView LocationHistory;
            @Bind(R.id.temperature) TextView temperature;

            public ViewHolder(View v){
                ButterKnife.bind(this, v);
            }
        }
    }

}
