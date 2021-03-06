package com.example.yunus.ototakip;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Yunus on 4.02.2017.
 */

public class AppRating
{

    private final static String APP_PNAME = "com.example.yunus.ototakip";// Paket Adi

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        int runCount = prefs.getInt("count",0) + 1;
        editor.putInt("count",runCount);

        SharedPreferences datePrefs = mContext.getSharedPreferences("SharedPrefDate", 0);
        SimpleDateFormat dfDate  = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cal = Calendar.getInstance();
        java.util.Date newDateVal = null;
        java.util.Date oldDateVal = null;
        try {
            newDateVal = dfDate.parse(dfDate.format(cal.getTime()));
            oldDateVal = dfDate.parse(datePrefs.getString("dateVal",""));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        int diffInDays = (int) ((newDateVal.getTime() -  oldDateVal.getTime())/ (1000 * 60 * 60 * 24));
        System.out.println("date"+diffInDays);

        if(diffInDays>=2){

            if(runCount%2==0){
                SharedPreferences dateShared = mContext.getSharedPreferences("SharedPrefDate", 0);
                SharedPreferences.Editor dateEditor  = dateShared.edit();
                Calendar today = Calendar.getInstance();
                dateEditor.putString("dateVal",dfDate.format(today.getTime()));
                dateEditor.commit();
                showRateDialog(mContext, editor);
            }

        }

        editor.commit();

    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {


        new MaterialStyledDialog.Builder(mContext)
                .setTitle("Oto Takip uygulamasını sevdiniz mi?")
                .setDescription("Google Play'de yorum yazarak Oto Takip uygulamasını başkalarına da önerin!")
                .setCancelable(false)
                .setNegativeText("HAYIR, BUNU BİR DAHA GÖSTERME")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        if (editor != null) {
                            editor.putBoolean("dontshowagain", true);
                            editor.commit();
                        }
                    }
                })
                .setNeutralText("BELKİ DAHA SONRA")
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("count",0);
                        editor.commit();

                    }
                })
                .setHeaderDrawable(R.drawable.appratebanner)
                .setPositiveText("EVET, ŞİMDİ DEĞERLENDİR")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                        Toast.makeText(mContext,"Uygulama henüz Google Play'de bulunmamakta. Takipte kalın!",Toast.LENGTH_LONG).show();
                    }
                })
                .show();

    }
}
