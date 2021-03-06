package ua.radioline.novaposhtasmsnotification.idoc;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ua.radioline.novaposhtasmsnotification.MainActivity;
import ua.radioline.novaposhtasmsnotification.basic.BaseValues;
import ua.radioline.novaposhtasmsnotification.basic.InternetDocument;
import ua.radioline.novaposhtasmsnotification.util.HttpUtil;
import ua.radioline.novaposhtasmsnotification.util.Response;

/**
 * Created by mikoladyachok on 12/30/15.
 */
public class InternetDocumentAsyncTask extends AsyncTask<String, Void, Response> {
    private InternetDocumentOnTaskListener listener;
    private final static String SERVICE_URL = "https://api.novaposhta.ua/v2.0/json/";
    private boolean forsend;


    private Context mContext;



    public InternetDocumentAsyncTask(Context mContext, InternetDocumentOnTaskListener listener) {
        this.mContext = mContext;
        this.listener=listener;
    }

    public InternetDocumentAsyncTask(InternetDocumentOnTaskListener listener) {
        this.listener = listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onTaskStarted();

    }


    @Override
    protected Response doInBackground(String... params) {
        Response response = null;


        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("apiKey", BaseValues.GetValue("KeyAPI"));
            jsonObject.put("modelName", "InternetDocument");
            jsonObject.put("calledMethod", "getDocumentList");
            jsonObject.put("methodProperties", new JSONObject().put("DateTime",params[0]));
            if ((params.length>1)&&params[1].isEmpty())
            {
                forsend=false;
            }
            else if ((params.length>1))
            {
                forsend = true;
            }
            else
             forsend = false;
//            jsonObject.put("nome", mUser.getUsername());
//            jsonObject.put("senha", mUser.getPassword());


            response = HttpUtil.sendJSONPostResquest(jsonObject, SERVICE_URL);
        } catch (IOException ioex) {
            Log.e("IOException", ioex.getMessage());
        } catch (JSONException jsonex) {
            Log.e("JSONException", jsonex.getMessage());
        }


        return response;
    }


    @Override
    protected void onPostExecute(Response response) {
        super.onPostExecute(response);

//        if ((mProgressDialog!=null)&&(mProgressDialog.isShowing()))
//            mProgressDialog.dismiss();


        JSONObject jsonObject = null;


        try {
            jsonObject = new JSONObject(response.getContentValue());
            ArrayList<InternetDocument> internetDocuments = InternetDocument.fromJson(jsonObject.getJSONArray("data"));
            if (internetDocuments!=null)
                listener.onTaskCompleted(internetDocuments,forsend);
        } catch (JSONException jsonex) {
            Toast.makeText(MainActivity.getContextOfApplication(), "JSON Errors:" + jsonex.getMessage(),Toast.LENGTH_LONG);
            Log.e("JSONException", jsonex.getMessage());

        }catch (NullPointerException ex){
            Toast.makeText(MainActivity.getContextOfApplication(), "No data!:" + ex.getMessage(), Toast.LENGTH_LONG);
            Log.e("NullPointerException", ex.getMessage());
        }



    }


}