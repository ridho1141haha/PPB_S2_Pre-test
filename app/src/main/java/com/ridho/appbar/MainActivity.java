package com.ridho.appbar;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Toolbar Setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Adalah pokoknya");
        }

        // 2. Navigation Drawer Setup
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(this, "Home dipilih", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_gallery) {
                Toast.makeText(this, "Gallery dipilih", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_slideshow) {
                Toast.makeText(this, "Slideshow dipilih", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // 3. Context Menu Setup
        TextView tvContent = findViewById(R.id.tv_content);
        registerForContextMenu(tvContent);
    }

    // --- Options Menu ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            showSettings();
            return true;
        } else if (id == R.id.action_favorites) {
            showFavorites();
            return true;
        } else if (id == R.id.action_delete) {
            showDelete();
            return true;
        } else if (id == R.id.action_save) {
            showSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // --- Context Menu ---
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
        menu.setHeaderTitle(R.string.context_menu_title);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_edit) {
            Toast.makeText(this, "Edit dipilih dari Context Menu", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_share) {
            Toast.makeText(this, "Share dipilih dari Context Menu", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.menu_delete) {
            Toast.makeText(this, "Delete dipilih dari Context Menu", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    // --- Helper Methods ---
    private void showSettings() {
        Toast.makeText(this, getString(R.string.settings_message), Toast.LENGTH_SHORT).show();
    }

    private void showFavorites() {
        Toast.makeText(this, getString(R.string.favorites_message), Toast.LENGTH_SHORT).show();
    }

    private void showDelete() {
        Toast.makeText(this, getString(R.string.delete_message), Toast.LENGTH_SHORT).show();
    }

    private void showSave() {
        Toast.makeText(this, getString(R.string.save_message), Toast.LENGTH_SHORT).show();
    }
}