package appstopper.mobile.cs.fsu.edu.appstopper;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static appstopper.mobile.cs.fsu.edu.appstopper.R.id.email;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;

    private DrawerLayout drawer;

    private static final String TAG = "HomeActivity";

    DashboardFragment dashFrag;

    Intent ServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ---- Top and Bottom NavBar colors ---- //
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.lightGreen));
            getWindow().setStatusBarColor(getResources().getColor(R.color.lightGreen));
        }

        boolean fFlag = getIntent().getBooleanExtra("FingerprintFlag",true);
        Log.d("xXx", "fFlag onCreate() " + fFlag);

        // ---- Display App List ---- //
        /* Scroll Menu display the list of packages on the phone */
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        dashFrag = new DashboardFragment();
        trans.add(R.id.home_fragment_container, dashFrag, "DashboardFragment");
        trans.commit();

        // ---- NavMenu ---- //
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Change NavBar current user */
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.email);
        FirebaseUser currentFirebaseUser = mAuth.getInstance().getCurrentUser();
        navUsername.setText(currentFirebaseUser.getEmail());
        //Log.e("CurrentUser", "is " + currentFirebaseUser.toString());

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    // ---- NavMenu ---- //
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.start_blocking:
                // ---- Start Service ---- //
                Log.v("StopperService", "Start blocking button");
                ServiceIntent = new Intent(getApplicationContext(), StopperService.class);
                startService(ServiceIntent);
                break;
            case R.id.stop_blocking:
                // ---- Stop Service ---- //
                Log.v("StopperService", "Stop blocking button");
                ServiceIntent = new Intent(HomeActivity.this, StopperService.class);
                stopService(ServiceIntent);
                break;
            case R.id.logout_button:
                // ---- Logout User ---- //
                FirebaseUser currentFirebaseUser = mAuth.getInstance().getCurrentUser();
                Toast.makeText(getApplicationContext(), "Logging out " + currentFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                ServiceIntent = new Intent(HomeActivity.this, StopperService.class);
                stopService(ServiceIntent);
                Intent backIntent = new Intent(getApplicationContext(), MainActivity.class);
                FirebaseAuth.getInstance().signOut();
                startActivity(backIntent);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean fFlag = getIntent().getBooleanExtra("FingerprintFlag",true);
        Log.d("xXx", "fFlag onResume() " + fFlag);
        if (fFlag) {
            Intent launchIntent = new Intent(this, FingerprintActivity.class);
            startActivity(launchIntent);
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        Intent launchIntent = new Intent(this, FingerprintActivity.class);
//        startActivity(launchIntent);
//    }
}
