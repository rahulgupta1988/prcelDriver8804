package com.parcelsixd.parcel.driver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.parcelsixd.parcel.driver.db.DBHelper;
import com.parcelsixd.parcel.driver.model.User;
import com.parcelsixd.parcel.driver.parse.AsyncTaskCompleteListener;
import com.parcelsixd.parcel.driver.parse.MultiPartRequester;
import com.parcelsixd.parcel.driver.parse.ParseContent;
import com.parcelsixd.parcel.driver.utills.AndyConstants;
import com.parcelsixd.parcel.driver.utills.AndyUtils;
import com.parcelsixd.parcel.driver.utills.AppLog;
import com.parcelsixd.parcel.driver.utills.PreferenceHelper;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Praveen on 20-Dec-17.
 */

public class ProfileActivity_New extends Activity implements
        View.OnClickListener, AsyncTaskCompleteListener {
    private DBHelper dbHelper;
    private PreferenceHelper preferenceHelper;
    private ParseContent parseContent;
    ImageView edit_ic,update_ic,back_btn;
    de.hdodenhof.circleimageview.CircleImageView ivProfileProfile;
    TextView tvProfileFName,phone_txt,carmake_txt,carmodel_txt,caryear_txt,license_txt,email_txt,address_txt,package_txt;
    private AQuery aQuery;
    private ImageOptions imageOptions;
    private String profileImageData, loginType, filePath = "",
            profileImageFilePath;
    private Uri uri = null;
    private int rotationAngle;
    private Bitmap photoBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);
        parseContent = new ParseContent(this);
        preferenceHelper = new PreferenceHelper(this);
        aQuery = new AQuery(this);
        imageOptions = new ImageOptions();
        imageOptions.memCache = true;
        imageOptions.fileCache = true;
        imageOptions.targetWidth = 200;
        imageOptions.fallback = R.drawable.user;

        initViews();
        setData();
    }

    public void initViews(){
        edit_ic=(ImageView)findViewById(R.id.edit_ic);
        update_ic=(ImageView)findViewById(R.id.update_ic);
        back_btn=(ImageView)findViewById(R.id.back_btn);
        back_btn.setOnClickListener(this);
        edit_ic.setOnClickListener(this);
        update_ic.setOnClickListener(this);
        ivProfileProfile=(de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.ivProfileProfile);
        ivProfileProfile.setOnClickListener(this);

        tvProfileFName=(TextView)findViewById(R.id.tvProfileFName);
        phone_txt=(TextView)findViewById(R.id.phone_txt);
        carmake_txt=(TextView)findViewById(R.id.carmake_txt);
        carmodel_txt=(TextView)findViewById(R.id.carmodel_txt);
        caryear_txt=(TextView)findViewById(R.id.caryear_txt);
        license_txt=(TextView)findViewById(R.id.license_txt);
        email_txt=(TextView)findViewById(R.id.email_txt);
        address_txt=(TextView)findViewById(R.id.address_txt);
        package_txt=(TextView)findViewById(R.id.package_txt);



        disableViews();
    }

    private void setData() {
        dbHelper = new DBHelper(getApplicationContext());
        final User user = dbHelper.getUser();
        if(user!=null){

            aQuery.id(ivProfileProfile).progress(R.id.pBar)
                    .image(user.getPicture(), imageOptions);

            tvProfileFName.setText(user.getFull_name());
            phone_txt.setText(user.getPhone());
            carmake_txt.setText(user.getCar_make());
            carmodel_txt.setText(user.getCar_model());
            caryear_txt.setText(user.getCar_year());
            license_txt.setText(user.getCar_number());
            email_txt.setText(user.getEmail());
            address_txt.setText(user.getAddress());
            package_txt.setText(user.getPackage_type());

Log.i("profile pic23",""+user.getPicture());
            if (user != null && !TextUtils.isEmpty(user.getPicture())) {
                aQuery.id(R.id.ivProfileProfile).image(user.getPicture(), true,
                        true, 200, 0, new BitmapAjaxCallback() {
                            @Override
                            public void callback(String url, ImageView iv,
                                                 Bitmap bm, AjaxStatus status) {
                                AppLog.Log("AQUERY11", "URL FROM AQUERY::" + url);
                                File file = aQuery.getCachedFile(user.getPicture());
                                if (!TextUtils.isEmpty(file.getAbsolutePath())
                                        && file != null) {
                                    profileImageData = file.getAbsolutePath();
                                    AppLog.Log("path11", "URL path FROM AQUERY::" + url);
                                    iv.setImageBitmap(bm);
                                }
                            }
                        });
            }

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;

            case R.id.edit_ic:
                edit_ic.setVisibility(View.GONE);
                update_ic.setVisibility(View.VISIBLE);
                enableViews();
                carmake_txt.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(carmake_txt, InputMethodManager.SHOW_IMPLICIT);
                break;
            case R.id.update_ic:
             update_ic.setVisibility(View.GONE);
                edit_ic.setVisibility(View.VISIBLE);
                onUpdateButtonClick();
                break;

            case R.id.ivProfileProfile:
                showPictureDialog();
                break;
        }
    }

    private void disableViews() {
        tvProfileFName.setEnabled(false);
        phone_txt.setEnabled(false);
        carmake_txt.setEnabled(false);
        carmodel_txt.setEnabled(false);
        caryear_txt.setEnabled(false);
        license_txt.setEnabled(false);
        email_txt.setEnabled(false);
        address_txt.setEnabled(false);
        package_txt.setEnabled(false);
        ivProfileProfile.setEnabled(false);
    }

    private void enableViews() {
       // tvProfileFName.setEnabled(true);
        //phone_txt.setEnabled(true);
        carmake_txt.setEnabled(true);
        carmodel_txt.setEnabled(true);
        caryear_txt.setEnabled(true);
        license_txt.setEnabled(true);
        address_txt.setEnabled(true);
        ivProfileProfile.setEnabled(true);
    }

    private void onUpdateButtonClick() {

       /* if (tvProfileFName.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_empty_fname), this);
            return;
        }

        else if (phone_txt.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_empty_number), this);
            return;
        }*/

         if (carmake_txt.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_empty_maker), this);
            return;
        }

        else if (carmodel_txt.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_empty_model), this);
            return;
        }

        else if (caryear_txt.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_empty_year), this);
            return;
        }

        else if (license_txt.getText().length() == 0) {
            AndyUtils.showToast(
                    getResources().getString(R.string.error_empty_licnumber), this);
            return;
        }

        else {
            updateSocialProfile();
        }
    }

    private void updateSocialProfile() {

        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(
                    getResources().getString(R.string.toast_no_internet), this);
            return;
        }

        AndyUtils.showCustomProgressDialog(this,
                getResources().getString(R.string.progress_update_profile),
                false);

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.UPDATE_PROFILE);
        map.put(AndyConstants.Params.ID, preferenceHelper.getUserId());
        map.put(AndyConstants.Params.TOKEN, preferenceHelper.getSessionToken());

        map.put(AndyConstants.Params.CAR_MAKE_PROF,carmake_txt.getText().toString());
        map.put(AndyConstants.Params.CAR_MODEL_PROF, carmodel_txt.getText().toString());
        map.put(AndyConstants.Params.CAR_YEAR_REG, caryear_txt.getText().toString());
        map.put(AndyConstants.Params.LICENSE_NUM_PROF, license_txt.getText().toString());
        map.put(AndyConstants.Params.PICTURE, profileImageData);
        new MultiPartRequester(this, map,
                AndyConstants.ServiceCode.UPDATE_PROFILE, this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        switch (serviceCode) {
            case AndyConstants.ServiceCode.UPDATE_PROFILE:
                Log.i("pro019",""+response);

                if (!parseContent.isSuccess(response)) {
                    return;
                }
                if (parseContent.isSuccessWithId(response)) {
                    AndyUtils.removeCustomProgressDialog();
                    AndyUtils.showToast(
                            getResources().getString(
                                    R.string.toast_update_profile_success), this);
                    new DBHelper(this).deleteUser();
                    parseContent.parseUserAndStoreToDb(response);
                    onBackPressed();
                }

                break;

            default:
                break;
        }

    }


    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle(getResources().getString(
                R.string.dialog_chhose_photo));
        String[] pictureDialogItems = {
                getResources().getString(R.string.dialog_from_gallery),
                getResources().getString(R.string.dialog_from_camera) };

        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                            case 0:
                                choosePhotoFromGallary();
                                break;

                            case 1:
                                takePhotoFromCamera();
                                break;

                        }
                    }
                });
        pictureDialog.show();
    }


    @SuppressWarnings("deprecation")
    private void choosePhotoFromGallary() {

        // Intent intent = new Intent();
        // intent.setType("image/*");
        // intent.setAction(Intent.ACTION_GET_CONTENT);
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, AndyConstants.CHOOSE_PHOTO);

    }

    String imagepath="";
    String temp_path="";
    @SuppressWarnings("deprecation")
    private void takePhotoFromCamera() {
        Calendar cal = Calendar.getInstance();


        File file= new File(Environment.getExternalStorageDirectory().toString() + "/prcel_driver_saved_images");

        if(!file.exists())
            file.mkdirs();

        temp_path=Environment.getExternalStorageDirectory().toString() + "/prcel_driver_saved_images/" + cal.getTimeInMillis() + ".jpg";

       /* if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        imagepath=file.getAbsolutePath();
       // uri = Uri.fromFile(file);

        File file1= new File(temp_path);
        Uri uri = FileProvider.getUriForFile(this, "com.ondemandbay.taxianytime.driver.fileprovider",
                file1);

        Intent cameraIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(cameraIntent, AndyConstants.TAKE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AndyConstants.CHOOSE_PHOTO) {
            if (data != null) {
                Uri uri = data.getData();

                AppLog.Log("Choose photo", "Choose photo on activity result");

                profileImageFilePath = getRealPathFromURI(uri);

                filePath = profileImageFilePath;
                try {
                    int mobile_width = 480;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filePath, options);
                    int outWidth = options.outWidth;
                    int ratio = (int) ((((float) outWidth) / mobile_width) + 0.5f);

                    if (ratio == 0) {
                        ratio = 1;
                    }
                    ExifInterface exif = new ExifInterface(filePath);

                    String orientString = exif
                            .getAttribute(ExifInterface.TAG_ORIENTATION);
                    int orientation = orientString != null ? Integer
                            .parseInt(orientString)
                            : ExifInterface.ORIENTATION_NORMAL;

                    if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                        rotationAngle = 90;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                        rotationAngle = 180;
                    if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                        rotationAngle = 270;

                    System.out.println("Rotation : " + rotationAngle);

                    options.inJustDecodeBounds = false;
                    options.inSampleSize = ratio;

                    photoBitmap = BitmapFactory.decodeFile(filePath, options);
                    if (photoBitmap != null) {
                        Matrix matrix = new Matrix();
                        matrix.setRotate(rotationAngle,
                                (float) photoBitmap.getWidth() / 2,
                                (float) photoBitmap.getHeight() / 2);
                        photoBitmap = Bitmap.createBitmap(photoBitmap, 0, 0,
                                photoBitmap.getWidth(),
                                photoBitmap.getHeight(), matrix, true);

                        AppLog.Log("Take photo", "Take photo on activity result");
                        String path = MediaStore.Images.Media.insertImage(
                                getContentResolver(), photoBitmap, Calendar
                                        .getInstance().getTimeInMillis()
                                        + ".jpg", null);

                        beginCrop(Uri.parse(path));

                    }
                } catch (OutOfMemoryError e) {
                    System.out.println("out of bound");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            else {
                Toast.makeText(
                        this,
                        getResources().getString(
                                R.string.toast_unable_to_selct_image),
                        Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == AndyConstants.TAKE_PHOTO) {
            File file = new File(temp_path);
            Uri uri1 = FileProvider.getUriForFile(this, "com.ondemandbay.taxianytime.driver.fileprovider",
                    file);

            Log.i("path9801",""+uri1.getPath());
            if (uri1 != null) {

                String imageFilePath = uri1.getPath();
                if (imageFilePath != null && imageFilePath.length() > 0) {
                    // File myFile = new File(imageFilePath);
                    try {
                        // if (bmp != null)
                        // bmp.recycle();
                        int mobile_width = 480;
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        // options.inJustDecodeBounds = true;
                        // BitmapFactory.decodeFile(imageFilePath, options);
                        int outWidth = options.outWidth;
                        int outHeight = options.outHeight;
                        int ratio = (int) ((((float) outWidth) / mobile_width) + 0.5f);

                        if (ratio == 0) {
                            ratio = 1;
                        }
                        ExifInterface exif = new ExifInterface(temp_path);

                        String orientString = exif
                                .getAttribute(ExifInterface.TAG_ORIENTATION);
                        int orientation = orientString != null ? Integer
                                .parseInt(orientString)
                                : ExifInterface.ORIENTATION_NORMAL;
                        System.out.println("Orientation : " + orientation);
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                            rotationAngle = 90;
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                            rotationAngle = 180;
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                            rotationAngle = 270;

                        System.out.println("Rotation : " + rotationAngle);

                        options.inJustDecodeBounds = false;
                        options.inSampleSize = ratio;

                        Bitmap bmp = BitmapFactory.decodeFile(temp_path, options);

                        // bmp = new ImageHelper().decodeFile(myFile);
                        FileOutputStream outStream = new FileOutputStream(
                                file);

                        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                        outStream.flush();
                        outStream.close();

                        Matrix matrix = new Matrix();
                        matrix.setRotate(rotationAngle,
                                (float) bmp.getWidth() / 2,
                                (float) bmp.getHeight() / 2);

                        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                                bmp.getHeight(), matrix, true);

                        // ivStuffPicture.setImageBitmap(bmp);

                        String path = MediaStore.Images.Media.insertImage(
                                getContentResolver(), bmp, Calendar
                                        .getInstance().getTimeInMillis()
                                        + ".jpg", null);
                        beginCrop(Uri.parse(path));

                        // AQuery aQuery = new AQuery(this);
                        // aQuery.id(ivProfile).image(bmp);
                        //
                        profileImageData = imageFilePath;
                        // rlDescription.setVisibility(View.VISIBLE);
                        // llPicture.setVisibility(View.GONE);
                    } catch (OutOfMemoryError e) {
                        System.out.println("out of bound");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Toast.makeText(
                        this,
                        getResources().getString(
                                R.string.toast_unable_to_selct_image),
                        Toast.LENGTH_LONG).show();
            }

        } else if (requestCode == Crop.REQUEST_CROP) {
            AppLog.Log("Crop photo", "Crop photo on activity result");
            handleCrop(resultCode, data);
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null,
                null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            try {
                int idx = cursor
                        .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            } catch (Exception e) {
                AndyUtils
                        .showToast(
                                getResources().getString(
                                        R.string.text_error_get_image), this);
                result = "";
            }
            cursor.close();
        }
        return result;
    }

    private void beginCrop(Uri source) {
        // Uri outputUri = Uri.fromFile(new File(registerActivity.getCacheDir(),
        // "cropped"));
        Uri outputUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), (Calendar.getInstance()
                .getTimeInMillis() + ".jpg")));
        Crop.of(source,outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            AppLog.Log("Handle crop", "Handle crop");
            profileImageData = getRealPathFromURI(Crop.getOutput(result));
            ivProfileProfile.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
