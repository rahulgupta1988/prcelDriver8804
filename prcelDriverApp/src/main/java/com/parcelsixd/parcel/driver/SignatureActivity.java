package com.parcelsixd.parcel.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parcelsixd.parcel.driver.model.RequestDetail;
import com.parcelsixd.parcel.driver.parse.AsyncTaskCompleteListener;
import com.parcelsixd.parcel.driver.parse.MultiPartRequester;
import com.parcelsixd.parcel.driver.parse.ParseContent;
import com.parcelsixd.parcel.driver.utills.AndyConstants;
import com.parcelsixd.parcel.driver.utills.AndyUtils;
import com.parcelsixd.parcel.driver.utills.AppLog;
import com.parcelsixd.parcel.driver.utills.PreferenceHelper;
import com.parcelsixd.parcel.driver.widget.MyFontButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

/**
 * Created by Praveen on 19-Jan-18.
 */

public class SignatureActivity  extends Activity implements
        View.OnClickListener, AsyncTaskCompleteListener{

    Context mContext;
    private MyFontButton tvsubmit;
    ImageView sig_ic;
    private RequestDetail requestDetail;

    boolean moveotnot=false;

    // signature views
    Button mClear, mGetSign;
    File file;
    LinearLayout mContent;
    View view;
    signature mSignature;
    Bitmap bitmap;
    String requestID="";
    String signature_base64="";
    private ParseContent parseContent;
    protected PreferenceHelper preferenceHelper;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signature_activity);
        mContext=this;
        preferenceHelper = new PreferenceHelper(this);
        Intent intent=getIntent();
        if(intent!=null)
            requestDetail= (RequestDetail) intent.getSerializableExtra("requestDetail");
        parseContent = new ParseContent(this);
        initViews();

    }


    public void initViews(){
        tvsubmit=(MyFontButton)findViewById(R.id.tvsubmit);
        tvsubmit.setOnClickListener(this);
        sig_ic=(ImageView)findViewById(R.id.sig_ic);
        getSignature();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.clear:
                moveotnot=false;
                mSignature.clear();
                mGetSign.setEnabled(false);
                break;

            case R.id.tvsubmit:
                view.setDrawingCacheEnabled(true);
                mSignature.save(mContent);

                requestID=requestDetail.getRequest_id();
                if(requestID.length()>0 && signature_base64.length()>0) {
                    if (moveotnot)
                        sendSignature();
                    else
                        Toast.makeText(mContext,"Please write your signature.",Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        AndyUtils.removeCustomProgressDialog();
        AppLog.Log("signature rsponce", response);
        switch (serviceCode) {
            case AndyConstants.ServiceCode.SIGNATURE:
                if (!parseContent.isSuccess(response)) {
                return;
            }
                preferenceHelper.putRequestId(-1);
                Toast.makeText(getApplicationContext(), "Successfully Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mContext, MapActivity.class));
                finish();
        }


    }


    public void getSignature(){
        mContent = (LinearLayout) findViewById(R.id.canvasLayout);
        mSignature = new signature(getApplicationContext(), null);
        mSignature.setBackgroundColor(Color.WHITE);
        // Dynamically generating Layout through java code
        mContent.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mClear = (Button) findViewById(R.id.clear);
        mGetSign = (Button) findViewById(R.id.getsign);
        mGetSign.setEnabled(false);
        view = mContent;
        mGetSign.setOnClickListener(this);
        mClear.setOnClickListener(this);


    }

    public class signature extends View {

        private static final float STROKE_WIDTH = 7f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void save(View v) {
            Log.v("log_tag", "Width: " + v.getWidth());
            Log.v("log_tag", "Height: " + v.getHeight());

            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(mContent.getWidth(), mContent.getHeight(), Bitmap.Config.RGB_565);
            }
            Canvas canvas = new Canvas(bitmap);
            v.draw(canvas);
                sig_ic.setImageBitmap(bitmap);
            getStringImage(bitmap);
        }

        public void getStringImage(Bitmap finalBitmap) {
            if (finalBitmap != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                finalBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
                byte[] imageBytes = baos.toByteArray();
                signature_base64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            }

        }

        public void clear() {
            path.reset();
            invalidate();
            mGetSign.setEnabled(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            mGetSign.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    moveotnot=true;

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void debug(String string) {

            Log.v("log_tag", string);

        }

        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }


    private void sendSignature() {

        if (!AndyUtils.isNetworkAvailable(this)) {
            AndyUtils.showToast(
                    getResources().getString(R.string.toast_no_internet), this);
            return;
        }

        AndyUtils.showCustomProgressDialog(this,
                getResources().getString(R.string.progress_update_profile),
                false);


        HashMap<String, String> map = new HashMap<String, String>();
        map.put(AndyConstants.URL, AndyConstants.ServiceType.SIGNATURE);
        map.put("request_id",requestID);
        map.put("signature",signature_base64);

        new MultiPartRequester(this, map,
                AndyConstants.ServiceCode.SIGNATURE, this);
    }

}
