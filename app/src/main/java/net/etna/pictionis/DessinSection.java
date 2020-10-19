package net.etna.pictionis;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.Map;

public class DessinSection extends View
{
    private Paint brush = new Paint();
    private ArrayMap<String, Path> usersPaths = new ArrayMap<>();

    public DessinSection(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        brush.setAntiAlias(true);
        brush.setStyle(Paint.Style.STROKE);
        brush.setStrokeJoin(Paint.Join.ROUND);
        brush.setStrokeWidth(8f);
        brush.setColor(Color.GRAY);
        loadData();
    }

    public void loadData() {
        ValueEventListener pathListener = new ValueEventListener()
        {
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        String userId = childDataSnapshot.getKey();
                        for (DataSnapshot snapshotSingle : childDataSnapshot.getChildren()) {
                            if (!usersPaths.containsKey(userId)) {
                                usersPaths.put(userId, new Path());
                            }
                            Point point = snapshotSingle.getValue(Point.class);
                            usersPaths.put(userId, getPathFromDatabase(point, usersPaths.get(userId)));
                        }
                    }
                } else {
                    clear();
                }
                invalidate();
            }
        };
        FirebaseDatabase.getInstance().getReference().child("dessin").addValueEventListener(pathListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float pointX = event.getX();
        float pointY = event.getY();
        int actionType = event.getAction();
        Point point = new Point();
        point.setX(pointX);
        point.setY(pointY);
        point.setEvent(actionType);
        if (actionType == MotionEvent.ACTION_DOWN || actionType == MotionEvent.ACTION_MOVE) {
            FirebaseDatabase.getInstance().getReference().child("dessin").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(point);
            this.postInvalidate();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        for (Map.Entry<String, Path> userPath : usersPaths.entrySet()) {
            canvas.drawPath(userPath.getValue(), brush);
        }
    }

    private Path getPathFromDatabase(Point pathPoint, Path path)
    {
        float pointX = pathPoint.getX();
        float pointY = pathPoint.getY();
        int event = pathPoint.getEvent();
        if (event == MotionEvent.ACTION_DOWN) {
            path.moveTo(pointX, pointY);
        } else if (event == MotionEvent.ACTION_MOVE) {
            path.lineTo(pointX, pointY);
        }
        return path;
    }

    public void clear()
    {
        FirebaseDatabase.getInstance().getReference().child("dessin").removeValue();
        for (Map.Entry<String, Path> path : usersPaths.entrySet()) {
            path.getValue().reset();
        }
        this.invalidate();
    }
}