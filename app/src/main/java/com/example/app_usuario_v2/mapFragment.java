package com.example.app_usuario_v2;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.app_usuario_v2.model.LineaTrasporte;
import com.example.app_usuario_v2.model.Trasporte;
import com.example.app_usuario_v2.model.coordenada;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class mapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference myDatabase;

    //arreglo de puntos para evitar que se llene de puntos el mapa
    private ArrayList<Marker> RealTimeMarkets = new ArrayList<>();

    //arreglo de puntos para evitar que se llene de perfiles de trasporte
    private ArrayList<Trasporte> tmpRealTimeTrasportes = new ArrayList<>();
    private ArrayList<Trasporte> ListaTrasportes = new ArrayList<>();

    private ArrayList<LineaTrasporte> lineasActuales = new ArrayList<>();

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        //preguntar si los servicios de google estan corriendo

        if (status == ConnectionResult.SUCCESS) {

            SupportMapFragment mapFragment = (SupportMapFragment)
                    getChildFragmentManager().findFragmentById(R.id.mapa);
            mapFragment.getMapAsync(this);
        } else {

            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, (Activity) getContext(), 10);
            dialog.show();
        }

        myDatabase = FirebaseDatabase.getInstance().getReference();


        SolicitudDePermisoGPS();

        octenerLineasTrasporte();
        octenerTrasportes();
        octenerUbicacionEnTimpoReal();

    }

    public void SolicitudDePermisoGPS() {

        //pregunta si el permiso no esta dado
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Log.e("permiso : ", "no dado");

            //si no esta dado , habre la ventana de pregunta
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return;

        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //se modifica el tipo de mapa a mostrar
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);


        //habilita algunas funciones que bienen desabilitadad en google maps
        UiSettings uiSettings = mMap.getUiSettings();

        //boton de zoom
        uiSettings.setZoomControlsEnabled(true);

        //que la aplicacion empieze con la ubicacion de talca
        LatLng Talca = new LatLng(-35.423244, -71.648483);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Talca, 13), 200, null);

        //metodo para ordenar informacion de los marcadores
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }


    public int BORRADOR = 0;

    private void octenerUbicacionEnTimpoReal() {

        //este metodo se ejecuta cada ves que hay un cambio en la base de datos
        myDatabase.child("coordenada").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    coordenada c = snapshot.getValue(coordenada.class);


                    myDatabase.child("coordenada").child(c.getIdTrasporte()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            coordenada cord = dataSnapshot.getValue(coordenada.class);
                            double latitud = cord.getLatitud();
                            double longitud = cord.getLongitud();


                            if (latitud != 0 || longitud != 0) {
                                for (Trasporte trasporte : ListaTrasportes) {

                                    if (trasporte.getIdTrasporte().equals(cord.getIdTrasporte())) {

                                        MarkerOptions markerOptions = new MarkerOptions();
                                        markerOptions.position(new LatLng(latitud, longitud));

                                        markerOptions.icon(vectorToBitmap(R.drawable.bus, Color.parseColor("#E22214")));
                                        markerOptions.snippet("Patente: " + trasporte.getPatente() + "\n"
                                                + "Conductor: " + trasporte.getNombreConductor() + "\n "
                                                + "calificacion : " + trasporte.getCalificacion());


                                        for (LineaTrasporte linea : lineasActuales) {
                                            if (linea.getIdLinea() == trasporte.getIdLinea()) {
                                                markerOptions.title(linea.getNombreLinea());
                                            }
                                        }

                                        if (BORRADOR == 1) {
                                            for (Marker marker : RealTimeMarkets) {
                                                if (marker.getSnippet().equals(markerOptions.getSnippet())) {
                                                    marker.remove();
                                                }
                                            }
                                        }

                                        RealTimeMarkets.add(mMap.addMarker(markerOptions));
                                    }
                                }


                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

                BORRADOR = 1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void octenerTrasportes() {

        myDatabase.child("trasporte").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                //el try evita la inconsistencia de datos
                try {
                    for (Trasporte trasporte : ListaTrasportes) {
                        ListaTrasportes.remove(trasporte);
                    }
                } catch (Exception e) {
                    tmpRealTimeTrasportes.clear();

                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Trasporte trasporte = snapshot.getValue(Trasporte.class);
                    tmpRealTimeTrasportes.add(trasporte);
                }


                ListaTrasportes.clear();
                ListaTrasportes.addAll(tmpRealTimeTrasportes);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void octenerLineasTrasporte() {
        myDatabase.child("lineaTrasporte").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LineaTrasporte linea = snapshot.getValue(LineaTrasporte.class);
                    lineasActuales.add(linea);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    //metodo para agregar imagen en ves de marcador
    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        try {
            Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            DrawableCompat.setTint(vectorDrawable, color);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        }catch (Exception e){
            Log.e("error : ","en vectorToBitmap");
                return null;

        }

    }


}
