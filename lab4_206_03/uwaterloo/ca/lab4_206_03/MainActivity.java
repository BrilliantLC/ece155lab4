package lab4_206_03.uwaterloo.ca.lab4_206_03;

import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import mapper.MapView;
import mapper.NavigationalMap;
import mapper.MapLoader;
import mapper.PositionListener;


public class MainActivity extends AppCompatActivity implements PositionListener {
    TextView output, direction;
    Button resetstp;
    int step;
    SensorEventListeners listeners;
    MapView mapView;
    PositionListener pl;
    NavigationalMap map;
    LinearLayout l;
    public PointF origin, destination;
    int i = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //add the linear layout
        l = (LinearLayout) findViewById(R.id.linearLayout);
        l.setOrientation(LinearLayout.VERTICAL);

        //pedometer output display
        output = new TextView(getApplicationContext());
        output.setTextColor(Color.BLACK);
        l.addView(output);
        direction = new TextView(getApplicationContext());
        direction.setTextColor(Color.BLACK);
        l.addView(direction);


        //map
        pl = new MainActivity();
        mapView = new MapView(getApplicationContext(), 1000, 1000, 40, 40);
        mapView.addListener(pl);
        registerForContextMenu(mapView);
        map = MapLoader.loadMap(Environment.getExternalStorageDirectory(), "E2-3344.svg");
        mapView.setMap(map);
        l.addView(mapView);


        //reset step count button
        resetstp = new Button(getApplicationContext());
        resetstp.setText("RESET STEPS");
        resetstp.setGravity(Gravity.CENTER_HORIZONTAL);
        l.addView(
                resetstp,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)
        );

        //request the sensor manager and get the accelerometer
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Sensor accelerometerO = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        //instantiate its sensor listener
//        registerForContextMenu(mapView);
        listeners = new SensorEventListeners(output, direction, mapView, step, map);
        sensorManager.registerListener(listeners, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listeners, magnetic, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listeners, accelerometerO, SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(listeners, rotation, SensorManager.SENSOR_DELAY_NORMAL);
        //on-click listeners for both buttons
        resetstp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listeners.resetit();
            }
        });


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        mapView.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (i == 0) {
            origin = mapView.getOriginPoint();
            i = 1;
        } else {
            destination = mapView.getDestinationPoint();
            i = 0;
        }
        return super.onContextItemSelected(item) || mapView.onContextItemSelected(item);
    }

    @Override
    public void originChanged(MapView source, PointF loc) {
        source.setUserPoint(source.getOriginPoint());
    }

    public void destinationChanged(MapView source, PointF loc) {
    }


    class SensorEventListeners implements SensorEventListener {
        TextView output, direction;
        float z;
        int counter = 0;
        int prev = 0;
        int state = 2;
        float[] mag, acc, rot;
        double orientation;
        double prevor = orientation;
        int nscounter, wecounter = 0;
        PointF a = new PointF(5.4f, 18.5f);
        PointF b = new PointF(12f, 18.5f);
        PointF c = new PointF(19f, 18.5f);
        List<PointF> path = new ArrayList<>();
        NavigationalMap map;
        MapView mapView;
        String notification = "";
        PointF pos;

        //constructor
        public SensorEventListeners(TextView outputView, TextView dirView, MapView mv, int stp, NavigationalMap nvgMap) {
            output = outputView;
            direction = dirView;
            mapView = mv;
            counter = stp;
            map = nvgMap;
        }

        //method for resetting the counter for the "resetstp" button
        public void resetit() {
            counter = 0;
            nscounter = 0;
            wecounter = 0;
        }

        public void onAccuracyChanged(Sensor s, int i) {
        }

        public void pathDrawer() {
            destination = mapView.getDestinationPoint();
            path.clear();
            path.add(mapView.getOriginPoint());
            pos = mapView.getOriginPoint();
            if (map.calculateIntersections(mapView.getOriginPoint(), mapView.getDestinationPoint()).size() == 0) {
                path.add(destination);
                mapView.setUserPath(path);
            } else if (map.calculateIntersections(mapView.getOriginPoint(), a).size() == 0) {
                path.add(a);
                if (map.calculateIntersections(a, destination).size() == 0) {
                    path.add(destination);
                    mapView.setUserPath(path);
                } else if (map.calculateIntersections(b, destination).size() == 0) {
                    path.add(b);
                    path.add(destination);
                    mapView.setUserPath(path);
                } else if (map.calculateIntersections(c, destination).size() == 0) {
                    path.add(c);
                    path.add(destination);
                    mapView.setUserPath(path);
                }
            } else if (map.calculateIntersections(mapView.getOriginPoint(), b).size() == 0) {
                path.add(b);
                if (map.calculateIntersections(a, destination).size() == 0) {
                    path.add(a);
                    path.add(destination);
                    mapView.setUserPath(path);
                } else if (map.calculateIntersections(b, destination).size() == 0) {
                    path.add(destination);
                    mapView.setUserPath(path);
                } else if (map.calculateIntersections(c, destination).size() == 0) {
                    path.add(c);
                    path.add(destination);
                    mapView.setUserPath(path);
                }
            } else if (map.calculateIntersections(mapView.getOriginPoint(), c).size() == 0) {
                path.add(c);
                if (map.calculateIntersections(a, destination).size() == 0) {
                    path.add(a);
                    path.add(destination);
                    mapView.setUserPath(path);
                } else if (map.calculateIntersections(b, destination).size() == 0) {
                    path.add(b);
                    path.add(destination);
                    mapView.setUserPath(path);
                } else if (map.calculateIntersections(c, destination).size() == 0) {
                    path.add(destination);
                    mapView.setUserPath(path);
                }
            }
        }

        public void onSensorChanged(SensorEvent se) {
            pathDrawer();
            if (se.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                rot = se.values;
            }
            if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                acc = se.values;
            }
            if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                mag = se.values;
            }
            if (acc != null && mag != null) {
                float R[] = new float[9];
                float I[] = new float[9];
                boolean success = SensorManager.getRotationMatrix(R, I, acc, mag);
                if (success) {
                    float[] orient = new float[3];
                    SensorManager.getOrientation(R, orient);
                    orientation = orient[0];
                    orientation = Math.toDegrees(orientation);
                    if (orientation < 0) {
                        orientation = Math.abs(orientation);
                    } else if (orientation > 0) {
                        orientation = Math.abs(orientation - 360);
                    }
                    if (orientation <= 45 || orientation > 315) {
                        direction.setText("Current Direction: NORTH");
                    } else if (orientation > 45 && orientation <= 135) {
                        direction.setText("Current Direction: WEST");
                        prevor = orientation;
                    } else if (orientation > 135 && orientation <= 225) {
                        direction.setText("Current Direction: SOUTH");
                    } else if (orientation > 225 && orientation <= 315) {
                        direction.setText("Current Direction: EAST");
                    }
                }
            }
            double diff = orientation - prevor;
            double avg = (orientation + prevor) / 2;
            if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                //we only care about the z component as our algorithm depends on the vertical acceleration
                z = se.values[2];
                //threshold
                if (z >= 3.587) {
                    state = 1;
                } else if (z <= -3.587) {
                    state = 0;
                } else {
                    state = 2;  //state 2, where the acceleration is in-between the boundaries
                }
                if (state == 2) {
                    prevor = orientation;
                }
                //if acceleration is increasing
                if ((prev == 2 && state == 1) || (prev == 0 && state == 1)) {
                    if (Math.abs(destination.x - mapView.getUserPoint().x) < 0.8
                            && Math.abs(destination.y - mapView.getUserPoint().y) < 0.8) {
                        path.clear();
                        notification = "You have reached your destination.";
                    } else {
                        notification = "";
                    }
                    counter++;  //register as 1 step
                    prev = state;  //sets previous state to the current state

                    if (avg < 45 || avg > 315)

                    {
                        nscounter++;
                        pos.y -= 1;
                        mapView.setUserPoint(pos);
                    } else if (avg <= 202.5 && avg > 157.5 && (Math.abs(diff) > 270))

                    {
                        nscounter++;
                        pos.y -= 1;
                        mapView.setUserPoint(pos);
                    } else if (avg > 135 && avg <= 225)

                    {
                        nscounter--;
                        pos.y += 1;
                        mapView.setUserPoint(pos);
                    } else if (avg > 225 && avg <= 315)

                    {
                        wecounter++;
                        pos.x += 1;
                        mapView.setUserPoint(pos);
                    } else if (avg > 45 && avg <= 135)

                    {
                        wecounter--;
                        pos.x -= 1;
                        mapView.setUserPoint(pos);
                    }

                    //if acceleration is decreasing
                } else if (prev == 1 && state == 0)

                {
                    prev = state;
                }

            }
            output.setText("Orientation in degrees: " + orientation + "\nTotal steps: " + counter + "\nN-S: " + nscounter + "\nW-E: " + wecounter + "\n" + notification);
        }
    }
}
