package com.example.ceb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity<FusedLocationProviderClient> extends AppCompatActivity {

    Button btn;
    String Latitude, Longitude,nameTXT,numberTXT,complaintTXT;
    FusedLocationProviderClient fusedLocationProviderClient;
    EditText name, number, complaint;
    DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.buttonlocaion);
        name=findViewById(R.id.editTextTextPersonName);
        number=findViewById(R.id.editTextPhone);
        complaint=findViewById(R.id.editTextTextMultiLine);
        DB=new DBHelper(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                MainActivity.this
        );

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 nameTXT = name.getText().toString();
                 numberTXT = number.getText().toString();
                 complaintTXT = complaint.getText().toString();

                if (ActivityCompat.checkSelfPermission(MainActivity.this
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this
                        , Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();

                }else {
                    ActivityCompat.requestPermissions(MainActivity.this
                    ,new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    ,Manifest.permission.ACCESS_COARSE_LOCATION}
                    ,100);
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100 && grantResults.length >0 && (grantResults[0] + grantResults[1]
        ==PackageManager.PERMISSION_GRANTED)){
            getCurrentLocation();
        }else{
            Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE
        );

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();

                    if (location != null){
                        Latitude=String.valueOf(location.getLatitude());
                        Longitude=String.valueOf(location.getLongitude());

                        Boolean checkInsert=DB.insertData(nameTXT,numberTXT,complaintTXT,Latitude,Longitude);
                        if (checkInsert==true){
                            Toast.makeText(getApplicationContext(),"Complaint Added Successfully ",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getApplicationContext(),"Complaint Not Added!!!! Try Again!!!",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback= new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                Latitude=String.valueOf(location1.getLatitude());
                                Longitude=String.valueOf(location1.getLongitude());

                                Boolean checkInsert=DB.insertData(nameTXT,numberTXT,complaintTXT,Latitude,Longitude);
                                if (checkInsert==true){
                                    Toast.makeText(getApplicationContext(),"Complaint Added Successfully ",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(getApplicationContext(),"Complaint Not Added!!!! Try Again!!!",Toast.LENGTH_SHORT).show();
                                }

                            }
                        };

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest
                        ,locationCallback, Looper.myLooper());

                    }

                }
            });
        }else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        }

    }
}