package com.poc.evault.adapters;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Vibrator;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.poc.evault.R;
import com.poc.evault.activities.RegisterEmailActivity;
import com.poc.evault.callbacks.OnDeleteClick;
import com.poc.evault.model.Item;
import com.poc.evault.utils.Constants;
import com.poc.evault.utils.DocumentDataSource;
import com.scanlibrary.ScanConstants;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DASP2 on 4/4/2017.
 */
@TargetApi(Build.VERSION_CODES.M)
public class DocListAdapter extends RecyclerView.Adapter<DocListAdapter.ViewHolder> {
    private List<Item> listItem;
    private Activity context;
    private ProgressDialog mProgressDialog;
    private String PATH = Environment.getExternalStorageDirectory() + "/";
    private OnDeleteClick onDeleteClick;
    private KeyStore keyStore;
    private static final String KEY_NAME = "androidHive";
    private Cipher cipher;
    private Dialog dialog;
    private boolean isAuthenticationRequired = true;
    private Item item;
    private int selectedItemId;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView imgItemIcon;
        public TextView txtItemName;
        public TextView txtItemUploadDate;
        public TextView txtItemSize;
        public ImageView imgMenu;

        public ViewHolder(View view) {
            super(view);
            imgItemIcon = (CircleImageView) view.findViewById(R.id.item_icon);
            txtItemName = (TextView) view.findViewById(R.id.item_name);
            txtItemUploadDate = (TextView) view.findViewById(R.id.item_upload_date);
            txtItemSize = (TextView) view.findViewById(R.id.item_size);
            imgMenu = (ImageView) view.findViewById(R.id.item_menu_icon);
        }
    }

    public DocListAdapter(List<Item> listItem, Activity context, OnDeleteClick onDeleteClick) {
        this.listItem = listItem;
        this.context = context;
        this.onDeleteClick=onDeleteClick;
    }

    @Override
    public DocListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        item = listItem.get(position);
        Resources res = context.getResources();
        final String name = String.format(res.getString(R.string.item_name), item.getName());
        String date = String.format(res.getString(R.string.item_upload_date), item.getUploadDate());
        String size = String.format(res.getString(R.string.item_size), item.getSize());
        int imageId;
        if (item.getType().equalsIgnoreCase("pdf")) {
            imageId = R.mipmap.pdf;
        } else if (item.getType().equalsIgnoreCase("doc")|| item.getType().equalsIgnoreCase("docx")) {
            imageId = R.mipmap.doc;
        } else if (item.getType().equalsIgnoreCase("xls") || item.getType().equalsIgnoreCase("xlsx")) {
            imageId = R.mipmap.excel;
        } else if (item.getType().equalsIgnoreCase("ppt") || item.getType().equalsIgnoreCase("pptx")) {
            imageId = R.mipmap.ppt;
        } else {
            imageId = R.mipmap.ic_launcher;
        }
        //holder.imgItemIcon.setImageBitmap(BitmapFactory.decodeResource(res, imageId));
        holder.imgItemIcon.setImageResource(imageId);
        holder.txtItemName.setText(name);
        holder.txtItemUploadDate.setText(date);
        holder.txtItemSize.setText(size);
        holder.imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(holder.imgMenu, position);
            }
        });
        //bm.recycle();
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public void showPopup(View v, final int position) {
        item=listItem.get(position);
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_item, popup.getMenu());
        popup.show();

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_view:
                        //showProgressDialog(R.string.downloading, context);
                        //new ViewImage(context).execute(listItem.getName(),listItem.getType());
                        selectedItemId=R.id.action_view;
                        initFingerprint();
                        showDialog();
                        break;
                    case R.id.action_download:
                        selectedItemId=R.id.action_download;
                        initFingerprint();
                        showDialog();
                        //showProgressDialog(R.string.downloading, context);
                        //new DownloadImage(context).execute(listItem.getName(),listItem.getType());
                        break;
                    case R.id.action_delete:
                        selectedItemId=R.id.action_delete;
                        initFingerprint();
                        showDialog();
                        /*showProgressDialog(R.string.deleting, context);
                        DocumentDataSource documentDataSource = new DocumentDataSource(context);
                        documentDataSource.open();
                        documentDataSource.deleteItem(listItem.getId());
                        documentDataSource.close();
                        hideProgressDialog(context);
                        onDeleteClick.onDelete();*/
                        break;
                    default:
                        return false;
                }
                return false;
            }
        });
    }

    public class ViewImage extends AsyncTask<String, Integer, Boolean> {
        Activity context;

        public ViewImage(Activity context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            // This is done in a background thread
            return viewImage(arg0[0], context,arg0[1]);
        }

        /**
         * Called after the image has been downloaded
         * -> this calls a function on the main thread again
         */
        protected void onPostExecute(Drawable image) {

        }


        /**
         * Actually download the Image from the _url
         *
         * @param fileName
         * @return
         */
        private boolean viewImage(String fileName, Activity context, String type) {
            //Prepare to download image
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            try {
                String _url = Constants.SERVER_BASE_URL+"uploads/" + fileName.replace(" ", "%20");
                url = new URL(_url);
                in = url.openStream();

                try {
                    File file = new File(PATH, fileName);
                    OutputStream output = new FileOutputStream(file);
                    try {
                        try {
                            byte[] buffer = new byte[4 * 1024]; // or other buffer size
                            int read;

                            while ((read = in.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }
                            output.flush();
                        } finally {
                            output.close();
                        }
                    } catch (Exception e) {
                        hideProgressDialog(context);
                        e.printStackTrace(); // handle exception, define IOException and others
                    }
                } finally {
                    in.close();
                }
                hideProgressDialog(context);
                openFile(context, fileName, type);
                return true;

            } catch (Exception e) {
                hideProgressDialog(context);
                Log.e("Error reading file", e.toString());
                return false;
            }

        }

    }

    public class DownloadImage extends AsyncTask<String, Integer, Boolean> {
        Activity context;

        public DownloadImage(Activity context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            // This is done in a background thread
            return downloadImage(arg0[0], context);
        }

        /**
         * Called after the image has been downloaded
         * -> this calls a function on the main thread again
         */
        protected void onPostExecute(Drawable image) {

        }


        /**
         * Actually download the Image from the _url
         *
         * @param fileName
         * @return
         */
        private boolean downloadImage(String fileName, Activity context) {
            //Prepare to download image
            URL url;
            BufferedOutputStream out;
            InputStream in;
            BufferedInputStream buf;

            try {
                String _url = "http://49.12.33.176/TDLockerServer/uploads/" + fileName;
                url = new URL(_url);
                in = url.openStream();

                try {
                    File file = new File(PATH, fileName);
                    OutputStream output = new FileOutputStream(file);
                    try {
                        try {
                            byte[] buffer = new byte[4 * 1024]; // or other buffer size
                            int read;

                            while ((read = in.read(buffer)) != -1) {
                                output.write(buffer, 0, read);
                            }
                            output.flush();
                        } finally {
                            output.close();
                        }
                    } catch (Exception e) {
                        hideProgressDialog(context);
                        e.printStackTrace(); // handle exception, define IOException and others
                    }
                } finally {
                    in.close();
                }
                hideProgressDialog(context);
                return true;

            } catch (Exception e) {
                hideProgressDialog(context);
                Log.e("Error reading file", e.toString());
                return false;
            }

        }

    }

    private void showProgressDialog(final int text, final Activity context) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog == null) {
                    mProgressDialog = new ProgressDialog(context);
                    mProgressDialog.setMessage(context.getString(text));
                    mProgressDialog.setIndeterminate(true);
                }

                mProgressDialog.show();
            }
        });
    }

    private void hideProgressDialog(Activity context) {

        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.hide();
                }
            }
        });
    }

    private void openFile(Activity context, String fileName, String type) {
        File file = new File(PATH, fileName);
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri data = Uri.fromFile(file);
        //String type = type;
        intent.setDataAndType(data, getType(type));
        context.startActivity(intent);
    }

    private String getType(String type){
        String mimeType="*/*";
        if (type.contains("doc") || type.contains("docx")) {
            mimeType="application/msword";
        } else if (type.contains("pdf")) {
            mimeType="application/pdf";
        } else if (type.contains("ppt") || type.contains("pptx")) {
            mimeType="application/vnd.ms-powerpoint";
        } else if (type.contains("xls") || type.contains("xlsx")) {
            mimeType="application/vnd.ms-excel";
        } else if (type.contains("zip") || type.contains("rar")) {
            mimeType="application/x-wav";
        } else if (type.contains("rtf")) {
            mimeType="application/rtf";
        } else if (type.contains("wav") || type.contains("mp3")) {
            mimeType="audio/x-wav";
        } else if (type.contains("gif")) {
            mimeType="image/gif";
        } else if (type.contains("jpg") || type.contains("jpeg") || type.contains("png")) {
            mimeType="image/jpeg";
        } else if (type.contains("txt")) {
            mimeType="text/plain";
        } else if (type.contains("3gp") || type.contains("mpg") ||
                type.contains("mpeg") || type.contains("mpe") || type.contains("mp4") || type.contains("avi")) {
            mimeType="video/*";
        }

        return mimeType;
    }

    public void showDialog() {
        dialog = new Dialog(context);
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
        KeyguardManager keyguardManager = (KeyguardManager)context.getSystemService(context.KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(context.FINGERPRINT_SERVICE);

        if (!fingerprintManager.isHardwareDetected()) {
            Toast.makeText(context, "Your Device does not have a Fingerprint Sensor", Toast.LENGTH_LONG).show();
        } else {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Fingerprint authentication permission not enabled", Toast.LENGTH_LONG).show();
            } else {
                if (!fingerprintManager.hasEnrolledFingerprints()) {
                    Toast.makeText(context, "Register at least one fingerprint in Settings", Toast.LENGTH_LONG).show();
                } else {
                    if (!keyguardManager.isKeyguardSecure()) {
                        Toast.makeText(context, "Lock screen security not enabled in Settings", Toast.LENGTH_LONG).show();
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

                switch(selectedItemId){
                    case R.id.action_view:
                        showProgressDialog(R.string.downloading, context);
                        new ViewImage(context).execute(item.getName(),item.getType());
                        break;
                    case R.id.action_download:
                        showProgressDialog(R.string.downloading, context);
                        new DownloadImage(context).execute(item.getName(), item.getType());
                        break;
                    case R.id.action_delete:
                        showProgressDialog(R.string.deleting, context);
                        DocumentDataSource documentDataSource = new DocumentDataSource(context);
                        documentDataSource.open();
                        documentDataSource.deleteItem(item.getId());
                        documentDataSource.close();
                        hideProgressDialog(context);
                        onDeleteClick.onDelete();
                        break;
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
            cipher.init(Cipher.DECRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private void startVibration() {
        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(500);
    }

    private void startShakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.animation_shake);
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
        Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show();
    }

}

