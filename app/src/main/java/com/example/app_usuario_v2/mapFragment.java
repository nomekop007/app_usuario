package com.example.app_usuario_v2;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.app_usuario_v2.model.LineaTrasporte;
import com.example.app_usuario_v2.model.Trasporte;
import com.example.app_usuario_v2.model.coordenada;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.maps.android.data.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;


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
    private int BORRADOR = 0;

    //donde se guarda la ID recibida
    private int ID = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            //pregunta si hay linea seleccionada y extrae la ID en caso de que si
       ID = getArguments() != null ? getArguments().getInt("ID") : 0;

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

    private void mostrarRecorrido(int id) {

        switch (id){
            case 100:

                try {
                    KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.linea_a, getApplicationContext());
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                break;
            case 101:
                try {
                    KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.linea_b, getApplicationContext());
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                break;
            case 102:
                try {
                    KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.linea_c, getApplicationContext());
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                break;
            case 103:
                try {
                    KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.linea_d, getApplicationContext());
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                break;
            case 104:
                try {
                    KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.linea_colin, getApplicationContext());
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }

                break;
            case 105:
                try {
                    KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.linea_1, getApplicationContext());
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                break;
            case 106:
                try {
                    KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.linea_2, getApplicationContext());
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                break;
            case 107:
                try {
                    KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.linea_4, getApplicationContext());
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                break;
            case 108:
                try {
                    KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.linea_6, getApplicationContext());
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                break;
            case 109:
                try {
                    KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.linea_3, getApplicationContext());
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                break;
            case 110:
                try {
                    KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.linea_3b, getApplicationContext());
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                break;
            case 111:
                try {
                    KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.linea_5, getApplicationContext());
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                break;
            case 112:
                try {
                    KmlLayer kmlLayer = new KmlLayer(mMap, R.raw.linea_7, getApplicationContext());
                    kmlLayer.addLayerToMap();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                break;
        }

    }




    private void SolicitudDePermisoGPS() {

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


        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);


        //habilita algunas funciones que bienen desabilitadad en google maps
        UiSettings uiSettings = mMap.getUiSettings();

        //boton de zoom
        uiSettings.setZoomControlsEnabled(true);

        //llama al metodo si es que hay una linea seleccionada
        if (ID != 0){
            mostrarRecorrido(ID);
        }

        //que la aplicacion empieze con la ubicacion de talca
        LatLng Talca = new LatLng(-35.423244, -71.648483);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Talca, 13), 10, null);

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


    private void octenerUbicacionEnTimpoReal() {

        //este metodo se ejecuta una vez para extraer las coordenadas de la base de datos
        myDatabase.child("coordenada").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    coordenada c = snapshot.getValue(coordenada.class);


                    //este metodo se ejecuta cada vez que la coordenada especifica sufre un cambio
                    myDatabase.child("coordenada").child(c.getIdTrasporte()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            coordenada cord = dataSnapshot.getValue(coordenada.class);
                            double latitud = cord.getLatitud();
                            double longitud = cord.getLongitud();


                            for (Trasporte trasporte : ListaTrasportes) {

                                if (trasporte.getIdTrasporte().equals(cord.getIdTrasporte())) {

                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(new LatLng(latitud, longitud));

                                    markerOptions.icon(bitmapDescriptorFromVector(getContext(), R.drawable.icon));

                                    String patente = trasporte.getPatente();
                                    String conductor = trasporte.getNombreConductor();
                                    String calificacion = trasporte.getCalificacion() + "";
                                    int idLinea = trasporte.getIdLinea();

                                    markerOptions.snippet("Patente: " + patente + "\n"
                                            + "Conductor: " + conductor + "\n "
                                            + "calificacion : " + calificacion);


                                    //extrae el nombre de la linea
                                    for (LineaTrasporte linea : lineasActuales) {
                                        if (linea.getIdLinea() == idLinea) {
                                            markerOptions.title(linea.getNombreLinea());
                                        }
                                    }


                                    //borra la coordenada antiguo y la remplaza por la nueva
                                    if (BORRADOR == 1) {

                                        for (Marker marker : RealTimeMarkets) {
                                            if (marker.getSnippet().equals(markerOptions.getSnippet())) {
                                                marker.remove();
                                            }
                                        }
                                    }

                                    if (latitud != 0 || longitud != 0) {
                                        try {

                                            if (ID == 0) {
                                                RealTimeMarkets.add(mMap.addMarker(markerOptions));
                                            } else {
                                                if (idLinea == ID) {
                                                    RealTimeMarkets.add(mMap.addMarker(markerOptions));
                                                }
                                                //de lo contrario no se mostrara el marker

                                            }

                                        } catch (Exception e) {

                                        }
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

    private void octenerTrasportes() {

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

    private void octenerLineasTrasporte() {
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
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        try {
            Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
            return BitmapDescriptorFactory.fromBitmap(bitmap);
        } catch (Exception e) {
            Log.e("error : ", "en vectorToBitmap");
            return null;

        }
    }


}
