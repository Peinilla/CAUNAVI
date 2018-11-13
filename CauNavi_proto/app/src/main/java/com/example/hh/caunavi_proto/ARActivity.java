package com.example.hh.caunavi_proto;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Camera;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Point;
import com.google.ar.core.Point.OrientationMode;
import com.google.ar.core.PointCloud;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;

import com.example.hh.caunavi_proto.common.helpers.CameraPermissionHelper;
import com.example.hh.caunavi_proto.common.helpers.DisplayRotationHelper;
import com.example.hh.caunavi_proto.common.helpers.FullScreenHelper;
import com.example.hh.caunavi_proto.common.helpers.SnackbarHelper;
import com.example.hh.caunavi_proto.common.helpers.TapHelper;
import com.example.hh.caunavi_proto.common.rendering.BackgroundRenderer;
import com.example.hh.caunavi_proto.common.rendering.ObjectRenderer;
import com.example.hh.caunavi_proto.common.rendering.ObjectRenderer.BlendMode;
import com.example.hh.caunavi_proto.common.rendering.PlaneRenderer;
import com.example.hh.caunavi_proto.common.rendering.PointCloudRenderer;

import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * This is a simple example that shows how to create an augmented reality (AR) application using the
 * ARCore API. The application will display any detected planes and will allow the user to tap on a
 * plane to place a 3d model of the Android robot.
 */
public class ARActivity extends AppCompatActivity implements GLSurfaceView.Renderer {
    private static final String TAG = ARActivity.class.getSimpleName();

    // Rendering. The Renderers are created here, and initialized when the GL surface is created.
    private GLSurfaceView surfaceView;
    private GpsManager gps;
    private MapManager mapManager;
    // 지도 경로

    private boolean installRequested;

    private Session session;
    private final SnackbarHelper messageSnackbarHelper = new SnackbarHelper();
    private DisplayRotationHelper displayRotationHelper;
    private TapHelper tapHelper;

    private final BackgroundRenderer backgroundRenderer = new BackgroundRenderer();
    private final ObjectRenderer virtualObject = new ObjectRenderer();
    private final ObjectRenderer virtualObjectShadow = new ObjectRenderer();
    private final PlaneRenderer planeRenderer = new PlaneRenderer();
    private final PointCloudRenderer pointCloudRenderer = new PointCloudRenderer();

    // Temporary matrix allocated here to reduce number of allocations for each frame.
    private final float[] anchorMatrix = new float[16];
    private static final float[] DEFAULT_COLOR = new float[] {0f, 0f, 0f, 0f};

    int count = 0;

    private ArrayList<float[]> testList = new ArrayList<>();
    private ArrayList<Float> rotateList = new ArrayList<>();

    float[] projmtx = new float[16];
    float[] viewmtx = new float[16];

    private float[] accelValue;
    private float[] magneticValue;

    private float headingAngle;
    private float pitchAngle;
    private float rollAngle;

    private float modelAngle;

    private TextView angleText;
    private TextView destView;

    private Timer timer;
    private TimerTask timerTask;

    private ListView listView;

    private int destinationID;
    private boolean isDestinationSet;

    // Anchors created from taps used for object placing with a given color.
    private static class ColoredAnchor {
        public final Anchor anchor;
        public final float[] color;

        public ColoredAnchor(Anchor a, float[] color4f) {
            this.anchor = a;
            this.color = color4f;
        }
    }

    private final ArrayList<ColoredAnchor> anchors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        surfaceView = findViewById(R.id.preview);
        displayRotationHelper = new DisplayRotationHelper(/*context=*/ this);

        setMenuView();
        isDestinationSet = false;

        Intent i = new Intent();
        i = getIntent();
        destinationID = i.getIntExtra("Build_id",0);
        Log.i("destinationID", Integer.toString(destinationID));

        // Set up tap listener.
        tapHelper = new TapHelper(/*context=*/ this);
        surfaceView.setOnTouchListener(tapHelper);

        // Set up renderer.
        surfaceView.setPreserveEGLContextOnPause(true);
        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0); // Alpha used for plane blending.
        surfaceView.setRenderer(this);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        installRequested = false;

        angleText = (TextView)findViewById(R.id.headangle);
        destView = (TextView)findViewById(R.id.gpsText);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        SensorEventListener mListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] v = sensorEvent.values;

                switch (sensorEvent.sensor.getType()){
                    case Sensor.TYPE_ACCELEROMETER:
                        accelValue = v;
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        magneticValue = v;
                        break;

                }

                if(accelValue != null && magneticValue != null){
                    float[] values = new float[3];
                    float[] mr = new float[9];

                    SensorManager.getRotationMatrix(mr, null, accelValue, magneticValue);
                    SensorManager.getOrientation(mr, values);
                    headingAngle = (float) Math.toDegrees(values[0]);
                    pitchAngle = (float) Math.toDegrees(values[1]);
                    rollAngle = (float) Math.toDegrees(values[2]);


                    //angleText.setText((String.valueOf(headingAngle)));
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

        sensorManager.registerListener(mListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);

        sensorManager.registerListener(mListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                sensorManager.SENSOR_DELAY_UI);


        gps = new GpsManager(this);

        mapManager = new MapManager(this);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(gps.isGetLocation) {
                            if(!isDestinationSet){
                                    setDest();
                                isDestinationSet = true;
                            }
                            angleText.setText("현재위치 : " + mapManager.getNearPoint());
                            if(!mapManager.isArrivalDest()) {
                                setArrow(mapManager.getNextBearingTest(gps.lat, gps.lon));
                            }else{
                                end();
                            }
                        }else{
                            angleText.setText("GPS 정보가 없습니다.");
                        }
                    }
                });
            }
        };

        Intent intent = new Intent(getApplicationContext(), NaviPopupActivity.class);
        startActivityForResult(intent, 1);

        timer = new Timer();
        timer.schedule(timerTask,5000,3000);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (session == null) {
            Exception exception = null;
            String message = null;
            try {
                switch (ArCoreApk.getInstance().requestInstall(this, !installRequested)) {
                    case INSTALL_REQUESTED:
                        installRequested = true;
                        return;
                    case INSTALLED:
                        break;
                }

                // ARCore requires camera permissions to operate. If we did not yet obtain runtime
                // permission on Android M and above, now is a good time to ask the user for it.
                if (!CameraPermissionHelper.hasCameraPermission(this)) {
                    CameraPermissionHelper.requestCameraPermission(this);
                    return;
                }
                if (!CameraPermissionHelper.hasFindLocationPermission(this)) {
                    CameraPermissionHelper.requestFindLocationPermission(this);
                    return;
                }
                if (!CameraPermissionHelper.hasCoarseLocationPermission(this)) {
                    CameraPermissionHelper.requestCoarseLocationPermission(this);
                    return;
                }

                // Create the session.
                session = new Session(/* context= */ this);

            } catch (UnavailableArcoreNotInstalledException
                    | UnavailableUserDeclinedInstallationException e) {
                message = "Please install ARCore";
                exception = e;
            } catch (UnavailableApkTooOldException e) {
                message = "Please update ARCore";
                exception = e;
            } catch (UnavailableSdkTooOldException e) {
                message = "Please update this app";
                exception = e;
            } catch (UnavailableDeviceNotCompatibleException e) {
                message = "This device does not support AR";
                exception = e;
            } catch (Exception e) {
                message = "Failed to create AR session";
                exception = e;
            }


            if (message != null) {
                messageSnackbarHelper.showError(this, message);
                Log.e(TAG, "Exception creating session", exception);
                return;
            }
        }

        // Note that order matters - see the note in onPause(), the reverse applies here.
        try {
            session.resume();
        } catch (CameraNotAvailableException e) {
            // In some cases (such as another camera app launching) the camera may be given to
            // a different app instead. Handle this properly by showing a message and recreate the
            // session at the next iteration.
            messageSnackbarHelper.showError(this, "Camera not available. Please restart the app.");
            session = null;
            return;
        }

        surfaceView.onResume();
        displayRotationHelper.onResume();

        //messageSnackbarHelper.showMessage(this, "Searching for surfaces...");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (session != null) {
            // Note that the order matters - GLSurfaceView is paused first so that it does not try
            // to query the session. If Session is paused before GLSurfaceView, GLSurfaceView may
            // still call session.update() and get a SessionPausedException.
            displayRotationHelper.onPause();
            surfaceView.onPause();
            session.pause();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                    .show();
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this);
            }
            finish();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenHelper.setFullScreenOnWindowFocusChanged(this, hasFocus);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Prepare the rendering objects. This involves reading shaders, so may throw an IOException.
        try {
            // Create the texture and pass it to ARCore session to be filled during update().
            backgroundRenderer.createOnGlThread(/*context=*/ this);
            planeRenderer.createOnGlThread(/*context=*/ this, "models/trigrid.png");
            pointCloudRenderer.createOnGlThread(/*context=*/ this);

            virtualObject.createOnGlThread(/*context=*/ this, "models/arrow.obj", "models/arrow4.png");
            virtualObject.setMaterialProperties(0.0f, 2.0f, 0.5f, 6.0f);

            virtualObjectShadow.createOnGlThread(
                    /*context=*/ this, "models/andy_shadow.obj", "models/andy_shadow.png");
            virtualObjectShadow.setBlendMode(BlendMode.Shadow);
            virtualObjectShadow.setMaterialProperties(1.0f, 0.0f, 0.0f, 1.0f);

        } catch (IOException e) {
            Log.e(TAG, "Failed to read an asset file", e);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        displayRotationHelper.onSurfaceChanged(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear screen to notify driver it should not load any pixels from previous frame.
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (session == null) {
            return;
        }
        // Notify ARCore session that the view size changed so that the perspective matrix and
        // the video background can be properly adjusted.
        displayRotationHelper.updateSessionIfNeeded(session);

        try {
            session.setCameraTextureName(backgroundRenderer.getTextureId());

            // Obtain the current frame from ARSession. When the configuration is set to
            // UpdateMode.BLOCKING (it is by default), this will throttle the rendering to the
            // camera framerate.
            Frame frame = session.update();
            Camera camera = frame.getCamera();

            // Handle one tap per frame.
            handleTap(frame, camera);

            // Draw background.
            backgroundRenderer.draw(frame);

            // If not tracking, don't draw 3d objects.
            if (camera.getTrackingState() == TrackingState.PAUSED) {
                return;
            }

            // Get projection matrix.
            camera.getProjectionMatrix(projmtx, 0, 0.1f, 100.0f);

            // Get camera matrix and draw.
            camera.getViewMatrix(viewmtx, 0);

            // projmxt, viewmtx 확인용 로그
            /*
            if(count%15 == 0) {
                String str1 = "\n\nproj : ";
                for (int inx = 0; inx < projmtx.length; inx++) {
                    str1 += projmtx[inx] + " / ";
                }
                Log.i("test", str1);
                String str2 = "view : ";
                for (int inx = 0; inx < viewmtx.length; inx++) {
                    str2 += viewmtx[inx] + " / ";
                }
                Log.i("test", str2);
            }
            count++;
            */


            // Compute lighting from average intensity of the image.
            // The first three components are color scaling factors.
            // The last one is the average pixel intensity in gamma space.
            final float[] colorCorrectionRgba = new float[4];
            frame.getLightEstimate().getColorCorrection(colorCorrectionRgba, 0);

            // Visualize tracked points.
            PointCloud pointCloud = frame.acquirePointCloud();
            pointCloudRenderer.update(pointCloud);
            //pointCloudRenderer.draw(viewmtx, projmtx);

            // Application is responsible for releasing the point cloud resources after
            // using it.
            pointCloud.release();

            /* / Check if we detected at least one plane. If so, hide the loading message.
            if (messageSnackbarHelper.isShowing()) {
                for (Plane plane : session.getAllTrackables(Plane.class)) {
                    if (plane.getTrackingState() == TrackingState.TRACKING) {
                        messageSnackbarHelper.hide(this);
                        break;
                    }
                }
            } */

            SensorManager sensorManager;
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


            // Visualize planes.
            //planeRenderer.drawPlanes(session.getAllTrackables(Plane.class), camera.getDisplayOrientedPose(), projmtx);
            //

            // Visualize anchors created by touch.
            float scaleFactor = 1.0f;
            for (ColoredAnchor coloredAnchor : anchors) {
                if (coloredAnchor.anchor.getTrackingState() != TrackingState.TRACKING) {
                    continue;
                }
                // Get the current pose of an Anchor in world space. The Anchor pose is updated
                // during calls to session.update() as ARCore refines its estimate of the world.
                coloredAnchor.anchor.getPose().toMatrix(anchorMatrix, 0);

                // Update and draw the model and its shadow.
                //virtualObject.updateModelMatrix(anchorMatrix, scaleFactor,modelAngle);
                //virtualObjectShadow.updateModelMatrix(anchorMatrix, scaleFactor);
                //virtualObject.draw(viewmtx, projmtx, colorCorrectionRgba, coloredAnchor.color);
                //virtualObjectShadow.draw(viewmtx, projmtx, colorCorrectionRgba, coloredAnchor.color);
            }

            for(int inx = 0; inx < testList.size(); inx ++) {
                virtualObject.updateModelMatrix(testList.get(inx), scaleFactor,modelAngle,headingAngle);
                virtualObject.draw(viewmtx, projmtx, colorCorrectionRgba, new float[]{66.0f, 133.0f, 244.0f, 180.0f});
            }
            modelAngle++;

        } catch (Throwable t) {
            // Avoid crashing the application due to unhandled exceptions.
            Log.e(TAG, "Exception on the OpenGL thread", t);
        }
    }

    // Handle only one tap per frame, as taps are usually low frequency compared to frame rate.
    private void handleTap(Frame frame, Camera camera) {
        MotionEvent tap = tapHelper.poll();
        if (tap != null && camera.getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                // Check if any plane was hit, and if it was hit inside the plane polygon
                Trackable trackable = hit.getTrackable();
                // Creates an anchor if a plane or an oriented point was hit.
                if ((trackable instanceof Plane
                        && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())
                        && (PlaneRenderer.calculateDistanceToPlane(hit.getHitPose(), camera.getPose()) > 0))
                        || (trackable instanceof Point
                        && ((Point) trackable).getOrientationMode()
                        == OrientationMode.ESTIMATED_SURFACE_NORMAL)) {
                    // Hits are sorted by depth. Consider only closest hit on a plane or oriented point.
                    // Cap the number of objects created. This avoids overloading both the
                    // rendering system and ARCore.
                    if (anchors.size() >= 20) {
                        anchors.get(0).anchor.detach();
                        anchors.remove(0);
                    }

                    // Assign a color to the object for rendering based on the trackable type
                    // this anchor attached to. For AR_TRACKABLE_POINT, it's blue color, and
                    // for AR_TRACKABLE_PLANE, it's green color.
                    float[] objColor;
                    if (trackable instanceof Point) {
                        objColor = new float[] {66.0f, 133.0f, 244.0f, 255.0f};
                    } else if (trackable instanceof Plane) {
                        objColor = new float[] {139.0f, 195.0f, 74.0f, 255.0f};
                    } else {
                        objColor = DEFAULT_COLOR;
                    }

                    // Adding an Anchor tells ARCore that it should track this position in
                    // space. This anchor is created on the Plane to place the 3D model
                    // in the correct position relative both to the world and to the plane.
                    anchors.add(new ColoredAnchor(hit.createAnchor(), objColor));
                    break;
                }
            }
        }
    }

    public void setArrow(float destinationAngle){
        float[] tMatrix = new float[16];
        float[] rMatrix = new float[16];
        float[] tempM = new float[16];

        AngleAdjustment(destinationAngle);

        Matrix.setIdentityM(tMatrix, 0);
        Matrix.translateM(tMatrix, 0, 0f, 0.2f, -3.0f);

        Matrix.setIdentityM(rMatrix,0);
        //Matrix.translateM(tMatrix, 0, (float)Math.sin(270 + headingAngle), -0.2f, -(float)Math.cos(270 + headingAngle));

        Matrix.setRotateM(rMatrix,0,headingAngle - destinationAngle,0f,1f,0f);
        Matrix.multiplyMM(tMatrix,0,tMatrix,0,rMatrix,0);

        Matrix.setRotateM(rMatrix, 0, pitchAngle, -1.0f, 0.0f,  0.0f);
        Matrix.multiplyMM(tMatrix, 0, tMatrix, 0, rMatrix, 0);


        Matrix.setIdentityM(tempM, 0);
        Matrix.invertM(tempM,0,viewmtx,0);
        Matrix.multiplyMM(tempM,0,tempM,0,tMatrix, 0);

        if(testList.size() <= 10) {
            testList.add(tempM);
        }else{
            testList.remove(0);
            testList.add(tempM);
        }
    }
// TODO : 방향 조정 다 끝나면 여기로
    public void AngleAdjustment(float destinationAngle) {

        if (rollAngle < -90 || rollAngle > 90){ // 사용자가 핸드폰을 들고 하늘을 바라볼때
            if (headingAngle < 0 ) {
                headingAngle += 180;
            }
            else {
                headingAngle -= 180;
            }
            pitchAngle = pitchAngle*(-1) - 90;

            if (Math.abs(headingAngle)<90)
                pitchAngle *= -1;

        }
        else{// 사용자가 핸드폰을 들고 땅을 바라볼때
            headingAngle += 360;
            pitchAngle += 90;

            if (headingAngle > 270 || headingAngle <90)
                pitchAngle *= -1;
        }
    }

    public void drawerOpen(View v){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.Menu);
        if(!drawer.isDrawerOpen(Gravity.LEFT)){
            drawer.openDrawer(Gravity.LEFT) ;
        }

    }
// 어뎁터뷰
    public void setMenuView(){
        final String[] items = {"길찾기", "menu2", "menu3"};
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTextColor(Color.BLACK);
                return view;
            }
        };
        listView = (ListView) findViewById(R.id.MenuList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                switch (position) {
                    case 0: // menu1
                        Intent intent = new Intent(getApplicationContext(), NaviPopupActivity.class);
                        startActivityForResult(intent, 1);
                        break;
                    case 1: // menu2
                        break;
                    case 2: // menu3
                        break;
                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.Menu);
                drawer.closeDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1){
            if(resultCode==RESULT_OK){
                //데이터 받기
                destinationID = data.getIntExtra("result",0);
                if(destinationID != 0){
                    setDest();
                    isDestinationSet = true;
                }
            }
        }
    }

    public void setDest(){
        mapManager.setDestination(destinationID,gps.lat,gps.lon);
        Toast.makeText(this, ""+destinationID+"관으로", Toast.LENGTH_SHORT).show();
        destView.setText("목적지 : " + destinationID+"관");
        testList.clear();
    }

    public void end(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("목적지에 도착했습니다");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.show();
    }

    @Override
    public void finish() {
        timer.cancel();
        super.finish();
    }
}