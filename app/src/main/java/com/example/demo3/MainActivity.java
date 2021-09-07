package com.example.demo3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    static int [] datum;
    static TextView pocetniDatumSelektor;
    static TextView krajnjiDatumSelektor;
    static int selektor;

    ListView prozorZaIspisListe;
    Button goDugme;
    Button okDugme;
    Spinner spinnerLokacija;
    Spinner spinnerAlergena;

    String [] lokacije;
    String [] alergeni;

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

    String rezultat = "";
    String[] rezultatiZaStampu;
    List<Rezultat> listaRezultata;
    List<Rezultat> listaRezultataZaTrend;
    boolean gotovoPopunjavanje = false;
    boolean gotovoPopunjavanjeTrenda = false;

    String identifikatorZaListu = "0";
    String identifikatorZaTrend = "1";
    String krajnjiDatumZaTrend = "";

    int animationDuration = 500;

    public class PozivZaListuLokacija extends AsyncTask <String, Void, String>{
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

                while(data!=-1){
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
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public class PozivZaListuAlergena extends AsyncTask <String, Void, String>{
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

                while(data!=-1){
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
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public class PozivZaIDPolena extends AsyncTask <String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            try {

                String identifikator = strings[2];

                URL url = new URL(strings[0]);
                String datum = strings[1];

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                String result = "";
                int data = reader.read();

                while(data!=-1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result + datum + identifikator;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String identifikator = result.substring(result.length()-1);
            String datum = result.substring(result.length()-11, result.length()-1);
            result = result.substring(0, result.length()-11);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray rezultati = jsonObject.getJSONArray("results");
                if(identifikator.equals(identifikatorZaListu)){
                    if(rezultati.length()>0){
                        JSONObject rezultat = rezultati.getJSONObject(0);
                        int pollenID = rezultat.getInt("id");
                        String url = urlZaPolenPT1 + izabranAlergenID + urlZaPolenPT2 + pollenID;
                        PozivZaKoncentraciju poziv = new PozivZaKoncentraciju();
                        poziv.execute(url, datum);
                    }
                    else if(rezultati.length()<=0 ){
                        String url = urlZaPolenPT1 + izabranAlergenID + urlZaPolenPT2 + 0;
                        PozivZaKoncentraciju poziv = new PozivZaKoncentraciju();
                        poziv.execute(url, datum);
                    }
                }
                else if(identifikator.equals(identifikatorZaTrend)){
                    if(rezultati.length()>0){
                        JSONObject rezultat = rezultati.getJSONObject(0);
                        int pollenID = rezultat.getInt("id");
                        String url = urlZaPolenPT1 + izabranAlergenID + urlZaPolenPT2 + pollenID;
                        PozivZaTrend poziv = new PozivZaTrend();
                        poziv.execute(url, datum);
                    }
                    else if(rezultati.length()<=0 ){
                        String url = urlZaPolenPT1 + izabranAlergenID + urlZaPolenPT2 + 0;
                        PozivZaTrend poziv = new PozivZaTrend();
                        poziv.execute(url, datum);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class PozivZaKoncentraciju extends AsyncTask <String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                String prazanUrl = "http://polen.sepa.gov.rs/api/opendata/concentrations/?allergen=" + izabranAlergenID + "&pollen=0";
                String result = "";
                String datum = strings[1];

                if(strings[0].equals(prazanUrl)){
                    result="prazanURL" + datum;
                    return result;
                }

                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while(data!=-1){
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
            if(urlProvera.equals("prazanURL")){
                Rezultat rezultat;
                if(datum.equals(izabranKrajnjiDatum)){
                    gotovoPopunjavanje = true;
                    rezultat = new Rezultat(datum, izabranAlergenIme, -1, true);
                    listaRezultata.add(rezultat);
                }
                else{
                    rezultat = new Rezultat(datum, izabranAlergenIme, -1, true);
                    listaRezultata.add(rezultat);
                }
            }
            else{
                result = result.substring(0, result.length()-10);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray rezultati = jsonObject.getJSONArray("results");
                    int vrednost=0;
                    boolean prazan;
                    Rezultat rezultat;
                    if(rezultati.length() <= 0){
                        prazan=true;
                    }
                    else{
                        vrednost = rezultati.getJSONObject(0).getInt("value");
                        prazan=false;
                    }
                    if(datum.equals(izabranKrajnjiDatum)){
                        gotovoPopunjavanje = true;
                        rezultat = new Rezultat(datum, izabranAlergenIme, vrednost, prazan);
                        listaRezultata.add(rezultat);
                    }
                    else{
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

    public class PozivZaTrend extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                String prazanUrl = "http://polen.sepa.gov.rs/api/opendata/concentrations/?allergen=" + izabranAlergenID + "&pollen=0";
                String result = "";
                String datum = strings[1];

                if(strings[0].equals(prazanUrl)){
                    result="prazanURL" + datum;
                    return result;
                }

                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while(data!=-1){
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
            if(urlProvera.equals("prazanURL")){
                Rezultat rezultat;
                if(datum.equals(krajnjiDatumZaTrend)){
                    gotovoPopunjavanjeTrenda = true;
                    rezultat = new Rezultat(datum, izabranAlergenIme, -1, true);
                    listaRezultataZaTrend.add(rezultat);
                }
                else{
                    rezultat = new Rezultat(datum, izabranAlergenIme, -1, true);
                    listaRezultataZaTrend.add(rezultat);
                }
            }
            else{
                result = result.substring(0, result.length()-10);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray rezultati = jsonObject.getJSONArray("results");
                    int vrednost=0;
                    boolean prazan;
                    Rezultat rezultat;
                    if(rezultati.length() <= 0){
                        prazan=true;
                    }
                    else{
                        vrednost = rezultati.getJSONObject(0).getInt("value");
                        prazan=false;
                    }
                    if(datum.equals(krajnjiDatumZaTrend)){
                        gotovoPopunjavanjeTrenda = true;
                        rezultat = new Rezultat(datum, izabranAlergenIme, vrednost, prazan);
                        listaRezultataZaTrend.add(rezultat);
                    }
                    else{
                        rezultat = new Rezultat(datum, izabranAlergenIme, vrednost, prazan);
                        listaRezultataZaTrend.add(rezultat);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }
    }

    public class Rezultat {
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

        public Rezultat(){

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

        public boolean getPrazan(){
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

        public void setPrazan(boolean prazan){
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
            String datum = year + "-" + String.format("%02d", month+1) + "-" + String.format("%02d", day);
            if(selektor==1) {
                pocetniDatumSelektor.setText(datum);
            }
            else if(selektor==2){
                krajnjiDatumSelektor.setText(datum);
            }
        }
    }

    private void popuniSpinner(Spinner spinner, String[] items){
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.spinner_item, items){
            public View getView(int position, View convertView,ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setGravity(Gravity.CENTER);
                ((TextView) v).setTextSize(16);
                return v;
            }

            public View getDropDownView(int position, View convertView,ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,parent);
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
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.about_app:
                Intent intent = new Intent(getApplicationContext(), AboutApp.class);
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
        setContentView(R.layout.activity_main);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String trenutniDatum = year + "-" + String.format("%02d", month+1) + "-" + String.format("%02d", day);
        datum = new int[]{year, month, day};
        listaRezultata = new ArrayList<Rezultat>();
        listaRezultataZaTrend = new ArrayList<Rezultat>();

        goDugme = findViewById(R.id.goDugme);
        goDugme.setOnClickListener(view -> {
            try {
                click(view);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        okDugme = findViewById(R.id.okButton);
        okDugme.setOnClickListener(view -> click2(view));

        prozorZaIspisListe =findViewById(R.id.prozorZaIspisListe);

        zamraciSekundarniUI();

        pocetniDatumSelektor = findViewById(R.id.datumPocetni);
        pocetniDatumSelektor.setText("ПОЧЕТНИ ДАТУМ");
        //pocetniDatumSelektor.setText(trenutniDatum);
        pocetniDatumSelektor.setOnClickListener(view -> {
            showDatePickerDialog(view);
            selektor = 1;
        });

        krajnjiDatumSelektor = findViewById(R.id.datumKrajnji);
        krajnjiDatumSelektor.setText("КРАЈЊИ ДАТУМ");
        //krajnjiDatumSelektor.setText(trenutniDatum);
        krajnjiDatumSelektor.setOnClickListener(view -> {
            showDatePickerDialog(view);
            selektor = 2;
        });

        spinnerLokacija = findViewById(R.id.lokacije);
        spinnerAlergena = findViewById(R.id.alergeni);

        PozivZaListuLokacija pozivLokacije = new PozivZaListuLokacija();
        pozivLokacije.execute(urlZaLokacije);

        PozivZaListuAlergena pozivAlergena = new PozivZaListuAlergena();
        pozivAlergena.execute(urlZaAlergene);

        prozorZaIspisListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    click3(i);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

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

    }

    public void click(View v) throws ParseException{
        gotovoPopunjavanje=false;
        listaRezultata.clear();
        izabranPocetniDatum = pocetniDatumSelektor.getText().toString();
        izabranKrajnjiDatum = krajnjiDatumSelektor.getText().toString();
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
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date prvi = null;
            Date zadnji = null;
            try {
                prvi = df.parse(izabranPocetniDatum);
                zadnji = df.parse(izabranKrajnjiDatum);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(prvi.after(zadnji)){
                alert("Крајњи датум не може бити пре почетног!");
            }
            else{
                zamraciGlavniUI();
                //okDugme.setEnabled(false);
                prikaziSekundarniUI();
                List datumi = getDatesBetween(prvi, zadnji);

                for (int i = 0; i < datumi.size(); i++) {
                    PozivZaIDPolena poziv1 = new PozivZaIDPolena();
                    String url = urlZaListuKoncentracijaPT1 + izabranaLokacijaID + urlZaListuKoncentracijaPT2 + datumi.get(i).toString();
                    String datumString = datumi.get(i).toString();
                    poziv1.execute(url, datumString, identifikatorZaListu);
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
            gotovoPopunjavanjeTrenda=false;
            listaRezultataZaTrend.clear();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String startniDatum = listaRezultata.get(i).getDatum();
            Calendar c = Calendar.getInstance();
            c.setTime(df.parse(startniDatum));
            c.add(Calendar.DATE, 7);
            krajnjiDatumZaTrend = df.format(c.getTime());
            Date prvi = df.parse(startniDatum);
            Date zadnji = df.parse(krajnjiDatumZaTrend);
            List datumi = getDatesBetween(prvi, zadnji);

            for (int j = 0; j < datumi.size(); j++) {
                PozivZaIDPolena poziv1 = new PozivZaIDPolena();
                String url = urlZaListuKoncentracijaPT1 + izabranaLokacijaID + urlZaListuKoncentracijaPT2 + datumi.get(j).toString();
                String datumString = datumi.get(j).toString();
                poziv1.execute(url, datumString, identifikatorZaTrend);
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
                            if(gotovoPopunjavanjeTrenda){
                                int vrednostZaStampu = 0;
                                for (int j = 1; j < listaRezultataZaTrend.size(); j++) {
                                    int vrednost = listaRezultataZaTrend.get(j).getValue();
                                    if(vrednost == -1){
                                        vrednost = 0;
                                    }
                                    vrednostZaStampu += vrednost;
                                }
                                vrednostZaStampu = vrednostZaStampu/(listaRezultataZaTrend.size()-1);
                                if(vrednostZaStampu>listaRezultataZaTrend.get(0).getValue()){
                                    alert("Концентрација је у порасту у наредних 7 дана!");
                                }
                                else if(vrednostZaStampu<listaRezultataZaTrend.get(0).getValue()){
                                    alert("Концентрација је у опадању у наредних 7 дана!");
                                }
                                else{
                                    alert("Концентрација мирује у наредних 7 дана!");
                                }
                                timer.cancel();
                            }
                        }
                    });
                }
            }, delay, period);
        }
    }

    public void stampa(){
        rezultat = "";
        if(!gotovoPopunjavanje){
            rezultat = "PROCESSING THE REQUEST";
            //SMISLI KAKO DA PRIKAZES OVO!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        }
        else{
            for (int i = 0; i < listaRezultata.size(); i++) {
                Rezultat result = listaRezultata.get(i);
                if(result.getValue() == -1){
                    rezultat += (result.getDatum() + " За дати датум и локацију не постоје очитавања \n");
                }
                else{
                    if(result.getPrazan()){
                        rezultat += ("На дан " + result.getDatum() + " не постоји податак за дати алерген \n");
                    }
                    else{
                        rezultat += ("Концентрација алергена " + result.getAlergen() + " на дан " + result.getDatum() + " износи " + result.getValue() + "\n");
                    }
                }
            }
        }
    }

    public void popuniListu(String [] items){
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        prozorZaIspisListe.setAdapter(adapter);
    }

    public void zamraciGlavniUI(){
        spinnerLokacija.animate().alpha(0f).setDuration(animationDuration);
        spinnerLokacija.setEnabled(false);

        spinnerAlergena.animate().alpha(0f).setDuration(animationDuration);
        spinnerAlergena.setEnabled(false);

        pocetniDatumSelektor.animate().alpha(0f).setDuration(animationDuration);
        pocetniDatumSelektor.setEnabled(false);

        krajnjiDatumSelektor.animate().alpha(0f).setDuration(animationDuration);
        krajnjiDatumSelektor.setEnabled(false);

        goDugme.animate().alpha(0f).setDuration(animationDuration);
        goDugme.setEnabled(false);
    }

    public void prikaziGlavniUI(){
        spinnerLokacija.animate().alpha(1f).setDuration(animationDuration);
        spinnerLokacija.setEnabled(true);

        spinnerAlergena.animate().alpha(1f).setDuration(animationDuration);
        spinnerAlergena.setEnabled(true);

        pocetniDatumSelektor.animate().alpha(1f).setDuration(animationDuration);
        pocetniDatumSelektor.setEnabled(true);

        krajnjiDatumSelektor.animate().alpha(1f).setDuration(animationDuration);
        krajnjiDatumSelektor.setEnabled(true);

        goDugme.animate().alpha(1f).setDuration(animationDuration);
        goDugme.setEnabled(true);
    }

    public void zamraciSekundarniUI(){
        okDugme.animate().alpha(0f).setDuration(animationDuration);
        okDugme.setEnabled(false);

        prozorZaIspisListe.animate().alpha(0f).setDuration(animationDuration);
        prozorZaIspisListe.setEnabled(false);
    }

    public void prikaziSekundarniUI(){
        okDugme.animate().alpha(1f).setDuration(animationDuration);
        prozorZaIspisListe.animate().alpha(1f).setDuration(animationDuration);
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
}