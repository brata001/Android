package com.poc.evault.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.poc.evault.R;
import com.poc.evault.adapters.DocListAdapter;
import com.poc.evault.adapters.SpacesItemDecoration;
import com.poc.evault.callbacks.OnDeleteClick;
import com.poc.evault.model.Item;
import com.poc.evault.utils.DocumentDataSource;
import com.sa90.materialarcmenu.ArcMenu;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DASP2 on 4/1/2017.
 */
@TargetApi(Build.VERSION_CODES.M)
public class HomeActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private static final int REQUEST_CODE = 99;
    private GoogleApiClient mGoogleApiClient;
    private KeyStore keyStore;
    private static final String KEY_NAME = "androidHive";
    private Cipher cipher;
    private Dialog dialog;
    private boolean isAuthenticationRequired = true;
    private static final int PERMISSIONS_REQUEST = 100;
    private String SERVER_BASE_URL = "http://49.12.33.176/TDLockerServer/";
    private String SERVER_URL = SERVER_BASE_URL + "server.php";
    private ProgressDialog mProgressDialog;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private String PATH = Environment.getExternalStorageDirectory() + "/";
    private String EXTENSION = ".pdf";
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Item> listItem;
    private boolean cameraClicked = false;
    private boolean emailClicked = false;
    private ArcMenu arcMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        String name = "";
        String email = "";
        String photoUrl = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("NAME");
            email = bundle.getString("EMAIL");
            photoUrl = bundle.getString("URL");
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        arcMenu = (ArcMenu) findViewById(R.id.arcMenu);
        ImageView imgCamera = (ImageView) findViewById(R.id.camera);
        ImageView imgSDCard = (ImageView) findViewById(R.id.sd_card);
        ImageView imgFamilyEmail = (ImageView) findViewById(R.id.family_email);
        ImageView imgLogout = (ImageView) findViewById(R.id.logout);
        TextView txtName = (TextView) findViewById(R.id.name);
        txtName.setText(name);
        TextView txtEmail = (TextView) findViewById(R.id.email);
        txtEmail.setText(email);
        CircleImageView imgUserPhoto = (CircleImageView) findViewById(R.id.user_photo);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(40));

       /*  listItem = new ArrayList<>();
        DocumentDataSource documentDataSource=new DocumentDataSource(this);
        documentDataSource.open();
        listItem= documentDataSource.getAllDocuments();
        documentDataSource.close();*/
        updateList();
       /* Item item = new Item();
        item.setId(1l);
        item.setName("ABC.pdf");
        item.setUploadDate("April 4 2017");
        item.setSize("0.10MB");
        item.setType("pdf");
        listItem.add(item);
        item = new Item();
        item.setId(2l);
        item.setName("AAAA.doc");
        item.setUploadDate("April 5 2017");
        item.setSize("0.20MB");
        item.setType("doc");
        listItem.add(item);
        item = new Item();
        item.setId("3");
        item.setName("ffdfdsf.xlsx");
        item.setUploadDate("April 6 2017");
        item.setSize("1MB");
        item.setType("xlsx");
        listItem.add(item);*/

        /*if(listItem.size()>0) {
            mRecyclerView.setAdapter(new DocListAdapter(listItem, this));
        }*/
        DownloadImageTask task = new DownloadImageTask(imgUserPhoto);
        task.execute(photoUrl);

        imgCamera.setOnClickListener(this);
        imgSDCard.setOnClickListener(this);
        imgFamilyEmail.setOnClickListener(this);
        imgLogout.setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        generateKey();
        cipherInit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera:
                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }
                cameraClicked = true;
                checkPermissions();
                break;
            case R.id.sd_card:
                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }
                cameraClicked = false;
                checkPermissions();
                break;
            case R.id.family_email:
                if (arcMenu.isMenuOpened()) {
                    arcMenu.toggleMenu();
                }
                emailClicked = true;
                cameraClicked = false;
                initFingerprint();
                showDialog();
                break;
            case R.id.logout:
                signOut();
                startActivity(new Intent(HomeActivity.this, SplashActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    public void showDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_fingerprint);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isAuthenticationRequired = false;
            }
        });
    }

    private void initFingerprint() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (!fingerprintManager.isHardwareDetected()) {
            Toast.makeText(this, "Your Device does not have a Fingerprint Sensor", Toast.LENGTH_LONG).show();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Fingerprint authentication permission not enabled", Toast.LENGTH_LONG).show();
            } else {
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    Toast.makeText(this, "Register at least one fingerprint in Settings", Toast.LENGTH_LONG).show();
                } else {
                    if (!keyguardManager.isKeyguardSecure()) {
                        Toast.makeText(this, "Lock screen security not enabled in Settings", Toast.LENGTH_LONG).show();
                    } else {
                        isAuthenticationRequired = true;
                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                           /* FingerprintHandler helper = new FingerprintHandler(this);
                            helper.startAuth(fingerprintManager, cryptoObject);*/
                        fingerprintManager.authenticate(cryptoObject, new CancellationSignal(), 0, authenticationCallback, null);
                    }
                }
            }
        }
    }

    private FingerprintManager.AuthenticationCallback authenticationCallback = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            animateFingerprintDialog();
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            animateFingerprintDialog();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            if (isAuthenticationRequired) {
                dialog.dismiss();
                isAuthenticationRequired = false;
                if (cameraClicked) {
                    cameraClicked = false;
                    startScan(ScanConstants.OPEN_CAMERA);
                } else if (emailClicked) {
                    startActivity(new Intent(HomeActivity.this, RegisterEmailActivity.class));
                } else {
                    openMediaContent();
                }

            }
        }

        @Override
        public void onAuthenticationFailed() {
            animateFingerprintDialog();
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    protected void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            e.printStackTrace();
        }

        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get KeyGenerator instance", e);
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();
        } catch (NoSuchAlgorithmException |
                InvalidAlgorithmParameterException
                | CertificateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private void startVibration() {
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
    }

    private void startShakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.animation_shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //clearPasscodeFields();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        ((ViewGroup) dialog.getWindow().getDecorView())
                .getChildAt(0).startAnimation(shake);
    }

    private void animateFingerprintDialog() {
        startShakeAnimation();
        startVibration();
        Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onBackPressed();
    }

    private void startScan(int preference) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            final Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_file_name);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            final EditText edtFilename = (EditText) dialog.findViewById(R.id.name_field);
            Button btnOk = (Button) dialog.findViewById(R.id.submit);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(edtFilename);
                    String name = edtFilename.getText().toString().trim();
                    if (name.equals("")) {
                        edtFilename.setError("Please enter valid name");
                    } else {
                        dialog.dismiss();
                        showProgressDialog(R.string.uploading);
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            String filePath = PATH + name + EXTENSION;
                            uploadImage(rotateBitmap(bitmap, 90), filePath, name);
                        } catch (Exception e) {
                            hideProgressDialog();
                            showMessage("Upload failed", failureClickListener);
                            e.printStackTrace();
                        } finally {
                            getContentResolver().delete(uri, null, null);
                            if (bitmap != null)
                                bitmap.recycle();
                        }
                    }
                }
            });

            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    getContentResolver().delete(uri, null, null);
                }
            });
        } else if (requestCode == ScanConstants.PICKFILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_file_name);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            final EditText edtFilename = (EditText) dialog.findViewById(R.id.name_field);
            Button btnOk = (Button) dialog.findViewById(R.id.submit);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideKeyboard(edtFilename);
                    String name = edtFilename.getText().toString().trim();
                    if (name.equals("")) {
                        edtFilename.setError("Please enter valid name");
                    } else {
                        dialog.dismiss();
                        showProgressDialog(R.string.uploading);
                        Bitmap bitmap = null;
                        try {
                            bitmap = getBitmap(data.getData());
                            String filePath = PATH + name + EXTENSION;
                            uploadImage(rotateBitmap(bitmap, 90), filePath, name);
                        } catch (Exception e) {
                            hideProgressDialog();
                            showMessage("Upload failed", failureClickListener);
                            e.printStackTrace();
                        } finally {
                            if (bitmap != null)
                                bitmap.recycle();
                        }
                    }
                }
            });
        }
    }

    private void checkPermissions() {
        int readPermissionCheck = ContextCompat.checkSelfPermission(this,
                PERMISSIONS_STORAGE[0]);
        int writePermissionCheck = ContextCompat.checkSelfPermission(this,
                PERMISSIONS_STORAGE[1]);
        int cameraPermissionCheck = ContextCompat.checkSelfPermission(this,
                PERMISSIONS_STORAGE[2]);
        if (readPermissionCheck != PackageManager.PERMISSION_GRANTED && writePermissionCheck != PackageManager.PERMISSION_GRANTED && cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{PERMISSIONS_STORAGE[0], PERMISSIONS_STORAGE[1], PERMISSIONS_STORAGE[2]},
                    PERMISSIONS_REQUEST);
        } else {
            initFingerprint();
            showDialog();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    initFingerprint();
                    showDialog();
                } else {

                }
                return;
            }
        }
    }

    private void uploadImage(Bitmap bitmap, final String filePath, String name) throws Exception {
        Document document = null;
        try {
            document = new Document(PageSize.A4);
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bytearrayoutputstream);
            byte[] bArray = bytearrayoutputstream.toByteArray();
            Image image = Image.getInstance(bArray);
            image.scaleAbsolute(PageSize.A4.getWidth(), PageSize.A4.getHeight());
            image.setAbsolutePosition(0, 0);
            document.add(image);

            final Item item = new Item();
            item.setName(name + EXTENSION);
            item.setUploadDate(formatDate(System.currentTimeMillis()));
            item.setSize(getFileSize(bArray));
            item.setType("pdf");
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        uploadFile(filePath, item);
                    } catch (Exception e) {
                        showMessage("Upload failed", failureClickListener);
                        e.printStackTrace();
                    }

                    return null;
                }
            }.execute();
        } catch (Exception e) {
            showMessage("Upload failed", failureClickListener);
            throw e;
        } finally {
            document.close();
            bitmap.recycle();
        }
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public int uploadFile(final String selectedFilePath, Item item) throws Exception {

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        FileInputStream fileInputStream = null;

        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");

        try {
            fileInputStream = new FileInputStream(selectedFile);
            URL url = new URL(SERVER_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);//Allow Inputs
            connection.setDoOutput(true);//Allow Outputs
            connection.setUseCaches(false);//Don't use a cached Copy
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("uploaded_file", selectedFilePath);

            dataOutputStream = new DataOutputStream(connection.getOutputStream());

            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                    + selectedFilePath + "\"" + lineEnd);

            dataOutputStream.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            serverResponseCode = connection.getResponseCode();
            if (serverResponseCode == 200) {
                storeDocument(item);
            }
            hideProgressDialog();
            updateList();
            // showMessage("Upload Successful", successClickListener);
        } catch (Exception e) {
            hideProgressDialog();
            showMessage("Upload failed", failureClickListener);
            throw e;
        } finally {
            fileInputStream.close();
            dataOutputStream.flush();
            dataOutputStream.close();
        }

        //dialog.dismiss();
        return serverResponseCode;

    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void showProgressDialog(int text) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(text));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.hide();
                }
            }
        });
    }

    private void showMessage(final String message, final DialogInterface.OnClickListener onClickListener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle(message);
                builder.setPositiveButton(R.string.ok, onClickListener);
                builder.setCancelable(false);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private DialogInterface.OnClickListener successClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        }
    };

    private DialogInterface.OnClickListener failureClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        }
    };

    private void storeDocument(Item item) {
        DocumentDataSource documentDataSource = new DocumentDataSource(this);
        documentDataSource.open();
        documentDataSource.setItem(item);
        documentDataSource.close();
    }

    private String getFileSize(byte[] file) {
        int fileSize = file.length;
        double size = (double) fileSize / (1024 * 1024);
        DecimalFormat df = new DecimalFormat("####0.00");
        return String.valueOf(df.format(size)) + "MB";
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void updateList() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listItem = new ArrayList<>();
                DocumentDataSource documentDataSource = new DocumentDataSource(HomeActivity.this);
                documentDataSource.open();
                listItem = documentDataSource.getAllDocuments();
                documentDataSource.close();

                if (listItem.size() > 0) {
                    mRecyclerView.setAdapter(new DocListAdapter(listItem, HomeActivity.this, new OnDeleteClick() {
                        @Override
                        public void onDelete() {
                            updateList();
                        }
                    }));
                }
            }
        });
    }

    public String formatDate(long time) {
        String dateFormat = "MMMM dd yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return formatter.format(calendar.getTime());
    }

    public void openMediaContent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, ScanConstants.PICKFILE_REQUEST_CODE);
    }

    private Bitmap getBitmap(Uri selectedimg) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        AssetFileDescriptor fileDescriptor = null;
        fileDescriptor =
                getContentResolver().openAssetFileDescriptor(selectedimg, "r");
        Bitmap original
                = BitmapFactory.decodeFileDescriptor(
                fileDescriptor.getFileDescriptor(), null, options);
        return original;
    }
}
