package com.example.demo3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Objects;

public class AboutDev extends AppCompatActivity {

    TextView textView;
    String text;

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
            case R.id.about_app:
                Intent intent1 = new Intent(getApplicationContext(), AboutApp.class);
                startActivity(intent1);
                return true;
            default: return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_dev);

        textView = findViewById(R.id.textView2);
        text = "Аутор: Петар Коврлија\n" +
                "\n" +
                "e-mail: petarkovrlija1995@gmail.com\n" +
                "Телефон: +381693051995\n" +
                "\n" +
                "Образовање:\n" +
                "Математички Факултет Универзитета у Београду (2014-тренутно) - Информатика\n" +
                "Правно Пословна Школа Београд (2010-2014) - Правни техничар\n" +
                "\n" +
                "Вештине:\n" +
                "Програмски језици: Java, C, CSS, HTML, JavaScript, LaTeX, TypeScript\n" +
                "Контрола верзије: git \n" +
                "Рачунарска графика: Adobe Illustrator, Adobe Photoshop, Autodesk Maya\n" +
                "\n" +
                "Искуство:\n" +
                "Омикрон ИТ Заједница - Члан одбора, Лидер тима за људске ресурсе, Координатор за људске ресурсе - 2016-тренутно\n" +
                "\n" +
                "Језици: Српски - матерњи, Енглески - Напредни ниво\n" +
                "\n" +
                "Интересовања: Амерички фудбал, рачунарска графика";
        textView.setText(text);
        androidx.appcompat.widget.Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
    }
}