package com.example.yunus.ototakip;

/**
 * Created by Yunus on 15.02.2017.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class YaklasanlarAdapter extends PagerAdapter implements ValueEventListener {

    private Context context;
    private HashMap<String, Araba> arabalar;
    private DatabaseReference databaseReference;
    private String userId;
    private LayoutInflater inflater;
    private List<String> arabaPlakalari;
    private long gunFarki;
    private String gunKalma;

    public YaklasanlarAdapter(@NonNull Context context,
                              @NonNull List<String> arabaPlakalar,
                              final DatabaseReference databaseReference,
                              final String userId) {

        super();
        this.context = context;
        this.arabaPlakalari = arabaPlakalar;
        arabalar = new HashMap<>();
        this.databaseReference = databaseReference;
        this.userId = userId;
        databaseReference.child("Arabalar")
                .child(userId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        String plaka = dataSnapshot.getKey();
                        if(arabalar.get(plaka) == null){
                            databaseReference.child("Arabalar")
                                    .child(userId)
                                    .child(plaka).addValueEventListener(YaklasanlarAdapter.this);
                        }

                        notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        String plaka = dataSnapshot.getKey();
                        if(arabalar.get(plaka) != null){
                            arabaPlakalari.remove(plaka);
                            arabalar.remove(plaka);
                            databaseReference.child("Arabalar")
                                    .child(userId)
                                    .child(plaka)
                                    .removeEventListener(YaklasanlarAdapter.this);
                        }

                        notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }



    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        String plaka = arabaPlakalari.get(position);
        Araba arabaBilgisi = arabalar.get(plaka);
        TextView muayeneTarihiTextView;
        TextView emisyonTarihiTextView;
        TextView sigortaTarihiTextView;
        TextView kaskoTarihiTextView;
        TextView plakaTextView;
        TextView muaGun,sigGun,kaskoGun,emiGun;
        TextView emisyonFark,sigortaFark,muayeneFark,kaskoFark;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.list_item_custom, null);
        muayeneTarihiTextView = (TextView) viewLayout.findViewById(R.id.muayeneTarihiTextView);
        kaskoTarihiTextView = (TextView) viewLayout.findViewById(R.id.kaskoTarihiTextView);
        emisyonTarihiTextView = (TextView) viewLayout.findViewById(R.id.emisyonTarihiTextView);
        sigortaTarihiTextView = (TextView) viewLayout.findViewById(R.id.sigortaTarihiTextView);
        plakaTextView = (TextView) viewLayout.findViewById(R.id.plakaTextView);
        emisyonFark= (TextView) viewLayout.findViewById(R.id.emisyonFark);
        sigortaFark= (TextView) viewLayout.findViewById(R.id.sigortaFark);
        muayeneFark= (TextView) viewLayout.findViewById(R.id.muayeneFark);
        kaskoFark= (TextView) viewLayout.findViewById(R.id.kaskoFark);
        muaGun= (TextView) viewLayout.findViewById(R.id.muaGunKaldi);
        sigGun= (TextView) viewLayout.findViewById(R.id.sigortaGunKaldi);
        kaskoGun= (TextView) viewLayout.findViewById(R.id.kaskoGunKaldi);
        emiGun= (TextView) viewLayout.findViewById(R.id.emisyonGunKaldi);
        muayeneTarihiTextView.setText(arabaBilgisi.getEditTextMuayeneTarihi());
        kaskoTarihiTextView.setText(arabaBilgisi.getEditTextKaskoTarihi());
        sigortaTarihiTextView.setText(arabaBilgisi.getEditTextSigortaTarihi());
        emisyonTarihiTextView.setText(arabaBilgisi.getEditTextEmisyonTarihi());
        plakaTextView.setText(arabaBilgisi.getEditTextPlaka()+" plakalı aracınızın");
        emisyonFark.setText(String.valueOf(gunFarkiniGetir(arabaBilgisi.getEditTextEmisyonTarihi())));
        emiGun.setText(gunKalma);
        sigortaFark.setText(String.valueOf(gunFarkiniGetir(arabaBilgisi.getEditTextSigortaTarihi())));
        sigGun.setText(gunKalma);
        muayeneFark.setText(String.valueOf(gunFarkiniGetir(arabaBilgisi.getEditTextMuayeneTarihi())));
        muaGun.setText(gunKalma);
        kaskoFark.setText(String.valueOf(gunFarkiniGetir(arabaBilgisi.getEditTextKaskoTarihi())));
        kaskoGun.setText(gunKalma);




        container.addView(viewLayout,0);
        return viewLayout;
    }



    @Override
    public int getCount()
    {

        return arabalar.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
        Araba araba = dataSnapshot.getValue(Araba.class);
        String plaka = dataSnapshot.getKey();
        try
        {
            if(plaka!=null)
            {
                araba.setEditTextPlaka(plaka);
                arabalar.put(plaka, araba);
                if(!arabaPlakalari.contains(plaka))
                {
                    arabaPlakalari.add(plaka);
                }

            }
        }
        catch (NullPointerException e)
        {
            Log.d("Hata","Plaka boş döndü. Hata: "+e.toString());
        }

        notifyDataSetChanged();
    }


    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        ((ViewPager) container).removeView((View) object);
    }

    public long gunFarkiniGetir(String gelenTarih)
    {
        Calendar takvim = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try
        {

            Date date1 = sdf.parse(gelenTarih);
            Date date2 = sdf.parse(sdf.format(takvim.getTime()));
            if(date1.after(date2))
            {
                gunFarki = (date1.getTime() - date2.getTime()) / 86400000;
                gunKalma="GÜN KALDI";

            }

            if(date1.before(date2))
            {
                gunFarki = (date2.getTime() - date1.getTime())/86400000;
                gunKalma="GÜN GECİKTİ";

            }
            if(date1.compareTo(date2)==0)
            {
                gunFarki = (date2.getTime() - date1.getTime())/86400000;
                gunKalma="GÜN KALDI - BUGÜN";

            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        Log.d("geldi", String.valueOf(gunFarki));

        return gunFarki;
    }


}