package chetu.felixpat.countrydetails;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.webkit.*;
import android.widget.TextView;
import android.widget.Toast;



import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

        ProgressDialog pDialog;
        String result = "";
        SQLiteDatabase sqLiteDatabase;

        ArrayList<String> countryNames = new ArrayList<>();
        ArrayAdapter arrayAdapter;
        EditText searchBox;
        SharedPreferences sharedPreferences;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

            sharedPreferences = this.getSharedPreferences("chetu.felixpat.countrydetails", Context.MODE_PRIVATE);
            sharedPreferences.edit().putInt("status", 0);
            int status = sharedPreferences.getInt("status", 0);
            if (status == 0) {

                DownloadTask task = new DownloadTask();
                task.execute();
                sharedPreferences.edit().putInt("status", 1).apply();
            }

            ListView listView = (ListView) findViewById(R.id.listView);
            arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, countryNames);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String text = ((TextView) view).getText().toString();
                    Bundle bundle = getCountryDetails(text);
                    Intent in = new Intent(getApplicationContext(),Details.class);
                    in.putExtras(bundle);
                    startActivity(in);

                }
            });

            searchBox = (EditText) findViewById(R.id.searchBox);
            searchBox.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    // When user changed the Text
                    MainActivity.this.arrayAdapter.getFilter().filter(cs);
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                }
            });

            sqLiteDatabase = this.openOrCreateDatabase("Country", MODE_PRIVATE, null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS details (name text, capital text, region text, " +
                    "subregion text, population text, nativeName text," +
                    "numericCode text, flag text)");



            updateListView();

        }

    public Bundle getCountryDetails(String str)
    {
        Bundle bundle = new Bundle();
        Cursor c = sqLiteDatabase.rawQuery("Select * from details where name = '" + str + "'", null);
        if (c != null && c.moveToFirst()) {
            int nameIndex = c.getColumnIndex("name");
            int capitalIndex = c.getColumnIndex("capital");
            int regionIndex = c.getColumnIndex("region");
            int subregionIndex = c.getColumnIndex("subregion");
            int populationIndex = c.getColumnIndex("population");
            int nativeNameIndex = c.getColumnIndex("nativeName");
            int numericCodeIndex = c.getColumnIndex("numericCode");
            int flagIndex = c.getColumnIndex("flag");


            bundle.putString("name", c.getString(nameIndex));
            bundle.putString("capital", c.getString(capitalIndex));
            bundle.putString("region", c.getString(regionIndex));
            bundle.putString("subregion", c.getString(subregionIndex));
            bundle.putString("population", c.getString(populationIndex));
            bundle.putString("nativeName", c.getString(nativeNameIndex));
            bundle.putString("numericCode", c.getString(numericCodeIndex));
            bundle.putString("flag", c.getString(flagIndex));


            c.close();
        }
        return bundle;
    }

    public void updateListView()
    {
        Cursor c = sqLiteDatabase.rawQuery("Select * from details", null);
        if( c != null && c.moveToFirst())
        {
            int nameIndex = c.getColumnIndex("name");
            c.moveToFirst();
            countryNames.clear();
            countryNames.add(c.getString(nameIndex));
            while (c.moveToNext()) {
                countryNames.add(c.getString(nameIndex));
            }
            c.close();

            arrayAdapter.notifyDataSetChanged();
        }
    }

        public class DownloadTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL("https://restcountries.eu/rest/v2/all");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String line = "";
                    while (line != null) {
                        line = bufferedReader.readLine();
                        result += line;
                    }

                    sqLiteDatabase.execSQL("DELETE FROM details");

                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++)
                    {

                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String name = jsonObject.getString("name");
                        String capital = jsonObject.getString("capital");
                        String region = jsonObject.getString("region");
                        String subregion = jsonObject.getString("subregion");
                        String population = jsonObject.getString("population");
                        String nativeName = jsonObject.getString("nativeName");
                        String numericCode = jsonObject.getString("numericCode");
                        String flag = jsonObject.getString("flag");





                        String sql = "INSERT INTO details (name, capital, region, subregion, population, " +
                                "nativeName, numericCode, flag) values (?, ?, ?, ?, ?, ?, ?, ?)";
                        SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sql);

                        sqLiteStatement.bindString(1, name);
                        sqLiteStatement.bindString(2, capital);
                        sqLiteStatement.bindString(3, region);
                        sqLiteStatement.bindString(4, subregion);
                        sqLiteStatement.bindString(5, population);
                        sqLiteStatement.bindString(6, nativeName);
                        sqLiteStatement.bindString(7, numericCode);
                        sqLiteStatement.bindString(8, flag);

                        sqLiteStatement.execute();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // Dismiss the progress dialog
                if (pDialog.isShowing())
                    pDialog.dismiss();


                updateListView();
            }

            @Override
            protected void onPreExecute() {
                // Showing progress dialog
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                pDialog.show();
                super.onPreExecute();
            }

        }


    }

