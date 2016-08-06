package com.wuxing.imagecache;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.wuxing.activity.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class MyVideoThumbLoader {
    private ImageView imgView;
    private String path;
    private Context context;
    //创建cache
    private LruCache<String, Bitmap> lruCache;

    public MyVideoThumbLoader(){
        int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取最大的运行内存
        int maxSize = maxMemory /2;
        lruCache = new LruCache<String, Bitmap>(maxSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //这个方法会在每次存入缓存的时候调用，必须要实现的方法
                return value.getByteCount();
            }
        };
    }

    public void addVideoThumbToCache(String path,Bitmap bitmap){
        if(getVideoThumbToCache(path) == null){
            //当前地址没有缓存时，就添加
            lruCache.put(path, bitmap);
        }
    }
    public Bitmap getVideoThumbToCache(String path){

        return lruCache.get(path);

    }
    public void showThumbByAsynctack(String path,ImageView imgview){

        if(getVideoThumbToCache(path) == null){
            //异步加载
            new MyBobAsynctack(imgview, path).execute(path);
        }else{
            imgview.setImageBitmap(getVideoThumbToCache(path));
        }

    }
    /**
	 * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
	 * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * 
	 * @param videoPath
	 *            视频的路径
	 * @param width
	 *            指定输出视频缩略图的宽度
	 * @param height
	 *            指定输出视频缩略图的高度度
	 * @param kind
	 *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
	 *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	public Bitmap getVideoThumbnail(String videoPath, int width, int height,
			int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}
    class MyBobAsynctack extends AsyncTask<String, Void, Bitmap> {
        private ImageView imgView;
        private String path;

        public MyBobAsynctack(ImageView imageView,String path) {
            this.imgView = imageView;
            this.path = path;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
                                        //这里的创建缩略图方法是调用VideoUtil类的方法，也是通过 android中提供的 ThumbnailUtils.createVideoThumbnail(vidioPath, kind);
            Bitmap bitmap = getVideoThumbnail(params[0], 200, 200, Thumbnails.MICRO_KIND);

                                         //加入缓存中
            if(getVideoThumbToCache(params[0]) == null){
                if (bitmap!=null) {
                	addVideoThumbToCache(path, bitmap);
				}
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(imgView.getTag().equals(path)){//通过 Tag可以绑定 图片地址和 imageView，这是解决Listview加载图片错位的解决办法之一
                imgView.setImageBitmap(bitmap);
            }
        }
    }
}