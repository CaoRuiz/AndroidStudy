package com.example.rui.androidstudy.figure;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.rui.androidstudy.R;

import java.util.Vector;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShapesActivity extends AppCompatActivity {

    RadioButton rbLine;
    RadioButton rbRectangular;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInit();
    }
    //初始化用户界面
    void layoutInit() {
        MyView myView = new MyView(this);
        setContentView(myView);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        radioInit();
        layout.addView(radioGroup);
        addContentView(layout, params);
    }

    //动态生成RadioGroup
    void radioInit() {
        radioGroup = new RadioGroup(this);

        rbLine = new RadioButton(this);
        rbLine.setText("直线");
        radioGroup.addView(rbLine);

        rbRectangular = new RadioButton(this);
        rbRectangular.setText("矩形");
        radioGroup.addView(rbRectangular);

        rbLine.setChecked(true);
    }

    public class MyView extends View {
        private Paint paint;
        private Point startPoint;
        private Point endPoint;
        public MyView(Context context) {
            super(context);
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.RED);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(5);

            startPoint = new Point();
            endPoint = new Point();
        }

        Vector<Shapes> shapeVector = new Vector<Shapes>();

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                startPoint.set((int) event.getX(), (int) event.getY());
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                endPoint.set((int) event.getX(), (int) event.getY());
                if (rbLine.isChecked()) {
                    shapeVector.add(new Line(startPoint.x, startPoint.y,
                            endPoint.x, endPoint.y));
                } else if (rbRectangular.isChecked()) {
                    shapeVector.add(new Rect(startPoint.x, startPoint.y,
                            endPoint.x, endPoint.y));
                }
            }
            //在工作者线程中被调用刷新界面
            postInvalidate();
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for (int i = 0; i < shapeVector.size(); i++) {
                shapeVector.get(i).onDraw(canvas,paint);
            }
        }
    }

}

