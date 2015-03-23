package com.w1441879.appointmentbooker;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import static android.provider.BaseColumns._ID;
import static com.w1441879.appointmentbooker.Constants.C_DATE;
import static com.w1441879.appointmentbooker.Constants.C_DESCRIP;
import static com.w1441879.appointmentbooker.Constants.C_TABLE_NAME;
import static com.w1441879.appointmentbooker.Constants.C_TIME;
import static com.w1441879.appointmentbooker.Constants.C_TITLE;


public class Translator extends Activity {
    private static final String TAG = "TranslateTask";
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private EditText origText;
    private TextView transText;
    private Button transButton;
    private String fromLang;
    private String toLang;
    private TextWatcher textWatcher;
    private OnItemSelectedListener itemListener;
    private OnClickListener buttonListener;
    // needed to make translate requests to Microsoft
    private String accessToken;

    String descrip, newDescrip;
    long ID;
    EditText eventDescrip;
    Button saveInput, cancelInput;
    AppointmentData appointData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translate_layout);
        findViews();
        setAdapters();
        setListeners();
        // get the access token from Microsoft
        new GetAccessTokenTask().execute();

        //set description
        appointData = new AppointmentData(this);
        getData();
    }

    private void getData(){
        ID = getIntent().getLongExtra("ID", 0);
        descrip = getIntent().getStringExtra("description");
        eventDescrip = (EditText)findViewById(R.id.input_text);
        eventDescrip.setText(descrip);

        saveInput=(Button)findViewById(R.id.save_button);
        cancelInput=(Button)findViewById(R.id.cancel_button);

        saveInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                transText = (TextView) findViewById(R.id.translated_text);
                newDescrip = transText.getText().toString();
                updateData();
                Toast.makeText(getApplicationContext(), "DESCRIPTION TRANSLATED", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        cancelInput.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateData(){
        SQLiteDatabase db = appointData.getWritableDatabase();

        final String selection = _ID + "=?";
        final String[] selectionArgs = { String.valueOf(ID) };

        ContentValues values = new ContentValues();
            values.put(C_DESCRIP, newDescrip);
        db.update(C_TABLE_NAME,values,selection,selectionArgs);
    }
    //TRANSLATOR CODE
    private void findViews() {
        fromSpinner = (Spinner) findViewById(R.id.from_language);
        toSpinner = (Spinner) findViewById(R.id.to_language);
        origText = (EditText) findViewById(R.id.input_text);
        transText = (TextView) findViewById(R.id.translated_text);
        transButton = (Button) findViewById(R.id.translate_button);
    }

    /** Define data source for the spinners */
    private void setAdapters() {
        // Spinner list comes from a resource,
        // Spinner user interface uses standard layouts
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this,
                        R.array.languages,
                        android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
        // Automatically select two spinner items
        fromSpinner.setSelection(8); // English (en)
        toSpinner.setSelection(22); // Klingon (tlh)
    }

    private void setListeners() {
        textWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s,
                                          int start, int count,
                                          int after) {
            }
            public void onTextChanged(CharSequence s,
                                      int start, int before,
                                      int count) {
    /*doTranslate2(origText.getText().toString().trim(),fromLang, toLang); */
            }
            public void afterTextChanged(Editable s) {
            }};

        origText.addTextChangedListener(textWatcher);
        itemListener = new OnItemSelectedListener() {
            public void onItemSelected(AdapterView parent, View v,
                                       int position, long id) {
                fromLang = getLang(fromSpinner);
                toLang = getLang(toSpinner);
                if (accessToken != null)
                    doTranslate2(origText.getText().toString().
                            trim(), fromLang, toLang);
            }
            public void onNothingSelected(AdapterView parent) {
            /* Do nothing */
            }
        };
        fromSpinner.setOnItemSelectedListener(itemListener)
        ;
        toSpinner.setOnItemSelectedListener(itemListener);

        buttonListener = new OnClickListener() {
            public void onClick(View v) {
                if (accessToken != null)
                    doTranslate2(origText.getText().toString().
                            trim(), fromLang, toLang);
            }
        };
        transButton.setOnClickListener(buttonListener);
    }
    /** Extract the language code from the current spinner item */

    private String getLang(Spinner spinner) {
        String result = spinner.getSelectedItem().toString();
        int lparen = result.indexOf("(");
        int rparen = result.indexOf(")");
        result = result.substring(lparen + 1, rparen);

        return result;
    }

    private void doTranslate2(String original,
                              String from, String to) {
        if (accessToken != null)
            new TranslationTask().execute(original, from, to);
    }private class TranslationTask extends AsyncTask<String,
            Void,
            String> {
        protected void onPostExecute(String translation) {
            transText.setText(translation);
        }
        protected String doInBackground(String... s) {
            HttpURLConnection con2 = null;
            String result = getResources().getString(
                    R.string.translation_error);
            String original = s[0];
            String from = s[1];
            String to = s[2];
            try {
        // Read results from the query
                BufferedReader reader;
                String uri = "http://api.microsofttranslator.com" +
                        "/v2/Http.svc/Translate?text=" +
                        URLEncoder.encode(original) +
                        "&from=" + from + "&to=" + to;
                URL url_translate = new URL(uri);
                String authToken = "Bearer" + " " + accessToken;
                con2 = (HttpURLConnection) url_translate.
                        openConnection();
                con2.setRequestProperty("Authorization", authToken);
                con2.setDoInput(true);
                con2.setReadTimeout(10000 /* milliseconds */);
                con2.setConnectTimeout(15000 /* milliseconds */);

                reader = new BufferedReader(new InputStreamReader(
                        con2.getInputStream(), "UTF-8"));
                String translated_xml = reader.readLine();
                reader.close();
/* translated_xml now contains the following XML:
<string xmlns="http://schemas.microsoft.com/
2003/10/Serialization/">Hola</string> */
// parse the XML returned
                DocumentBuilder builder = DocumentBuilderFactory.
                        newInstance().newDocumentBuilder();
                Document doc = builder.parse(new InputSource(
                        new StringReader(translated_xml)));
                NodeList node_list = doc.getElementsByTagName("string"
                );
                NodeList l = node_list.item(0).getChildNodes();
                Node node;
                String translated = null;
                if (l != null && l.getLength() > 0) {
                    node = l.item(0);
                    translated = node.getNodeValue();
                }
                if (translated != null)
                    result = translated;
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            finally {
                if (con2 != null) {
                    con2.disconnect();
                }
            }
            return result;
        }
    }

    private class GetAccessTokenTask extends AsyncTask<
            Void, Void, String> {
        protected void onPostExecute(String access_token) {
            accessToken = access_token;
        }
        protected String doInBackground(Void... v) {
            String result = null;
            HttpURLConnection con = null;
            String clientID = "w1441879";
            String clientSecret = "bishweebishweebishwee";
            String strTranslatorAccessURI =
                    "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13";
            String strRequestDetails = "grant_type=" +
                    "client_credentials&client_id="
                    + URLEncoder.encode(clientID)
                    + "&client_secret="
                    + URLEncoder.encode(clientSecret)
                    + "&scope=http://api.microsofttranslator.com";
            try {
                URL url = new URL(strTranslatorAccessURI);
                con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(10000 /* milliseconds */);
                con.setConnectTimeout(15000 /* milliseconds */);
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                con.setChunkedStreamingMode(0);
// Start the query
                con.connect();
                OutputStream out = new BufferedOutputStream(
                        con.getOutputStream());
                out.write(strRequestDetails.getBytes());
                out.flush();


                // Read results from the query
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "UTF-8"))
                        ;
                String payload = reader.readLine();
                reader.close();
                out.close();
/* payload now contains JSON attribute value pairs:
{"token_type":"http://schemas.xmlsoap.org/ws/2009/
11/swt-token-profile-1.0","access_token":"....",
"expires_in":"600","scope":
"http://api.microsofttranslator.com"} */
// Parse to get the access token
                JSONObject jsonObject = new JSONObject(payload);
                result = jsonObject.getString("access_token");
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);
            } catch (JSONException e) {
                Log.e(TAG, "JSONException", e);
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
            return result;
        }
    }
}
