package si.uni_lj.fri.pbd.miniapp1;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    // Configuration option for NavigationUI methods (for Toolbar)
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // sets a toolbar (app_bar_main.xml)
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // todo: we don't need FAB
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // (activity_main.xml)
        // DrawerLayout allows interactive "drawer" views to be pulled out from edges of the window
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        //
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        // nav_host_fragment in content_main.xml
        // finds a NavController given the id of a View and its containing Activity
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // sets up the ActionBar returned by AppCompactActivity (this activity) for use with a NavController
        // AppBarConfiguration controls how the navigation button is displayed
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        // sets up a NavigationView for use with a NavController
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    // settings menu on the right
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // this methode is called whenever the user chooses to navigate UP within your application's
    // activity hierarchy from the action bar (to handle left up button)
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
