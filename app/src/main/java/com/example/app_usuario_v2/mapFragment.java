package com.example.app_usuario_v2;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import static android.view.View.VISIBLE;
import static com.facebook.FacebookSdk.getApplicationContext;


public class mapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference myDatabase;
    private View v;

    //arreglo de puntos para evitar que se llene de puntos el mapa
    private ArrayList<Marker> RealTimeMarkets = new ArrayList<>();

    //arreglo de puntos para evitar que se llene de perfiles de trasporte
    private ArrayList<Trasporte> tmpRealTimeTrasportes = new ArrayList<>();
    private ArrayList<Trasporte> ListaTrasportes = new ArrayList<>();

    private ArrayList<LineaTrasporte> lineasActuales = new ArrayList<>();

    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS;
    private int BORRADOR = 0;

    //donde se guarda los datos de la linea recibida
    private int ID = 0;

    //vetana de mapFragment
    private RelativeLayout ventana;
    private TextView nombreLinea;
    private View markerLayout = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        v = inflater.inflate(R.layout.fragment_map, container, false);


        //inicar venta en invisible
        ventana = v.findViewById(R.id.ventana);
        nombreLinea = v.findViewById(R.id.v_linea);

        ventana.setVisibility(v.INVISIBLE);
        Leyenda();
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
        if (ID != 0) {
            mostrarRecorrido(ID);

        }

        //que la aplicacion empieze con la ubicacion de talca
        LatLng Talca = new LatLng(-35.423244, -71.648483);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Talca, 13), 10, null);


        // click ventana de informacion que redirecciona a ventana
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                Intent intent = new Intent(getContext(), CalificarActivity.class);
                intent.putExtra("idTrasporte", marker.getSnippet());
                startActivity(intent);

            }
        });

        //metodo que llama a la ventana que muestra la info del trasporte
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {

                if (markerLayout == null) {
                    markerLayout = getLayoutInflater().inflate(R.layout.marker, null);
                }


                for (Trasporte trasporte : tmpRealTimeTrasportes) {

                    //busca el trasporte mediate la descripcion del marker seleccionado
                    if (marker.getSnippet().equals(trasporte.getIdTrasporte())) {


                        TextView titulo = markerLayout.findViewById(R.id.titulos);
                        TextView patente = markerLayout.findViewById(R.id.t_Patente);
                        TextView nomConductor = markerLayout.findViewById(R.id.t_Conductor);
                        TextView calificacion = markerLayout.findViewById(R.id.t_calificacion);
                        ImageView img = markerLayout.findViewById(R.id.iconos);
                        RatingBar ratingBar = markerLayout.findViewById(R.id.ratingBar);


                        Glide.with(markerLayout)
                                .load(trasporte.getFotoConductorUrl())
                                .fitCenter()
                                .centerCrop()
                                .circleCrop()
                                .error(R.drawable.error)
                                .placeholder(R.drawable.cargando)
                                .thumbnail(0.5f)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(img);


                        titulo.setText(marker.getTitle());
                        ratingBar.setRating(trasporte.getCalificacion());
                        patente.setText("Patente: " + trasporte.getPatente());
                        nomConductor.setText("Conductor: " + trasporte.getNombreConductor());
                        calificacion.setText("Calificacion: " + trasporte.getCalificacion());
                        return (markerLayout);
                    }
                }


                return null;
            }
        });
    }


    private void Leyenda() {
        //pregunta si hay linea seleccionada y extrae la ID en caso de que si
        if (getArguments() != null) {
            ID = getArguments().getInt("ID");
            nombreLinea.setText("Recorrido : " + getArguments().getString("linea"));
            ventana.setVisibility(v.VISIBLE);
        }

        // ID = getArguments() != null ? getArguments().getInt("ID") : 0;


    }

    private void mostrarRecorrido(int id) {

        switch (id) {
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
                                    markerOptions.snippet(trasporte.getIdTrasporte());


                                    //extrae el nombre de la linea y la coloca como titulo
                                    for (LineaTrasporte linea : lineasActuales) {
                                        if (linea.getIdLinea() == trasporte.getIdLinea()) {
                                            markerOptions.title(linea.getNombreLinea());
                                        }
                                    }


                                    //borra la coordenada antiguo y la remplaza por la nueva
                                    if (BORRADOR == 1) {
                                        for (Marker marker : RealTimeMarkets) {
                                            if (marker.getSnippet().equals(trasporte.getIdTrasporte())) {
                                                marker.remove();
                                            }
                                        }
                                    }



                                    //si las coordenadas son 0 quita el trasporte del mapa
                                    if (latitud != 0 || longitud != 0) {
                                        try {

                                            if (ID == 0) {
                                                RealTimeMarkets.add(mMap.addMarker(markerOptions));
                                            } else {
                                                if (trasporte.getIdLinea() == ID) {
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
