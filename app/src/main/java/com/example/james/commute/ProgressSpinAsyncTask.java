package com.example.james.commute;

import android.app.ProgressDialog;
import android.os.AsyncTask;

/**
 * Created by james on 12/6/2016.
 */

public class ProgressSpinAsyncTask extends AsyncTask <LocCord,Void,Void> {

    ProgressDialog pd;

    public ProgressSpinAsyncTask(MainActivity activity){
        pd= new ProgressDialog(activity);
    }

    @Override
    protected Void doInBackground(LocCord... params) {
        while(params[0].getLat()==0 && params[0].getLon()==0) {
        }
        return null;
    }
    @Override
    protected void onPreExecute(){
        pd.setIndeterminate(true);
        pd.setTitle("Getting Location");
        pd.show();
    }
    @Override
    protected void onPostExecute(Void v){
        pd.dismiss();
    }

}
