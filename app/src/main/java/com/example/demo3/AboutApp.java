package com.example.demo3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

public class AboutApp extends AppCompatActivity {

    String text;
    TextView textView;
    Button button;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.poleni:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.about_dev:
                Intent intent1 = new Intent(getApplicationContext(), AboutDev.class);
                startActivity(intent1);
                return true;
            default: return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        text = "Добродошли у Полен! \n" +
                "\n" +
                "Полен је апликација која омогућава кориснику да сазна концентрацију полена одређених алергена у ваздуху. Базирана је на подацима и мерењима Републичке Агенције за Заштиту Животне Средине. Апликација је намењена стручњацима у области животне средине, алерголозима, пчеларима, особама које пате од алергија, као и свима које ови подаци могу занимати.\n" +
                "\n" +
                "Упутство за употребу:\n" +
                "У екрану \"Полени\" први падајући мени омогућава избор локације, други падајући мени омогућава избог алергена. Поља почетни датум и крајњи датум омогућавају одабир периода за који се ради претрага (У случају да желите претрагу за само један дан, поставити почетни и крајњи датум на исту вредност). Након одабира параметара, кликом на дугме \"Претражи\" покреће се претрага концентрација у одабраном периоду. Након што се излистају резултати претраге, кликом на појединачне датуме/концентрације, добија се информација о тенденцији раста концентрације у наредној недељи. Притиском на дугме \"ОК\" завршава се тренутна претрага и могуће је унети нове параметре за претрагу.";
        textView = findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());
        button = findViewById(R.id.ok_button_about);

        /*androidx.appcompat.widget.Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        textView.setText(text);*/

        Intent intent = getIntent();
        String fresh = intent.getStringExtra("fresh");
        if(fresh != null){
            textView.setText(text);
            button.setAlpha(1);
            button.setEnabled(true);
            button.setOnClickListener(view -> {
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
            });
        }
        else{
            textView.setText(text);
            androidx.appcompat.widget.Toolbar myToolbar = findViewById(R.id.toolbar);
            setSupportActionBar(myToolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        }
    }
}