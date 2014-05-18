package com.mindpin.note;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mindpin.note.dao.RecordsDAO;
import com.mindpin.note.imageFilter.BigBrotherFilter;
import com.mindpin.note.imageFilter.BlackWhiteFilter;
import com.mindpin.note.imageFilter.BrightContrastFilter;
import com.mindpin.note.imageFilter.EdgeFilter;
import com.mindpin.note.imageFilter.IImageFilter;
import com.mindpin.note.imageFilter.Image;
import com.mindpin.note.imageFilter.InvertFilter;
import com.mindpin.note.imageFilter.PixelateFilter;

public class NoteEditFragment extends Fragment implements OnClickListener {
	private EditText edit;
	private int INSERTIMG_CODE = 501;
	/** 20k≈约1万汉字 */
	private float MAXSIZE;
	private RelativeLayout selectLayout;
	private LinearLayout recordLayout;
	private View noteEditView;
	private Recorder mRecorder;
	private TextView mTimerView;
	private TextView mRecordText;
	private ImageView imageView;
	String mTimerFormat;
	private RecordsDAO recordsDAO;
	final Handler mHandler = new Handler();
	Runnable mUpdateTimer = new Runnable() {
		public void run() {
			Log.v("mandy", "update time");
			updateTimerView();
		}
	};
	private Button back;
	private Button insertImg;
	private Button record;
	private Button save;
	private Button saveRecord;
	private String noteContent;
	private String picturePath;
	private String recordPath;
	private String id;
	private boolean isReEdit = false;
	private TextView textView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Bundle bundle = getArguments();
		if (bundle != null) {
			noteContent = bundle.getString("note_content");
			picturePath = bundle.getString("note_picture");
			recordPath = bundle.getString("note_record");
			id = bundle.getString("note_id");
			isReEdit = true;
		}
	
		
		noteEditView = inflater.inflate(R.layout.edit_main, container, false);
		int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		MAXSIZE = maxMemory / 8;
		mRecorder = new Recorder();
		mTimerFormat = getResources().getString(R.string.timer_format);
		initApp();

		recordsDAO = new RecordsDAO(getActivity());
	

		return noteEditView;
	}

	private void initApp() {
		edit = (EditText) noteEditView.findViewById(R.id.edit_text);
		selectLayout = (RelativeLayout) noteEditView
				.findViewById(R.id.selectLayout);
		recordLayout = (LinearLayout) noteEditView
				.findViewById(R.id.recordLayout);
		mTimerView = (TextView) noteEditView.findViewById(R.id.timerView);
		mRecordText = (TextView) noteEditView.findViewById(R.id.soundRecord);
		back = (Button) noteEditView.findViewById(R.id.back);
		insertImg = (Button) noteEditView.findViewById(R.id.insert_img);
		record = (Button) noteEditView.findViewById(R.id.record);
		save = (Button) noteEditView.findViewById(R.id.save);
		imageView = (ImageView) noteEditView.findViewById(R.id.picture);
		saveRecord = (Button) noteEditView.findViewById(R.id.saveRecord);
		textView = (TextView)noteEditView.findViewById(R.id.runtime);
		back.setOnClickListener(this);
		insertImg.setOnClickListener(this);
		record.setOnClickListener(this);
		save.setOnClickListener(this);
		mRecordText.setOnClickListener(this);
		saveRecord.setOnClickListener(this);
		
		if (isReEdit) {
		   if (null != noteContent && !"".equals(picturePath)) {
			 edit.setText(noteContent);
		   }
		   if (null != picturePath && !"".equals(picturePath) ) {
			   try {
				imageView.setVisibility(View.VISIBLE);
				imageView.setImageBitmap(BitmapFactory.decodeStream(this.getActivity().getContentResolver().openInputStream(Uri.parse(picturePath))));
				imageView.setTag(picturePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		   }
		  if (null != recordPath && !"".equals(recordPath)) {
			  mRecordText.setVisibility(View.VISIBLE);
		      mRecordText.setTag(recordPath);
		  }
		 	
		}

	}

	/**
	 * load image filter
	 */
	private void LoadImageFilter() {
		Gallery gallery = (Gallery) noteEditView.findViewById(R.id.galleryFilter);
		
		final ImageFilterAdapter filterAdapter = new ImageFilterAdapter(
				this.getActivity());
		gallery.setAdapter(new ImageFilterAdapter(this.getActivity()));
		gallery.setSelection(1);
		gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				IImageFilter filter = (IImageFilter) filterAdapter.getItem(position);
				new processImageTask(getActivity(), filter).execute();
			}
		});
	}

	public class processImageTask extends AsyncTask<Void, Void, Bitmap> {
		private IImageFilter filter;
        private Activity activity = null;
		public processImageTask(Activity activity, IImageFilter imageFilter) {
			this.filter = imageFilter;
			this.activity = activity;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			textView.setVisibility(View.VISIBLE);
		}

		public Bitmap doInBackground(Void... params) {
			Image img = null;
			try
	    	{
				img = new Image(((BitmapDrawable)imageView.getDrawable()).getBitmap());  
				
				if (filter != null) {
					img = filter.process(img);
					img.copyPixelsFromBuffer();
				}
				return img.getImage();
	    	}
			catch(Exception e){
				if (img != null && img.destImage.isRecycled()) {
					img.destImage.recycle();
					img.destImage = null;
					System.gc(); // 提醒系统及时回收
				}
			}
			finally{
				if (img != null && img.image.isRecycled()) {
					img.image.recycle();
					img.image = null;
					System.gc(); // 提醒系统及时回收
				}
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			if(result != null){
				super.onPostExecute(result);
				imageView.setImageBitmap(result);	
//				saveBitmap(result);
				
			}
			textView.setVisibility(View.GONE);
		}
	}
	
	public class ImageFilterAdapter extends BaseAdapter {
		private class FilterInfo {
			public int filterID;
			public IImageFilter filter;

			public FilterInfo(int filterID, IImageFilter filter) {
				this.filterID = filterID;
				this.filter = filter;
			}
		}

		private Context mContext;
		private List<FilterInfo> filterArray = new ArrayList<FilterInfo>();

		public ImageFilterAdapter(Context c) {
			mContext = c;
			
			filterArray.add(new FilterInfo(R.drawable.invert_filter, new InvertFilter()));
			filterArray.add(new FilterInfo(R.drawable.blackwhite_filter, new BlackWhiteFilter()));
			filterArray.add(new FilterInfo(R.drawable.edge_filter, new EdgeFilter()));
			filterArray.add(new FilterInfo(R.drawable.pixelate_filter, new PixelateFilter()));
			filterArray.add(new FilterInfo(R.drawable.bigbrother_filter, new BigBrotherFilter()));
			filterArray.add(new FilterInfo(R.drawable.brightcontrast_filter,new BrightContrastFilter()));
		}

		public int getCount() {
			return filterArray.size();
		}

		public Object getItem(int position) {
			return position < filterArray.size() ? filterArray.get(position).filter
					: null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Bitmap bmImg = BitmapFactory
					.decodeResource(mContext.getResources(),
							filterArray.get(position).filterID);
			int width = 100;// bmImg.getWidth();
			int height = 100;// bmImg.getHeight();
			bmImg.recycle();
			ImageView imageview = new ImageView(mContext);
			imageview.setImageResource(filterArray.get(position).filterID);
			imageview.setLayoutParams(new Gallery.LayoutParams(width, height));
			imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);// 设置显示比例类型
			return imageview;
		}
	};

	
	
	private void stopAudioPlayback() {
		// Shamelessly copied from MediaPlaybackService.java, which
		// should be public, but isn't.
		Intent i = new Intent("com.android.music.musicservicecommand");
		i.putExtra("command", "pause");

		this.getActivity().sendBroadcast(i);
	}

	/**
	 * Update the big MM:SS timer. If we are in playback, also update the
	 * progress bar.
	 */
	private void updateTimerView() {
		int state = mRecorder.state();

		boolean ongoing = state == Recorder.RECORDING_STATE
				|| state == Recorder.PLAYING_STATE;

		long time = ongoing ? mRecorder.progress() : mRecorder.sampleLength();
		String timeStr = String.format(mTimerFormat, time / 60, time % 60);

		mTimerView.setText(timeStr);
		if (ongoing)
			mHandler.postDelayed(mUpdateTimer, 1000);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == this.getActivity().RESULT_OK
				&& requestCode == INSERTIMG_CODE) {
			Uri uri = data.getData();
			Log.v("mandy", "insert picture: " + uri);
			imageView.setVisibility(View.VISIBLE);
			ContentResolver contentProvider = this.getActivity()
					.getContentResolver();
			imageView.setImageBitmap(getimage(contentProvider, uri));
			imageView.setTag(uri);
			LoadImageFilter();
			
		}
	}

	// /** 获取项目资源的URI */
	// private Uri getDrawableURI(int resourcesid) {
	// Resources r = getResources();
	// Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
	// + r.getResourcePackageName(resourcesid) + "/"
	// + r.getResourceTypeName(resourcesid) + "/"
	// + r.getResourceEntryName(resourcesid));
	// return uri;
	// }

	protected String saveFile(EditText edit2) {
		String conent = edit2.getText().toString();
		Editable eb = edit2.getEditableText();

		conent = Html.toHtml(eb);
		Log.v("mandy", "save content: " + conent);

		String fileName = String.valueOf(System.currentTimeMillis());
		fileName += ".html";
		writeFileData(fileName, conent);
		return fileName;
	}

	// 向指定的文件中写入指定的数据
	public void writeFileData(String filename, String message) {
		try {
			// 获得FileOutputStream
			FileOutputStream fout = this.getActivity().openFileOutput(filename,
					this.getActivity().MODE_APPEND);
			// 将要写入的字符串转换为byte数组
			byte[] bytes = message.getBytes();
			fout.write(bytes);// 将byte数组写入文件
			fout.close();// 关闭文件输出流
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	protected void dialog(String title, String message, String positiveButton) {

		Builder dialog = new AlertDialog.Builder(this.getActivity());
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setPositiveButton(positiveButton,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						FragmentTransaction transaction = NoteEditFragment.this
								.getActivity().getSupportFragmentManager()
								.beginTransaction();
						transaction.replace(R.id.container,
								new NoteListFragment());
						transaction.commit();
					}
				});
		dialog.show();

	}

	/** 超出大小限制 */
	protected boolean beyoundMaxSize(EditText edit2) {
		float size = edit2.getTextSize();
		if (size >= MAXSIZE) {
			return true;
		}
		return false;
	}

	// private Drawable getMyDrawable(Bitmap bitmap) {
	// Drawable drawable = new BitmapDrawable(bitmap);
	//
	// int imgHeight = drawable.getIntrinsicHeight();
	// int imgWidth = drawable.getIntrinsicWidth();
	//
	// System.out.println("setWidth:" + imgWidth);
	// drawable.setBounds(0, 0, imgWidth, imgHeight);
	// return drawable;
	// }

	// private ImageGetter imageGetter = new ImageGetter() {
	// @Override
	// public Drawable getDrawable(String source) {
	// String f = source.substring(0, 1);
	// String url = source.substring(2);
	// if (f.equals("1")) {
	// try {
	// ContentResolver cr =
	// NoteEditFragment.this.getActivity().getContentResolver();
	// Uri uri = Uri.parse(url);
	// Bitmap bitmap = getimage(cr, uri);
	// System.out.println("bitmap:" + bitmap);
	// return getMyDrawable(bitmap);
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// }
	// } else {
	// return null;
	// }
	// }
	// };

	private Bitmap getimage(ContentResolver cr, Uri uri) {
		try {
			Bitmap bitmap = null;
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			// options.inJustDecodeBounds=true,图片不加载到内存中
			newOpts.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(cr.openInputStream(uri), null, newOpts);
			// BitmapFactory.de
			newOpts.inJustDecodeBounds = false;
			// int imgWidth = newOpts.outWidth;
			// int imgHeight = newOpts.outHeight;
			// 缩放比,1表示不缩放
			int scale = 1;
			newOpts.inSampleSize = 4;// 设置缩放比例
			bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null,
					newOpts);

			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		if (back.getId() == v.getId()) {
			FragmentManager fragmentManager = NoteEditFragment.this
					.getActivity().getSupportFragmentManager();
			fragmentManager.popBackStack();

		} else if (insertImg.getId() == v.getId()) {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, INSERTIMG_CODE);

		} else if (record.getId() == v.getId()) {

			selectLayout.setVisibility(View.GONE);
			recordLayout.setVisibility(View.VISIBLE);
			stopAudioPlayback();
			mRecorder.startRecording(MediaRecorder.OutputFormat.THREE_GPP,
					".3gpp", NoteEditFragment.this.getActivity());
			updateTimerView();

		} else if (save.getId() == v.getId()) {
			if (beyoundMaxSize(edit)) {
				dialog("保存失败", "文件过大,无法保存", "返回");
			} else {
				String fileName = saveFile(edit);
				NoteInfo noteInfo = new NoteInfo();
				noteInfo.setNoteName("note");
				noteInfo.setNotePath(NoteEditFragment.this.getActivity()
						.getFilesDir().getAbsolutePath()
						+ "/" + fileName);
				noteInfo.setCreateTime(System.currentTimeMillis());
				noteInfo.setPicturePath(imageView.getTag() == null ? ""
						: imageView.getTag().toString());
				noteInfo.setAudioPath(mRecordText.getTag() == null ? ""
						: mRecordText.getTag().toString());
				
				if (isReEdit) {
					recordsDAO.updateRecordById(noteInfo, id);
					
				} else {
				     recordsDAO.insertRecord(noteInfo);
				}
				dialog("提示", "保存成功", "确定");
			}

		} else if (mRecordText.getId() == v.getId()) {
                if (isReEdit) {
                   mRecorder.startPlayback(recordPath);
                } else {
                   mRecorder.startPlayback(null);
                }
		} else if (saveRecord.getId()  == v.getId()) {
			mRecorder.stop();
			selectLayout.setVisibility(View.VISIBLE);
			recordLayout.setVisibility(View.GONE);
			mRecordText.setVisibility(View.VISIBLE);
			mRecordText.setTag(mRecorder.sampleFile().getAbsolutePath());
			
		}
	}
	
	public void saveBitmap(Bitmap bm) {
		  File files = Environment.getExternalStorageDirectory();
		   File f = new File(files, "picture.png");
		   if (f.exists()) {
		    f.delete();
		   }
		   try {
		    FileOutputStream out = new FileOutputStream(f);
		    bm.compress(Bitmap.CompressFormat.PNG, 90, out);
		    out.flush();
		    out.close();
		    
		   } catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		   } catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		   }
		 
		 }


}
