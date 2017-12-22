package com.richaelguitar.oomforbitmap;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    private ImageView imageView;
    private Button loadBtn,scaleBtn,leftScaleBtn,rightScaleBtn,optionsBtn;

    private Bitmap bitmap;

    private File file;

    private int offset = 10;

    private int screenWidth,screenHeight;

    public static final int PICK_PICTURE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        imageView = findViewById(R.id.iv_picture);

        loadBtn = findViewById(R.id.btn_load_pic);
        scaleBtn = findViewById(R.id.btn_scale_pic);
        optionsBtn = findViewById(R.id.btn_option_pic);
        leftScaleBtn = findViewById(R.id.btn_left_scale_pic);
        rightScaleBtn = findViewById(R.id.btn_right_scale_pic);

        loadBtn.setOnClickListener(this);
        scaleBtn.setOnClickListener(this);
        optionsBtn.setOnClickListener(this);
        leftScaleBtn.setOnClickListener(this);
        rightScaleBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_load_pic:
                loadPic();
                break;
            case R.id.btn_scale_pic:
                scaleBitmap();
                break;
            case R.id.btn_option_pic:
                optionsBitmap();
                break;
            case R.id.btn_left_scale_pic:
                offset-=10;
                areaScaleBitmap();
                break;
            case R.id.btn_right_scale_pic:
                offset+=10;
                areaScaleBitmap();
                break;
        }
    }

    /**
     * 局部缩放现实
     */
    private void areaScaleBitmap() {
        if(file!= null){
            if(bitmap != null){
                bitmap.recycle();
                bitmap = null;
            }
           try{
               //先加载图片的边框范围
               BitmapFactory.Options options = new BitmapFactory.Options();
               options.inJustDecodeBounds = true;
               BitmapFactory.decodeStream(new FileInputStream(file),null,options);
               int widthBitmap = options.outWidth;
               int heightBitmap = options.outHeight;

               if((widthBitmap/2+screenWidth/2+offset)>widthBitmap) {
                   offset=10;
                   Toast.makeText(this,"已到达最右边",Toast.LENGTH_SHORT).show();
                   return;
               }
               if((widthBitmap / 2 - screenWidth / 2 + offset)<0){
                   offset=-10;
                   Toast.makeText(this,"已到达最左边",Toast.LENGTH_SHORT).show();
                   return;
               }
               //设置图片的中心区域
               BitmapRegionDecoder bitmapRegionDecoder =  BitmapRegionDecoder.newInstance(new FileInputStream(file),false);
               bitmap = bitmapRegionDecoder.decodeRegion(new Rect(widthBitmap / 2 - screenWidth / 2 + offset, heightBitmap / 2 - screenHeight / 2,
                       widthBitmap / 2 + screenWidth / 2 + offset, heightBitmap / 2 + screenHeight / 2), new BitmapFactory.Options());
               Log.i(MainActivity.class.getSimpleName(), "bitmap size:" + bitmap.getByteCount());
               imageView.setImageBitmap(bitmap);
           }catch (Exception ex){
                ex.printStackTrace();
           }
        }else{
            Toast.makeText(this,"请先加载图片",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置解析方式
     */
    private void optionsBitmap() {
        if(file!= null){
            if(bitmap != null){
                bitmap.recycle();
                bitmap = null;
            }
            try{
            BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
            scaleOptions.inJustDecodeBounds =true;
            BitmapFactory.decodeStream(new FileInputStream(file),null,scaleOptions);
            int widthBitmap = scaleOptions.outWidth;
            int scale = 4;
            int count = 0;
            while (true){
                if(widthBitmap/scale<screenWidth){
                    break;
                }
                scale*=2;
                count++;
            }
            for(int i=0;i<count;i++){
                scale/=2;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = scale;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file),null,options);
            Log.i(MainActivity.class.getSimpleName(),"bitmap size:"+bitmap.getByteCount());
            imageView.setImageBitmap(bitmap);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else{
            Toast.makeText(this,"请先加载图片",Toast.LENGTH_SHORT).show();
        }
    }

    private void scaleBitmap() {
        if(file != null){
            if(bitmap != null){
                bitmap.recycle();
                bitmap = null;
            }
           try{
               BitmapFactory.Options options = new BitmapFactory.Options();
               options.inJustDecodeBounds = true;//仅加入图片边界，不显示图片
               BitmapFactory.decodeStream(new FileInputStream(file),null,options);
               int widthBitmap = options.outWidth;
               int scale = 4;
               int count = 0;
               while (true){
                   if(widthBitmap/scale<screenWidth){
                       break;
                   }
                   scale*=2;
                   count++;
               }
               for(int i=0;i<count;i++){
                   scale/=2;
               }
               BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
               scaleOptions.inSampleSize = scale;
               Log.i(MainActivity.class.getSimpleName(),"bitmap scale value:"+scale);
               bitmap = BitmapFactory.decodeStream(new FileInputStream(file),null,scaleOptions);
               Log.i(MainActivity.class.getSimpleName(),"bitmap size:"+bitmap.getByteCount());
               imageView.setImageBitmap(bitmap);
           }catch (Exception ex){
               ex.printStackTrace();
           }
        }else{
            Toast.makeText(this,"请先加载图片",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从系统相册中选择一张图片
     */
    private void loadPic() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                startActivityForResult(intent,PICK_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_PICTURE&&data!=null){
            Uri uri = data.getData();
            //根据uri得到图片
            String urlPath = getRealUrlForPicture(uri);
            imageView.setImageBitmap(loadBitmap(urlPath));
            Toast.makeText(this,"图片加载成功",Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * 根据url加载图片
     * @param urlPath
     */
    private Bitmap loadBitmap(String urlPath) {
       try{
           file = new File(urlPath);
           InputStream in = new FileInputStream(file);
           bitmap = BitmapFactory.decodeStream(in);
           Log.i(MainActivity.class.getSimpleName(),"bitmap size:"+bitmap.getByteCount());
           return bitmap;
       }catch (Exception ex){
           ex.printStackTrace();
       }
        return null;
    }

    /**
     * 根据uri得到系统中的路径
     * @param uri
     */
    private String getRealUrlForPicture(Uri uri) {
        String path=null;
        String[] columns = new String[]{MediaStore.Images.ImageColumns.DATA};
        Cursor cursor = getContentResolver().query(uri,columns,null,null,null);
        if(cursor!=null&&cursor.moveToFirst()){
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            path =  cursor.getString(index);
            cursor.close();
    }

        return path;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(MainActivity.class.getSimpleName(),"screen orientation is "+newConfig.orientation);
        switch (newConfig.orientation){
            case 1:
                    screenWidth = getResources().getDisplayMetrics().widthPixels;
                    screenHeight = getResources().getDisplayMetrics().heightPixels;
                break;
            case 2:
                    screenWidth = getResources().getDisplayMetrics().heightPixels;
                    screenHeight = getResources().getDisplayMetrics().widthPixels;
                break;
        }
    }
}
