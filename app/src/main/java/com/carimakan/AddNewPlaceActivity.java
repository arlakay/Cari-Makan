package com.carimakan;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.carimakan.helper.SQLiteHandler;
import com.carimakan.helper.SessionManager;
import com.carimakan.util.AndroidMultiPartEntity;
import com.carimakan.util.Config;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AddNewPlaceActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    // LogCat tag
    private static final String TAG = AddNewPlaceActivity.class.getSimpleName();

    // Camera activity request com.sun.org.omg.SendingContext._CodeBaseStub
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE2 = 300;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri, fileUri2; // file url to store image/video

    AlertDialog.Builder alertDialogBuilder;
    private ProgressBar progressBar;
    private ImageView imgPreview, imgPreviewMenu;
    private VideoView vidPreview;
    long totalSize = 0;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    // UI elements
    private TextView labelLat, labelLng;
    private SessionManager session;
    private SQLiteHandler db;
    private EditText etnama, etalamat, etkategori, etUid, etDetail;
    private TextInputLayout inputLayoutNama, inputLayoutAlamat, inputLayoutKategori, inputLayoutDetail, inputLayoutUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnew);

        alertDialogBuilder = new AlertDialog.Builder(AddNewPlaceActivity.this);
        alertDialogBuilder.setCancelable(false);

        Button btnCapturePicture = (Button) findViewById(R.id.btnCapturePicture);
        Button btnCapturePictureMenu = (Button) findViewById(R.id.btnCapturePictureMenu);

        Button btnRecordVideo = (Button) findViewById(R.id.btnRecordVideo);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        imgPreviewMenu = (ImageView) findViewById(R.id.imgPreviewMenu);

        vidPreview = (VideoView) findViewById(R.id.videoPreview);
        etnama = (EditText) findViewById(R.id.etNama);
        etalamat = (EditText) findViewById(R.id.etAlamat);
        etkategori = (EditText) findViewById(R.id.etKategori);
        etUid = (EditText) findViewById(R.id.etUid);
        etDetail = (EditText) findViewById(R.id.etDetail);
        labelLat = (TextView) findViewById(R.id.lbllat);
        labelLng = (TextView) findViewById(R.id.lbllng);

        inputLayoutNama = (TextInputLayout) findViewById(R.id.input_layout_nama);
        inputLayoutAlamat = (TextInputLayout) findViewById(R.id.input_layout_alamat);
        inputLayoutKategori = (TextInputLayout) findViewById(R.id.input_layout_kategori);
        inputLayoutDetail = (TextInputLayout) findViewById(R.id.input_layout_detail);
        inputLayoutUid = (TextInputLayout) findViewById(R.id.input_layout_uid);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(getApplicationContext());  // session manager

        if (!session.isLoggedIn()) {
            //logoutUser();
            Intent i = new Intent(this, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // Staring Login Activity
            startActivity(i);
            finish();
        }

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String uid = user.get("uid");
        String name = user.get("name");
        String email = user.get("email");

        // displaying user data
        Log.d(TAG, "uid: <b>" + uid + "</b>");
        Log.d(TAG, "Name: <b>" + name + "</b>");
        Log.d(TAG, "Email: <b>" + email + "</b>");

        etUid.setText(uid);

        etnama.addTextChangedListener(new MyTextWatcher(etnama));
        etalamat.addTextChangedListener(new MyTextWatcher(etalamat));
        etkategori.addTextChangedListener(new MyTextWatcher(etkategori));
        etDetail.addTextChangedListener(new MyTextWatcher(etDetail));
        etUid.addTextChangedListener(new MyTextWatcher(etUid));


        // Capture image button click event
        btnCapturePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });

        btnCapturePictureMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // capture picture
                captureImage2();
            }
        });

        // Record video button click event
        btnRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // record video
                recordVideo();
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }

        labelLat = (TextView) findViewById(R.id.lbllat);
        labelLng = (TextView) findViewById(R.id.lbllng);

        ImageButton btnShowLocation = (ImageButton) findViewById(R.id.btnShowLocation);
        //btnStartLocationUpdates = (Button) findViewById(R.id.btnLocationUpdates);

        // First we need to check availability of play services
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

        // Show location button click listener
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                displayLocation();
            }
        });
    }

    // Checking device has camera hardware or not
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;    // this device has a camera
        } else {
            return false;   // no camera on this device
        }
    }

    // Launching camera app to capture image
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);  // start the image capture Intent
    }

    private void captureImage2() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri2 = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri2);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE2);  // start the image capture Intent
    }

    // Launching camera app to record video
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set video quality
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);  // start the video capture Intent
    }

    // Here we store the file url as it will be null after returning from camera app
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("file_uri", fileUri);    // save file url in bundle as it will be null on screen orientation changes
        outState.putParcelable("file_uri2", fileUri2);    // save file url in bundle as it will be null on screen orientation changes

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable("file_uri"); // get the file url
        fileUri2 = savedInstanceState.getParcelable("file_uri2"); // get the file url

    }

    // Receiving activity result method will be called after closing the camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // launching upload activity
                //launchUploadActivity(true);
                previewMedia(true);
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // launching upload activity
                //launchUploadActivity(false);
                previewMedia(false);
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE2) {
            if (resultCode == RESULT_OK) {
                // video successfully recorded
                // launching upload activity
                //launchUploadActivity(false);
                previewMedia2(true);
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    // Displaying captured image/video on the screen
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            imgPreviewMenu.setVisibility(View.VISIBLE);

            vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 12;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            imgPreview.setImageBitmap(bitmap);
        } else {
            imgPreview.setVisibility(View.GONE);
            vidPreview.setVisibility(View.VISIBLE);
            vidPreview.setVideoPath(fileUri.getPath());
            // start playing
            vidPreview.start();
        }
    }

    // Displaying captured image/video on the screen
    private void previewMedia2(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreviewMenu.setVisibility(View.VISIBLE);
            vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options2 = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options2.inSampleSize = 12;

            final Bitmap bitmap2 = BitmapFactory.decodeFile(fileUri2.getPath(), options2);
            imgPreviewMenu.setImageBitmap(bitmap2);
        } else {
            imgPreview.setVisibility(View.GONE);
            vidPreview.setVisibility(View.VISIBLE);
            vidPreview.setVideoPath(fileUri.getPath());
            // start playing
            vidPreview.start();
        }
    }

    // Uploading the file to server
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {

            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);
            // updating progress bar value
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);

            try {

                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                String nama = etnama.getText().toString();
                String alamat = etalamat.getText().toString();
                String kategori = etkategori.getText().toString();
                String uid = etUid.getText().toString();
                String lat = labelLat.getText().toString();
                String lng = labelLng.getText().toString();
                String detail = etDetail.getText().toString();
                //Log.d(TAG, "print detail: "+detail);

                File sourceFile = new File(fileUri.getPath());
                File sourceFileMenu = new File(fileUri2.getPath());

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));
                entity.addPart("imageMenu", new FileBody(sourceFileMenu));

                // Extra parameters if you want to pass to server
                entity.addPart("nama", new StringBody(nama));
                entity.addPart("alamat", new StringBody(alamat));
                entity.addPart("kategori", new StringBody(kategori));
                entity.addPart("lat", new StringBody(lat));
                entity.addPart("lng", new StringBody(lng));
                entity.addPart("uid", new StringBody(uid));
                entity.addPart("detail", new StringBody(detail));

                //logcat debug print
                Log.d(TAG, "print: " + sourceFile);
                Log.d(TAG, "print: " + sourceFileMenu);

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            } catch (NullPointerException e) {
                responseString = "No Image, Please Capture First!";
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);
            // showing the server response in an alert dialog
            showAlert(result);
            progressBar.setVisibility(View.GONE);

            super.onPostExecute(result);
        }
    }

    // Method to show alert dialog
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void submitForm() {
        if (!validateNama()) {
            return;
        }
        if (!validateAlamat()) {
            return;
        }
        if (!validateKategori()) {
            return;
        }
        if (!validateDetail()) {
            return;
        }
        if (!validateUid()) {
            return;
        }

        new UploadFileToServer().execute();
    }
//------------------------------------- Helper Methods ---------------------------------------------

    // Creating file uri to store image/video
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    // Returning image / video
    private static File getOutputMediaFile(int type) {
        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    //------------------------------------------Location--------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Method to display the location on UI
     * */
    private void displayLocation() {
        startLocationUpdates();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            labelLat.setText(latitude + "");
            labelLng.setText(longitude + "");

        } else {
            labelLat.setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        //Toast.makeText(getApplicationContext(), "Location changed!", Toast.LENGTH_SHORT).show();

        // Displaying the new location on UI
        //displayLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_addnewplace, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //new UploadFileToServer().execute();
                //submitForm();

                alertDialogBuilder.setMessage("Maaf Fitur ini dinonaktifkan untuk sementara waktu");
                alertDialogBuilder.setPositiveButton("GOT IT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Intent i = new Intent(getApplicationContext(), Main2Activity.class);
                        //startActivity(i);
                        finish();
                    }
                });
                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean validateNama() {
        if (etnama.getText().toString().trim().isEmpty()) {
            inputLayoutNama.setError(getString(R.string.err_msg_nama));
            requestFocus(etnama);
            return false;
        } else {
            inputLayoutNama.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateAlamat() {
        if (etalamat.getText().toString().trim().isEmpty()) {
            inputLayoutAlamat.setError(getString(R.string.err_msg_alamat));
            requestFocus(etalamat);
            return false;
        } else {
            inputLayoutAlamat.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateKategori() {
        if (etkategori.getText().toString().trim().isEmpty()) {
            inputLayoutKategori.setError(getString(R.string.err_msg_kategori));
            requestFocus(etkategori);
            return false;
        } else {
            inputLayoutKategori.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateDetail() {
        if (etDetail.getText().toString().trim().isEmpty()) {
            inputLayoutDetail.setError(getString(R.string.err_msg_detail));
            requestFocus(etDetail);
            return false;
        } else {
            inputLayoutDetail.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateUid() {
        if (etUid.getText().toString().trim().isEmpty()) {
            inputLayoutUid.setError(getString(R.string.err_msg_uid));
            requestFocus(etUid);
            return false;
        } else {
            inputLayoutUid.setErrorEnabled(false);
        }
        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.etNama:
                    validateNama();
                    break;
                case R.id.etAlamat:
                    validateAlamat();
                    break;
                case R.id.etKategori:
                    validateKategori();
                    break;
                case R.id.etDetail:
                    validateDetail();
                    break;
                case R.id.etUid:
                    validateUid();
                    break;

            }
        }
    }

}