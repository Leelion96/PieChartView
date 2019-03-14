package github.leelion96.piechartview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import github.leelion96.piechartview.Entity.PieEntry;
import github.leelion96.piechartview.View.PieView;

public class MainActivity extends AppCompatActivity {
    private PieView mPieView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPieView = findViewById(R.id.pieView);
        initPieView();
        initAddAndReduce();
    }

    private void initPieView() {
        mPieView.setColors(createColors());
        mPieView.setData(createData());
    }

    private ArrayList<PieEntry> createData() {
        ArrayList<PieEntry> pieLists = new ArrayList<>();
        pieLists.add(new PieEntry(20.00F, "服装"));
        pieLists.add(new PieEntry(20.00F, "数码产品"));
        pieLists.add(new PieEntry(20.00F, "保健品"));
        pieLists.add(new PieEntry(20.00F, "户外运动用品"));
        pieLists.add(new PieEntry(20.00F, "其他"));
        return pieLists;
    }

    private ArrayList<Integer> createColors() {
        ArrayList<Integer> colorLists = new ArrayList<>();
        colorLists.add(Color.parseColor("#EBBF03"));
        colorLists.add(Color.parseColor("#ff4d4d"));
        colorLists.add(Color.parseColor("#8d5ff5"));
        colorLists.add(Color.parseColor("#41D230"));
        colorLists.add(Color.parseColor("#4FAAFF"));
        return colorLists;
    }

    private void initAddAndReduce() {
        TextView tvAdd = findViewById(R.id.tv_add);
        TextView tvReduce = findViewById(R.id.tv_reduce);
        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams layoutParams = mPieView.getLayoutParams();
                layoutParams.height = mPieView.getMeasuredHeight() + 10;
                mPieView.setLayoutParams(layoutParams);
            }
        });
        tvReduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams layoutParams = mPieView.getLayoutParams();
                layoutParams.height = mPieView.getMeasuredHeight() - 10;
                mPieView.setLayoutParams(layoutParams);
            }
        });
    }
}
