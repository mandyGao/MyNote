package com.mindpin.note;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.util.Xml.Encoding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends BaseAdapter{
    
    private List<NoteInfo> list;
    private Context mContext;
    private ContentResolver contentResolver;
    
    public ListAdapter (Context mContext, List<NoteInfo> list) {
        Log.v("mandy", "list: " + list);
        this.mContext = mContext;
        this.list = list;
        contentResolver = mContext.getContentResolver();
        
    }

    @Override
    public int getCount() {
     
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
          if (convertView == null) {
              LayoutInflater inflater = (LayoutInflater) mContext
                      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
              View view = inflater.inflate(R.layout.note_list_item, null);
              convertView = view;
          }
         TextView textView = (TextView)convertView.findViewById(R.id.noteText);
         ImageView imageView =(ImageView)convertView.findViewById(R.id.image);
         ImageView recordView = (ImageView)convertView.findViewById(R.id.record);
         
          if (list.get(position) != null) {
             
             File file = new File(list.get(position).getNotePath());
             textView.setText(Html.fromHtml(readFileData(file.getName())).toString());
             if (!"".equals(list.get(position).getPicturePath())) {
            	   imageView.setImageBitmap(getimage(contentResolver,Uri.parse(list.get(position).getPicturePath())));
             } else {
            	 imageView.setVisibility(View.GONE);
             }
             if (!"".equals(list.get(position).getAudioPath())) {
            	 
            	 recordView.setImageResource(R.drawable.ic_attachment_audio);
             } else {
            	 recordView.setVisibility(View.GONE);
            	 
             }
             convertView.setTag(textView.getText().toString());
             
         
          } 
        return convertView;
    }
    private Bitmap getimage(ContentResolver cr, Uri uri) {
        try {
            Bitmap bitmap = null;
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            // options.inJustDecodeBounds=true,图片不加载到内存中
            newOpts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(cr.openInputStream(uri), null, newOpts);

            newOpts.inJustDecodeBounds = false;
//            int imgWidth = newOpts.outWidth;
//            int imgHeight = newOpts.outHeight;
            // 缩放比,1表示不缩放
//            int scale = 1;

//            if (imgWidth > imgHeight && imgWidth > MainActivity.screenWidth) {
//                scale = (int) (imgWidth / MainActivity.screenWidth);
//            } else if (imgHeight > imgWidth && imgHeight > MainActivity.screeHeight) {
//                scale = (int) (imgHeight / MainActivity.screeHeight);
//            }
            newOpts.inSampleSize = 2;// 设置缩放比例
            bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null,
                    newOpts);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // 打开指定文件，读取其数据，返回字符串对象
    public String readFileData(String fileName) {
        String result = "";
        try {
            FileInputStream fin = mContext.openFileInput(fileName);
            // 获取文件长度
            int lenght = fin.available();
            byte[] buffer = new byte[lenght];
            fin.read(buffer);
            // 将byte数组转换成指定格式的字符串
            result = EncodingUtils.getString(buffer, Encoding.UTF_8.toString());
        } catch (Exception e) {
            System.out.println("读取失败" + e);
        }
        return result;
    }
}
