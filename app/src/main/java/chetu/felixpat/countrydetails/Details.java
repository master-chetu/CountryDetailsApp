package chetu.felixpat.countrydetails;

import android.content.Intent;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Details extends AppCompatActivity {

    TextView name,capital,region,subregion,population,nativename,numericcode;
    ImageView flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        name = findViewById(R.id.Name);
        capital = findViewById(R.id.capital);
        region = findViewById(R.id.region);
        subregion = findViewById(R.id.subregion);
        population = findViewById(R.id.population);
        nativename = findViewById(R.id.nativeName);
        numericcode = findViewById(R.id.numericCode);
        flag = findViewById(R.id.flagsvg);

        Intent in = getIntent();
        Bundle bundle = in.getExtras();
        name.setText(bundle.getString("name"));
        capital.setText(bundle.getString("capital"));
        region.setText(bundle.getString("region"));
        subregion.setText(bundle.getString("subregion"));
        population.setText(bundle.getString("population"));
        nativename.setText(bundle.getString("nativeName"));
        numericcode.setText(bundle.getString("numericCode"));
        




    }
}
