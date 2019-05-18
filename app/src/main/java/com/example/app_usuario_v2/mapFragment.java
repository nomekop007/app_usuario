package com.example.app_usuario_v2;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class mapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;

    public mapFragment() {
        // Required empty public constructor
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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        return v;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //se modifica el tipo de mapa a mostrar
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //habilitar para ubicacion real
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            // Show rationale and request permission.
            permisosDeGPS();
        }

        //habilita algunas funciones que bienen desabilitadad en google maps
        UiSettings uiSettings = mMap.getUiSettings();

        //boton de zoom
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.isMapToolbarEnabled();

    }
    public void permisosDeGPS() {

        int permissionCheck = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        //pregunta si no tiene permiso
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            //de ser asi pregunta
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
    }
}
