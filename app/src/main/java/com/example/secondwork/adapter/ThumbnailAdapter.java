package com.example.secondwork.adapter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.secondwork.MainActivity;
import com.example.secondwork.R;
import com.example.secondwork.model.Sticker;
import com.example.secondwork.util.MyApplication;
import com.example.secondwork.util.OKHttpUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {

    List<Sticker> stickers;

    public ThumbnailAdapter(List<Sticker> stickers) {
        this.stickers = stickers;
    }
    @NonNull
    @Override
    public ThumbnailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailAdapter.ViewHolder holder, int position) {
        Sticker sticker = stickers.get(position);
        Glide.with(holder.imageView.getContext())
                .load("file:///android_asset/thumbnail/"+sticker.getThumbnail())
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(holder.imageView);
        Activity activity = (Activity) holder.imageView.getContext();
        File file = new File(activity.getFilesDir(),sticker.getImageName());
        if(file.exists()){
            holder.is_download_image_view.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return stickers.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        public static final String PRE_URL = "https://res.guangzhuiyuan.cn/perfectme//sticker/";
        public static final int DOWNLOAD_SUCCESS = 1;

        private DownloadHandler handler = new DownloadHandler();

        ImageView imageView;
        ImageView is_download_image_view;
        ImageView loading_image_view;
         AnimationDrawable animationDrawable;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.item_image_view);
            is_download_image_view = itemView.findViewById(R.id.is_download_image_view);
            loading_image_view = itemView.findViewById(R.id.loading_image_view);
            animationDrawable = (AnimationDrawable) loading_image_view.getBackground();
            imageView.setOnClickListener(this);
            is_download_image_view.setOnClickListener(this);
        }
        @Override
        public void onClick(final View v) {
            final View view = v;
            int position = getAdapterPosition();
            Sticker sticker = stickers.get(position);
            switch (v.getId()){
                case R.id.is_download_image_view:
                    //下载对应的图片，判断联网状态，成功后隐藏，否则Toast用户知会其下载失败
                    if (!isNetWorkAvailable()){
                        Toast.makeText(imageView.getContext(),
                                "下载失败，请检查网络", Toast.LENGTH_SHORT).show();
                    }else {
                        //开始动画
                        loading_image_view.setVisibility(View.VISIBLE);
                        animationDrawable.start();
                        //开启下载线程，发送message,
                        final String url = PRE_URL + sticker.getImageName();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                InputStream in = OKHttpUtil.getInputStream(url);
                                saveImage(view.getContext(),in);
                                Message message = new Message();
                                message.what = DOWNLOAD_SUCCESS;
                                //通知下载成功，关闭动画
                                handler.sendMessage(message);
                                try {
                                    if(in!=null)
                                        in.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    break;
                case R.id.item_image_view:
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(view.getContext().openFileInput(sticker.getImageName()));
                        Activity mainActivity = (Activity) v.getContext();
                        WindowManager windowManager = mainActivity.getWindowManager();
                        float width = windowManager.getDefaultDisplay().getWidth();
                        float b_width = bitmap.getWidth();
                        float b_height = bitmap.getHeight();
                        bitmap = changeBitmap(bitmap,width/7,(b_height*width)/(7*b_width));
                        ImageView imageView = mainActivity.findViewById(R.id.ticket_image_view);
                        imageView.setImageBitmap(bitmap);
                        MainActivity.mbitmap = Bitmap.createBitmap(bitmap);
                        MainActivity.width = bitmap.getWidth();
                        MainActivity.height = bitmap.getHeight();
                        ImageView deleteImageView = mainActivity.findViewById(R.id.delete);
                        ImageView changeImageView = mainActivity.findViewById(R.id.change);
                        deleteImageView.setVisibility(View.VISIBLE);
                        changeImageView.setVisibility(View.VISIBLE);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }

        }
        public Bitmap changeBitmap(Bitmap bitmap,float x,float y){
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scaleWidth = x/width;
            float scaleHeight = y/height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth,scaleHeight);
            Bitmap newBitmap = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,false);
            return newBitmap;
        }
        public boolean isNetWorkAvailable(){
            ConnectivityManager manager = (ConnectivityManager) imageView.getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isAvailable()) {
                return false;
            } else {
                return true;
            }
        }
        public void saveImage(Context context,InputStream in){
            FileOutputStream fos = null;
            Sticker sticker = stickers.get(getAdapterPosition());
            try {
                fos = context.openFileOutput(sticker.getImageName(),Context.MODE_PRIVATE);
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                if(fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        private class DownloadHandler extends Handler{
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case DOWNLOAD_SUCCESS:
                        //关闭动画，隐藏下载图片
                        Toast.makeText(MyApplication.getContext(),
                                "下载完成",Toast.LENGTH_SHORT).show();
                        animationDrawable.stop();
                        loading_image_view.setVisibility(View.GONE);
                        is_download_image_view.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
