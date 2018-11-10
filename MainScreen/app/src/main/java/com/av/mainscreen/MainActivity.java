package com.av.mainscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Context ctx;

    private ViewPager mViewPager;
    private BottomSheetBehavior bottomSheet;
    private TextView sheetTitle;
    private ImageButton sheetIcon;

    private CardFragmentPageAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;

    private boolean mShowingFragments = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(true); // hide built-in Title

        setupBottomSheet();

        ctx = MainActivity.this;

        // changing color of status bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.notificationBar));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.bringToFront();

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mFragmentCardAdapter = new CardFragmentPageAdapter(getSupportFragmentManager(),
                dpToPixels(2));
        mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);
        mViewPager.setAdapter(mFragmentCardAdapter);
        mViewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);
        mFragmentCardShadowTransformer.enableScaling(true);
        mViewPager.setPageMargin(5);
    }

    private static final String TAG = "MainActivity";

    private void setupBottomSheet() {
        sheetTitle = findViewById(R.id.sheet_title);
        sheetIcon = findViewById(R.id.sheet_icon);
        sheetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheet.setState((bottomSheet.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                        ? BottomSheetBehavior.STATE_EXPANDED
                        : BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        sheetIcon.setImageResource(R.drawable.ic_more);
        sheetTitle.setText("Tap for more Settings");
        bottomSheet = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        bottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e(TAG, "onStateChanged: Hidden");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.e(TAG, "onStateChanged: expanded");
                        sheetIcon.setImageResource(R.drawable.ic_close_black_24dp);
                        sheetTitle.setText("Setting");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e(TAG, "onStateChanged: collapsed");
                        sheetIcon.setImageResource(R.drawable.ic_more);
                        sheetTitle.setText("Tap for more Settings");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.e(TAG, "onStateChanged: dragging");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.e(TAG, "onStateChanged: settling");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        bottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        final Spinner spinner = (Spinner) findViewById(R.id.DT_s);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_interval, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }

    public static float dpToPixels(int dp) {
        return dp * (ctx.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /** How to change colour of drawable bitmap
     *
     * Drawable drawable = getResources().getDrawable(R.drawable.menu);
     * Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_ham);
     * Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
     * Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 24, 24, true));
     * newdrawable.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
     */
}
