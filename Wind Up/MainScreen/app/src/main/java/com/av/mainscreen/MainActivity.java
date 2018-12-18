package com.av.mainscreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.av.mainscreen.activity.CallActivity;
import com.av.mainscreen.activity.TimerActivity;
import com.av.mainscreen.constants.SETTINGS;
import com.av.mainscreen.database.SyncWithDB;
import com.av.mainscreen.service.ForegroundService;
import com.rm.rmswitch.RMSwitch;

import static com.av.mainscreen.constants.SETTINGS.COMMON_SETTING;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener, RMSwitch.RMSwitchObserver {

    private static final String TAG = "MainActivity";
    public static Context ctx;

    private ViewPager mViewPager;
    private BottomSheetBehavior mBottomSheet;
    private CardFragmentPageAdapter mFragmentCardAdapter;
    private ShadowTransformer mFragmentCardShadowTransformer;
    private DrawerLayout mDrawer;
    private Toolbar mToolbar;

    /*widgets in content_main*/
    private Button btn_connect, btn_disConnect;
    private TextView tv_sheetTitle;
    private ImageButton btn_sheetIcon;
    private EditText et_mac;
    /*widgets in bottom sheet*/
    private RMSwitch tog_keepRunning, tog_bluetooth, tog_headphoneConnect, tog_headphoneDisconnect;
    private Spinner spinner_delayBtwMultipleClicks, spinner_clickInterval,
            spinner_singleDelay, spinner_singleRepeat,
            spinner_doubleDelay, spinner_doubleRepeat,
            spinner_trippleDelay, spinner_trippleRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = MainActivity.this;

        setupToolbar();
        init();
        setupBottomSheet();
        changeStatusBarColor();
        setupNavDrawer();
        setupCards();
        setupSettings();
        keyboard(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SyncWithDB.extractSettingsFromDB(this);
        SYNC();
    }

    private void SYNC() {
        tog_bluetooth.setChecked(COMMON_SETTING.CONNECT_BLUETOOTH_TRIGGER);
        tog_keepRunning.setChecked(COMMON_SETTING.KEEP_RUNNING);
        tog_headphoneConnect.setChecked(COMMON_SETTING.CONNECT_HEADPHONE_PLUGGED);
        tog_headphoneDisconnect.setChecked(COMMON_SETTING.DISCONNECT_HEADPHONE_REMOVED);
        spinner_clickInterval.setSelection(COMMON_SETTING.getPosClickInterval());
        spinner_delayBtwMultipleClicks.setSelection(COMMON_SETTING.getPosDelayMutiple());
        spinner_singleDelay.setSelection(SETTINGS.taps[1].getPosDelay());
        spinner_singleRepeat.setSelection(SETTINGS.taps[1].getPosRepeat());
        spinner_doubleDelay.setSelection(SETTINGS.taps[2].getPosDelay());
        spinner_doubleRepeat.setSelection(SETTINGS.taps[2].getPosRepeat());
        spinner_trippleDelay.setSelection(SETTINGS.taps[3].getPosDelay());
        spinner_trippleRepeat.setSelection(SETTINGS.taps[3].getPosRepeat());
    }

    /**
     * Includes all the toggle buttons, spinners : fvb, add listeners, setup data
     */
    private void setupSettings() {
        tog_bluetooth = findViewById(R.id.tog_bluetooth);
        tog_headphoneConnect = findViewById(R.id.tog_headphoneConnect);
        tog_headphoneDisconnect = findViewById(R.id.tog_headphoneDisconnect);
        tog_keepRunning = findViewById(R.id.tog_keepRunning);
        spinner_delayBtwMultipleClicks = findViewById(R.id.spinner_delayBtwMultipleClicks);
        spinner_clickInterval = findViewById(R.id.spinner_clickInterval);
        spinner_singleDelay = findViewById(R.id.spinner_singleDelay);
        spinner_singleRepeat = findViewById(R.id.spinner_singleRepeat);
        spinner_doubleDelay = findViewById(R.id.spinner_doubleDelay);
        spinner_doubleRepeat = findViewById(R.id.spinner_doubleRepeat);
        spinner_trippleDelay = findViewById(R.id.spinner_trippleDelay);
        spinner_trippleRepeat = findViewById(R.id.spinner_trippleRepeat);

        setupSpinner(spinner_singleDelay, R.array.array_vibration_delay, spinner_singleRepeat, R.array.array_vibration_repeat, true);
        setupSpinner(spinner_doubleDelay, R.array.array_vibration_delay, spinner_doubleRepeat, R.array.array_vibration_repeat, true);
        setupSpinner(spinner_trippleDelay, R.array.array_vibration_delay, spinner_trippleRepeat, R.array.array_vibration_repeat, true);
        setupSpinner(spinner_clickInterval, R.array.array_click_interval, spinner_delayBtwMultipleClicks, R.array.array_delay_btw_multiple_commands, false);

        tog_bluetooth.addSwitchObserver(this);
        tog_keepRunning.addSwitchObserver(this);
        tog_headphoneConnect.addSwitchObserver(this);
        tog_headphoneDisconnect.addSwitchObserver(this);
    }

    private void setupSpinner(Spinner delay, int array_delay, Spinner repeat, int array_repeat, boolean isTap) {
        ArrayAdapter<CharSequence> adapterDelay = ArrayAdapter.createFromResource(this,
                array_delay, (isTap) ? R.layout.spinner_item2 : R.layout.spinner_item1);
        adapterDelay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        delay.setAdapter(adapterDelay);

        ArrayAdapter<CharSequence> adapterRepeat = ArrayAdapter.createFromResource(this,
                array_repeat, (isTap) ? R.layout.spinner_item2 : R.layout.spinner_item1);
        adapterRepeat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeat.setAdapter(adapterRepeat);

        /*Add listeners*/
        delay.setOnItemSelectedListener(this);
        repeat.setOnItemSelectedListener(this);
    }

    private void setupCards() {
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

    private void setupNavDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_quickAccess);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.bringToFront();
    }

    private void changeStatusBarColor() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.notificationBar));
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(true); // hide built-in Title
    }

    private void init() {
        btn_connect = findViewById(R.id.btnConnect);
        btn_disConnect = findViewById(R.id.btnDisConnect);
        et_mac = findViewById(R.id.mac_address);
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SETTINGS.MAC_ADDRESS = et_mac.getText().toString();
                Intent intent = new Intent(MainActivity.this, ForegroundService.class);
                intent.setAction(ForegroundService.ACTION_START_FOREGROUND_SERVICE);
                startService(intent);
            }
        });
        btn_disConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForegroundService.class);
                intent.setAction(ForegroundService.ACTION_STOP_FOREGROUND_SERVICE);
                startService(intent);
            }
        });
        et_mac.setText(SETTINGS.MAC_ADDRESS);
        et_mac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboard(true);
            }
        });
    }

    private void setupBottomSheet() {
        tv_sheetTitle = findViewById(R.id.sheet_title);
        btn_sheetIcon = findViewById(R.id.sheet_icon);
        btn_sheetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheet.setState((mBottomSheet.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                        ? BottomSheetBehavior.STATE_EXPANDED
                        : BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        btn_sheetIcon.setImageResource(R.drawable.ic_more);
        tv_sheetTitle.setText("Tap for more Settings");
        mBottomSheet = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));
        mBottomSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.e(TAG, "onStateChanged: Hidden");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.e(TAG, "onStateChanged: expanded");
                        btn_sheetIcon.setImageResource(R.drawable.ic_close_black_24dp);
                        tv_sheetTitle.setText("Setting");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e(TAG, "onStateChanged: collapsed");
                        btn_sheetIcon.setImageResource(R.drawable.ic_more);
                        tv_sheetTitle.setText("Tap for more Settings");
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
        mBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public static float dpToPixels(int dp) {
        return dp * (ctx.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mBottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_quickAccess) {

        } else if (id == R.id.nav_Call) {
            Intent i = new Intent(MainActivity.this, CallActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_Timer) {
            Intent i = new Intent(MainActivity.this, TimerActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_alert) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View child, int pos, long id) {
        int selected = new Integer(((String) parent.getItemAtPosition(pos)).split(" ")[0]);
        // parent is the actual spinner and child is item which is clicked
        switch (parent.getId()) {
            case R.id.spinner_singleDelay:
                SETTINGS.taps[1].VIBRATE_DELAY = selected;
                break;
            case R.id.spinner_doubleDelay:
                SETTINGS.taps[2].VIBRATE_DELAY = selected;
                break;
            case R.id.spinner_trippleDelay:
                SETTINGS.taps[3].VIBRATE_DELAY = selected;
                break;
            case R.id.spinner_singleRepeat:
                SETTINGS.taps[1].VIBRATE_REPEAT = selected;
                break;
            case R.id.spinner_doubleRepeat:
                SETTINGS.taps[2].VIBRATE_REPEAT = selected;
                break;
            case R.id.spinner_trippleRepeat:
                SETTINGS.taps[3].VIBRATE_REPEAT = selected;
                break;
            case R.id.spinner_clickInterval:
                SETTINGS.COMMON_SETTING.CLICK_INTERVAL = selected;
                break;
            case R.id.spinner_delayBtwMultipleClicks:
                SETTINGS.COMMON_SETTING.DELAY_BTW_MULTIPLE_COMMANDS = selected;
                break;
        }
        SyncWithDB.putSettingsInDB(this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckStateChange(RMSwitch view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.tog_bluetooth:
                COMMON_SETTING.CONNECT_BLUETOOTH_TRIGGER = isChecked;
                break;
            case R.id.tog_keepRunning:
                COMMON_SETTING.KEEP_RUNNING = isChecked;
                break;
            case R.id.tog_headphoneConnect:
                COMMON_SETTING.CONNECT_HEADPHONE_PLUGGED = isChecked;
                break;
            case R.id.tog_headphoneDisconnect:
                COMMON_SETTING.DISCONNECT_HEADPHONE_REMOVED = isChecked;
                break;
        }
        SyncWithDB.putSettingsInDB(this);
    }

    public void keyboard(boolean show) {
        getWindow().setSoftInputMode(show ? WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
                : WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
