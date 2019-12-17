package com.example.secondwork;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import com.example.secondwork.adapter.ThumbnailAdapter;
import com.example.secondwork.model.Sticker;
import com.example.secondwork.util.DegreesUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener, View.OnClickListener {

    private ThumbnailAdapter thumbnailAdapter;
    private String jsonData ;
    private List<Sticker> stickers;
    private ImageView imageView;
    private ImageView ticket_image_view;
    private ConstraintLayout ticket_layout;

    public static Bitmap mbitmap = null;
    public static int width;
    public static int height;
    private Matrix matrix = new Matrix();
    private float last_x;
    private float last_y;
    private float x1,y1;
    private float x2,y2;
    private float distance_of_fingues = -1;
    //public static HashMap<String,String> stickerMap = new HashMap<>(160);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jsonData = getJsonString("json/sticker.json");
        stickers = parseJson(jsonData);
        thumbnailAdapter = new ThumbnailAdapter(stickers);
        imageView = findViewById(R.id.image_view);

        ticket_image_view = findViewById(R.id.ticket_image_view);
        ticket_layout = findViewById(R.id.ticket_layout);
        imageView.setOnClickListener(this);
        ticket_image_view.setFocusable(true);
        ticket_image_view.setOnTouchListener(this);
        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.HORIZONTAL);
        manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(thumbnailAdapter);
    }

    public String getJsonString(String filename) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(filename)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public List<Sticker> parseJson(String jsonStr){
        List<Sticker> list = new ArrayList<>(50);
        Gson gson = new Gson();
        list = gson.fromJson(jsonStr,new TypeToken<List<Sticker>>(){}.getType());
        return list;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_view:
                BitmapDrawable bitmapDrawable = (BitmapDrawable) this.ticket_image_view.getDrawable();
                if(bitmapDrawable.getBitmap()!=null) {
                    ImageView change = findViewById(R.id.change);
                    ImageView del = findViewById(R.id.delete);
                    if (change.getVisibility() == View.VISIBLE) {
                        change.setVisibility(View.INVISIBLE);
                    } else if (change.getVisibility() == View.INVISIBLE) {
                        change.setVisibility(View.VISIBLE);
                    }
                    if (del.getVisibility() == View.VISIBLE) {
                        del.setVisibility(View.INVISIBLE);
                    } else if (del.getVisibility() == View.INVISIBLE) {
                        del.setVisibility(View.VISIBLE);
                    }
                }
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getPointerCount()==1) {
                    last_x = event.getRawX();
                    last_y = event.getRawY();
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if(event.getPointerCount()==2){
                    x1 = event.getX(0);
                    y1 = event.getY(0);
                    x2 = event.getX(1);
                    y2 = event.getY(1);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount()==1) {
                    float dx = event.getRawX() - last_x;
                    float dy = event.getRawY() - last_y;
                    int left = (int) (ticket_layout.getLeft() + dx);
                    int top = (int) (ticket_layout.getTop() + dy);
                    int right = (int) (ticket_layout.getRight() + dx);
                    int bottom = (int) (ticket_layout.getBottom() + dy);
                    ticket_layout.layout(left, top, right, bottom);
                    last_x = event.getRawX();
                    last_y = event.getRawY();
                }else if(event.getPointerCount()==2) {
                    float x3 = event.getX(0), y3 = event.getY(0);
                    float x4 = event.getX(1), y4 = event.getY(1);
                    float[] nodes = new float[2];
                    float degree = 0.0f;
                    float scale = 1;
                    boolean isNode = DegreesUtil.getNode(x1, y1, x2, y2, x3, y3, x4, y4, nodes);
                    if (isNode) {
                        degree = (float) DegreesUtil.getActionDegrees(nodes[0], nodes[1], x3, y3, x1, y1);
                    }
                    float cur_dis = DegreesUtil.getDistanceOfFingue(event);
                    if(distance_of_fingues<0){
                        distance_of_fingues = cur_dis;
                    }else {
                        scale = cur_dis / distance_of_fingues;//distance_of_fingues = cur_dis;
                    }
                    ticket_layout.setRotation(-degree);
                    ticket_layout.setScaleX(scale);
                    ticket_layout.setScaleY(scale);
//                    matrix.reset();
//                    matrix.setScale(scale, scale);
//                    matrix.postRotate(-degree);
//                    BitmapDrawable bitmapDrawable = (BitmapDrawable) ticket_image_view.getDrawable();
//                    Bitmap tmp = bitmapDrawable.getBitmap();
//                    Bitmap bitmap = Bitmap.createBitmap(mbitmap, 0, 0, width, height, matrix, true);
//                    if(tmp.isRecycled()==false)tmp.recycle();
//                    ticket_image_view.setImageBitmap(bitmap);

                }
                break;
            case MotionEvent.ACTION_UP:
                distance_of_fingues = -1;
                break;
            default:
                break;
        }
        return true;
    }


    //    public static HashMap<String,String> getStickerMap(){
//        return stickerMap;
//    }
//    public void saveThumbnail(List<Sticker> stickers) throws FileNotFoundException {
//        String preUrl = "https://res.guangzhuiyuan.cn/perfectme//sticker/";
//            for(Sticker s:stickers){
//                FileOutputStream fos = openFileOutput(s.getThumbnail(), Context.MODE_PRIVATE);
//                InputStream in = download(preUrl+s.getThumbnail());
//                Bitmap bitmap = null;
//                if(in!=null) {
//                    bitmap = BitmapFactory.decodeStream(in);
//                }else {
//                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
//                }
//                if(bitmap!=null)
//                    bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
//            }
//    }
//    public InputStream download(String url){
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder().url(url).build();
//        ResponseBody body = null;
//        try {
//            body = client.newCall(request).execute().body();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        InputStream in = body.byteStream();
//        return in;
//    }
//    public static void initMap(List<Sticker> stickers){
//        for(Sticker s:stickers){
//            stickerMap.put(s.getThumbnail(),s.getImageName());
//        }
//    }
//        initMap(stickers);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    saveThumbnail(stickers);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
}
