package com.example.demo3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    static int[] datum;
    @SuppressLint("StaticFieldLeak")
    static TextView pocetniDatumSelektor;
    @SuppressLint("StaticFieldLeak")
    static TextView krajnjiDatumSelektor;
    static int selektor;

    ListView prozorZaIspisListe;
    Button goDugme;
    Button okDugme;
    Spinner spinnerLokacija;
    Spinner spinnerAlergena;

    String[] lokacije;
    String[] alergeni;

    String urlZaLokacije = "http://polen.sepa.gov.rs/api/opendata/locations/";
    String urlZaAlergene = "http://polen.sepa.gov.rs/api/opendata/allergens/";
    String urlZaListuKoncentracijaPT1 = "http://polen.sepa.gov.rs/api/opendata/pollens/?location=";
    String urlZaListuKoncentracijaPT2 = "&date=";
    String urlZaPolenPT1 = "http://polen.sepa.gov.rs/api/opendata/concentrations/?allergen=";
    String urlZaPolenPT2 = "&pollen=";

    JSONArray lokacijeJSON;
    JSONArray alergeniJSON;

    int izabranaLokacijaID;
    int izabranAlergenID;
    String izabranAlergenIme;
    String izabranPocetniDatum;
    String izabranKrajnjiDatum;
    static String [] meseci;

    String rezultat = "";
    String[] rezultatiZaStampu;
    List<Rezultat> listaRezultata;
    List<Rezultat> listaRezultataZaTrend;
    boolean gotovoPopunjavanje = false;

    String krajnjiDatumZaTrend = "";

    int animationDuration = 500;

    boolean popunjeneLokacije;
    boolean popunjeniAlergeni;
    boolean internetConnected;

    @SuppressLint("StaticFieldLeak")
    public class PozivZaListuLokacija extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                String result = "";
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray jsonArray = new JSONArray(result);
                lokacije = new String[jsonArray.length()];
                lokacijeJSON = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    lokacijeJSON.put(jsonArray.getJSONObject(i));
                    lokacije[i] = jsonArray.getJSONObject(i).getString("name");
                }
                popuniSpinner(spinnerLokacija, lokacije);
                popunjeneLokacije = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    public class PozivZaListuAlergena extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                String result = "";
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray jsonArray = new JSONArray(result);
                alergeni = new String[jsonArray.length()];
                alergeniJSON = new JSONArray();
                for (int i = 0; i < jsonArray.length(); i++) {
                    alergeniJSON.put(jsonArray.getJSONObject(i));
                    alergeni[i] = jsonArray.getJSONObject(i).getString("localized_name");
                }
                popuniSpinner(spinnerAlergena, alergeni);
                popunjeniAlergeni = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @SuppressLint("StaticFieldLeak")
    public class PozivZaIDPolena extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {

                URL url = new URL(strings[0]);
                String datum = strings[1];

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                String result = "";
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result + datum;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String datum = result.substring(result.length() - 10, result.length());
            result = result.substring(0, result.length() - 10);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray rezultati = jsonObject.getJSONArray("results");
                if (rezultati.length() > 0) {
                    JSONObject rezultat = rezultati.getJSONObject(0);
                    int pollenID = rezultat.getInt("id");
                    String url = urlZaPolenPT1 + izabranAlergenID + urlZaPolenPT2 + pollenID;
                    PozivZaKoncentraciju poziv = new PozivZaKoncentraciju();
                    poziv.execute(url, datum);
                } else if (rezultati.length() <= 0) {
                    String url = urlZaPolenPT1 + izabranAlergenID + urlZaPolenPT2 + 0;
                    PozivZaKoncentraciju poziv = new PozivZaKoncentraciju();
                    poziv.execute(url, datum);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class PozivZaKoncentraciju extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String prazanUrl = "http://polen.sepa.gov.rs/api/opendata/concentrations/?allergen=" + izabranAlergenID + "&pollen=0";
                String result = "";
                String datum = strings[1];

                if (strings[0].equals(prazanUrl)) {
                    result = "prazanURL" + datum;
                    return result;
                }

                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result + datum;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            String urlProvera = result.substring(0, 9);
            String datum = result.substring(result.length() - 10);
            if (urlProvera.equals("prazanURL")) {
                Rezultat rezultat;
                if (datum.equals(krajnjiDatumZaTrend)) {
                    gotovoPopunjavanje = true;
                    rezultat = new Rezultat(datum, izabranAlergenIme, -1, true);
                    listaRezultata.add(rezultat);
                } else {
                    rezultat = new Rezultat(datum, izabranAlergenIme, -1, true);
                    listaRezultata.add(rezultat);
                }
            } else {
                result = result.substring(0, result.length() - 10);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray rezultati = jsonObject.getJSONArray("results");
                    int vrednost = 0;
                    boolean prazan;
                    Rezultat rezultat;
                    if (rezultati.length() <= 0) {
                        prazan = true;
                    } else {
                        vrednost = rezultati.getJSONObject(0).getInt("value");
                        prazan = false;
                    }
                    if (datum.equals(krajnjiDatumZaTrend)) {
                        gotovoPopunjavanje = true;
                        rezultat = new Rezultat(datum, izabranAlergenIme, vrednost, prazan);
                        listaRezultata.add(rezultat);
                    } else {
                        rezultat = new Rezultat(datum, izabranAlergenIme, vrednost, prazan);
                        listaRezultata.add(rezultat);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }
    }

    public static class Rezultat {
        String datum;
        String alergen;
        int value;
        boolean prazan;

        public Rezultat(String datum, String alergen, int value, boolean prazan) {
            this.datum = datum;
            this.alergen = alergen;
            this.value = value;
            this.prazan = prazan;
        }

        public Rezultat() {

        }

        public String getDatum() {
            return datum;
        }

        public String getAlergen() {
            return alergen;
        }

        public int getValue() {
            return value;
        }

        public boolean getPrazan() {
            return prazan;
        }

        public void setDatum(String datum) {
            this.datum = datum;
        }

        public void setAlergen(String alergen) {
            this.alergen = alergen;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public void setPrazan(boolean prazan) {
            this.prazan = prazan;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = datum[0];
            int month = datum[1];
            int day = datum[2];

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            datum[0] = year;
            datum[1] = month;
            datum[2] = day;
            String datum = year + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day);
            datum = domaciDatum(datum);
            if (selektor == 1) {
                pocetniDatumSelektor.setText(datum);
            } else if (selektor == 2) {
                krajnjiDatumSelektor.setText(datum);
            }
        }
    }

    private void popuniSpinner(Spinner spinner, String[] items) {
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item, items) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                ((TextView) v).setTextSize(16);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                return v;
            }

        };
        spinner.setAdapter(adapter);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static List getDatesBetween(Date startDate, Date endDate) {
        List datesInRange = new ArrayList<>();
        Calendar calendar = getCalendarWithoutTime(startDate);
        Calendar endCalendar = getCalendarWithoutTime(endDate);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        while (calendar.before(endCalendar)) {
            Date result = calendar.getTime();
            String rezultat = df.format(result);
            datesInRange.add(rezultat);
            calendar.add(Calendar.DATE, 1);
        }
        String krajnji = df.format(endCalendar.getTime());
        datesInRange.add(krajnji);

        return datesInRange;
    }

    private static Calendar getCalendarWithoutTime(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.about_app:
                Intent intent = new Intent(getApplicationContext(), AboutApp.class);
                startActivity(intent);
                return true;
            case R.id.about_dev:
                Intent intent1 = new Intent(getApplicationContext(), AboutDev.class);
                startActivity(intent1);
                return true;
            default:
                return false;
        }
    }

    public boolean isInternetConnected() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        return connected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.demo3", Context.MODE_PRIVATE);
        //sharedPreferences.edit().clear().apply();
        Boolean freshInstall = sharedPreferences.getBoolean("fresh", true);
        if(freshInstall){
            sharedPreferences.edit().putBoolean("fresh", false).apply();
            Intent intent = new Intent(getApplicationContext(), AboutApp.class);
            intent.putExtra("fresh", "FRESH");
            startActivity(intent);
        }

        meseci = new String[] {"темп", "Јануар", "Фебруар", "Март", "Април", "Мај", "Јун", "Јул", "Август", "Септембар", "Октобар", "Новембар", "Децембар"};
        internetConnected = isInternetConnected();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datum = new int[]{year, month, day};
        listaRezultata = new ArrayList<Rezultat>();
        listaRezultataZaTrend = new ArrayList<Rezultat>();

        Log.d("TAG", formatirajDatumZaPoziv("01. Септембар 2021."));

        popunjeneLokacije = false;
        popunjeniAlergeni = false;

        goDugme = findViewById(R.id.goDugme);
        goDugme.setOnClickListener(view -> {
            try {
                click(view);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        okDugme = findViewById(R.id.okButton);
        okDugme.setOnClickListener(this::click2);

        prozorZaIspisListe = findViewById(R.id.prozorZaIspisListe);

        zamraciSekundarniUI();

        pocetniDatumSelektor = findViewById(R.id.datumPocetni);
        pocetniDatumSelektor.setText("ПОЧЕТНИ ДАТУМ");
        pocetniDatumSelektor.setOnClickListener(view -> {
            showDatePickerDialog(view);
            selektor = 1;
        });

        krajnjiDatumSelektor = findViewById(R.id.datumKrajnji);
        krajnjiDatumSelektor.setText("КРАЈЊИ ДАТУМ");
        krajnjiDatumSelektor.setOnClickListener(view -> {
            showDatePickerDialog(view);
            selektor = 2;
        });

        spinnerLokacija = findViewById(R.id.lokacije);
        spinnerAlergena = findViewById(R.id.alergeni);
        String[] tempLok = new String[]{"АПАТИН"};
        izabranaLokacijaID = 29;
        String[] tempAlg = new String[]{"ЈАВОР"};
        izabranAlergenIme = "ЈАВОР";
        izabranAlergenID = 1;

        popuniSpinner(spinnerLokacija, tempLok);
        popuniSpinner(spinnerAlergena, tempAlg);

        if(!internetConnected){
            alertInternet("Нема интернет конекције!");
        }
        else {
            PozivZaListuLokacija pozivLokacije = new PozivZaListuLokacija();
            pozivLokacije.execute(urlZaLokacije);

            PozivZaListuAlergena pozivAlergena = new PozivZaListuAlergena();
            pozivAlergena.execute(urlZaAlergene);

            int delay = 0;
            int period = 50;
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (popunjeneLokacije == true && popunjeniAlergeni == true) {
                                spinnerLokacija.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        try {
                                            izabranaLokacijaID = lokacijeJSON.getJSONObject(i).getInt("id");

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                                spinnerAlergena.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        try {
                                            izabranAlergenID = alergeniJSON.getJSONObject(i).getInt("id");
                                            izabranAlergenIme = alergeniJSON.getJSONObject(i).getString("localized_name");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                                timer.cancel();
                            }
                        }
                    });
                }
            }, delay, period);

            prozorZaIspisListe.setOnItemClickListener((adapterView, view, i, l) -> {
                try {
                    click3(i);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void click(View v) throws ParseException{
        internetConnected = isInternetConnected();
        if(!internetConnected){
            alertInternet("Нема интернет конекције!");
        }
        gotovoPopunjavanje=false;
        listaRezultata.clear();
        String [] prazna_lista = new String[]{"Обрада захтева..."};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, prazna_lista);
        prozorZaIspisListe.setAdapter(adapter);
        izabranPocetniDatum = formatirajDatumZaPoziv(pocetniDatumSelektor.getText().toString());
        izabranKrajnjiDatum = formatirajDatumZaPoziv(krajnjiDatumSelektor.getText().toString());
        if(izabranPocetniDatum.equals("ПОЧЕТНИ ДАТУМ") && izabranKrajnjiDatum.equals("КРАЈЊИ ДАТУМ")){
            alert("Изаберите датуме за претрагу!");
        }
        else if(izabranPocetniDatum.equals("ПОЧЕТНИ ДАТУМ")){
            alert("Изаберите почетни датум!");
        }
        else if(izabranKrajnjiDatum.equals("КРАЈЊИ ДАТУМ")){
            alert("Изаберите крајњи датум!");
        }
        else{
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date prvi = null;
            Date zadnji = null;
            Date praviZadnji = null;
            try {
                prvi = df.parse(izabranPocetniDatum);
                zadnji = df.parse(izabranKrajnjiDatum);
                praviZadnji = zadnji;
                Calendar c = Calendar.getInstance();
                c.setTime(zadnji);
                c.add(Calendar.DATE, 7);
                krajnjiDatumZaTrend = df.format(c.getTime());
                zadnji = df.parse(krajnjiDatumZaTrend);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            assert prvi != null;
            if(prvi.after(praviZadnji)){
                alert("Крајњи датум не може бити пре почетног!");
            }
            else{
                zamraciGlavniUI();
                prikaziSekundarniUI();
                List datumi = getDatesBetween(prvi, zadnji);

                for (int i = 0; i < datumi.size(); i++) {
                    PozivZaIDPolena poziv1 = new PozivZaIDPolena();
                    String url = urlZaListuKoncentracijaPT1 + izabranaLokacijaID + urlZaListuKoncentracijaPT2 + datumi.get(i).toString();
                    String datumString = datumi.get(i).toString();
                    poziv1.execute(url, datumString);
                }
                int delay = 0;
                int period = 500;
                Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask()
                {
                    public void run()
                     {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                stampa();
                                if(gotovoPopunjavanje){
                                    rezultatiZaStampu = new String[listaRezultata.size()];
                                    rezultatiZaStampu = rezultat.split("\n");
                                    popuniListu(rezultatiZaStampu);
                                    osposobiSekundarniUI();
                                    timer.cancel();
                                }
                            }
                        });
                    }
                }, delay, period);
            }
        }
    }

    public void click2(View v){
        prikaziGlavniUI();
        zamraciSekundarniUI();
    }

    public void click3(int i) throws ParseException {

        if(listaRezultata.get(i).getPrazan()){
            alert("Није могуће проверити тренд пошто нема тренутних података!");
        }
        else{
            double doubleZaStampu = 0;
            int vrednost;
            for (int j = i+1; j <= i+7; j++) {
                vrednost = listaRezultata.get(j).getValue();
                if(vrednost==-1){
                    vrednost=0;
                }
                doubleZaStampu += vrednost;
            }
            doubleZaStampu = doubleZaStampu/7;
            int vrednostZaStampu = (int) Math.round(doubleZaStampu);
            if(vrednostZaStampu>listaRezultata.get(i).getValue()){
                alert("Концентрација је у порасту у наредних 7 дана!");
            }
            else if(vrednostZaStampu<listaRezultata.get(i).getValue()){
                alert("Концентрација је у опадању у наредних 7 дана!");
            }
            else{
                alert("Концентрација мирује у наредних 7 дана!");
            }
        }
    }

    public void stampa(){
        rezultat = "";
        for (int i = 0; i < listaRezultata.size(); i++) {
            String datumZaStampu;
            Rezultat result = listaRezultata.get(i);
            if(result.getValue() == -1){
                datumZaStampu = domaciDatum(result.getDatum());
                rezultat += (datumZaStampu + " За дати датум и локацију не постоје очитавања \n");
            }
            else{
                if(result.getPrazan()){
                    datumZaStampu = domaciDatum(result.getDatum());
                    rezultat += ("На дан " + datumZaStampu + " не постоји податак за дати алерген \n");
                }
                else{
                    datumZaStampu = domaciDatum(result.getDatum());
                    rezultat += ("Концентрација алергена " + result.getAlergen() + " на дан " + datumZaStampu + " износи " + result.getValue() + "\n");
                }
            }
        }
    }

    public void popuniListu(String [] items){
        String[] newItems = new String[items.length-7];
        for (int i = 0; i < newItems.length; i++) {
            newItems[i] = items[i];
        }
        for (int i = 0; i < listaRezultata.size(); i++) {
            Log.d("TAG", listaRezultata.get(i).toString());
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, newItems);
        prozorZaIspisListe.setAdapter(adapter);
    }

    public void zamraciGlavniUI(){
        spinnerLokacija.animate().alpha(0f).setDuration(animationDuration);
        spinnerLokacija.setVisibility(View.GONE);
        spinnerLokacija.setEnabled(false);

        spinnerAlergena.animate().alpha(0f).setDuration(animationDuration);
        spinnerAlergena.setVisibility(View.GONE);
        spinnerAlergena.setEnabled(false);

        pocetniDatumSelektor.animate().alpha(0f).setDuration(animationDuration);
        pocetniDatumSelektor.setVisibility(View.GONE);
        pocetniDatumSelektor.setEnabled(false);

        krajnjiDatumSelektor.animate().alpha(0f).setDuration(animationDuration);
        krajnjiDatumSelektor.setVisibility(View.GONE);
        krajnjiDatumSelektor.setEnabled(false);

        goDugme.animate().alpha(0f).setDuration(animationDuration);
        goDugme.setVisibility(View.GONE);
        goDugme.setEnabled(false);
    }

    public void prikaziGlavniUI(){
        spinnerLokacija.animate().alpha(1f).setDuration(animationDuration);
        spinnerLokacija.setVisibility(View.VISIBLE);
        spinnerLokacija.setEnabled(true);

        spinnerAlergena.animate().alpha(1f).setDuration(animationDuration);
        spinnerAlergena.setVisibility(View.VISIBLE);
        spinnerAlergena.setEnabled(true);

        pocetniDatumSelektor.animate().alpha(1f).setDuration(animationDuration);
        pocetniDatumSelektor.setVisibility(View.VISIBLE);
        pocetniDatumSelektor.setEnabled(true);

        krajnjiDatumSelektor.animate().alpha(1f).setDuration(animationDuration);
        krajnjiDatumSelektor.setVisibility(View.VISIBLE);
        krajnjiDatumSelektor.setEnabled(true);

        goDugme.animate().alpha(1f).setDuration(animationDuration);
        goDugme.setVisibility(View.VISIBLE);
        goDugme.setEnabled(true);
    }

    public void zamraciSekundarniUI(){
        okDugme.animate().alpha(0f).setDuration(animationDuration);
        okDugme.setVisibility(View.GONE);
        okDugme.setEnabled(false);

        prozorZaIspisListe.animate().alpha(0f).setDuration(animationDuration);
        prozorZaIspisListe.setVisibility(View.GONE);
        prozorZaIspisListe.setEnabled(false);
    }

    public void prikaziSekundarniUI(){
        okDugme.animate().alpha(1f).setDuration(animationDuration);
        okDugme.setVisibility(View.VISIBLE);
        prozorZaIspisListe.animate().alpha(1f).setDuration(animationDuration);
        prozorZaIspisListe.setVisibility(View.VISIBLE);
    }

    public void osposobiSekundarniUI(){
        okDugme.setEnabled(true);
        prozorZaIspisListe.setEnabled(true);
    }

    public void alert(String poruka){
        new AlertDialog.Builder(this)
                .setMessage(poruka)
                .setPositiveButton("OK", null)
                .show();
    }

    public void alertInternet(String poruka){
        new AlertDialog.Builder(this)
                .setMessage(poruka)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    public static String domaciDatum(String initialDatum){
        String godina = initialDatum.substring(0, 4);
        String mesec = initialDatum.substring(6, 7);
        int mesecID = Integer.parseInt(mesec);
        mesec = meseci[mesecID];
        String dan = initialDatum.substring(8,10);
        String returnDatum = dan + ". " + mesec + " " + godina + ".";
        return returnDatum;
    }

    public static String formatirajDatumZaPoziv(String initialDatum){
        String dan = initialDatum.substring(0, 2);
        String godina = initialDatum.substring(initialDatum.length()-5, initialDatum.length()-1);
        String mesec = initialDatum.substring(4, initialDatum.length()-6);
        for (int i = 0; i < meseci.length; i++) {
            if(mesec.equals(meseci[i])){
                mesec = String.format("%02d", i);
            }
        }
        String returnDatum = godina + "-" + mesec + "-" + dan;
        return returnDatum;
    }
}