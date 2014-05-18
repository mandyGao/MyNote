package com.mindpin.note;

import java.io.FileNotFoundException;

import com.mindpin.note.dao.RecordsDAO;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class NoteShowFragment extends Fragment implements OnClickListener {

	private View showNoteView;
	private Button back, edit, delete;
	private String noteContent;
	private String picturePath;
	private String recordPath;
	private String id;
	private RecordsDAO recordsDAO;
	private Recorder recorder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		noteContent = bundle.getString("note_content");
		picturePath = bundle.getString("note_picture");
		recordPath = bundle.getString("note_record");
		id = bundle.getString("note_id");

		showNoteView = inflater.inflate(R.layout.show_note, container, false);
		initRes();
		recordsDAO = new RecordsDAO(getActivity());
		recorder = new Recorder();
		return showNoteView;
	}

	public void initRes() {
      TextView text = (TextView)showNoteView.findViewById(R.id.edit_text);
      back = (Button)showNoteView.findViewById(R.id.back);
      edit = (Button)showNoteView.findViewById(R.id.edit);
      delete = (Button)showNoteView.findViewById(R.id.delete);
      
      back.setOnClickListener(this);
      edit.setOnClickListener(this);
      delete.setOnClickListener(this);
      
	  text.setText(noteContent);
	  if (!"".equals(recordPath)) {
		 TextView recordText = (TextView)showNoteView.findViewById(R.id.soundRecord);
				 recordText.setVisibility(View.VISIBLE);
				 recordText.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						recorder.startPlayback(recordPath);
					}
				});
		 
	  }
	 if (!"".equals(picturePath)) {
		 
		 ContentResolver cr = this.getActivity().getContentResolver();
         Uri uri = Uri.parse(picturePath);
         Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((ImageView)showNoteView.findViewById(R.id.picture)).setImageBitmap(bitmap);
		 
	 }
      
		
	}

	@Override
	public void onClick(View v) {
		if(back.getId() == v.getId()){
		   this.getActivity().getSupportFragmentManager().popBackStack();	
			
		} else if (edit.getId() == v.getId()) {
			
			FragmentTransaction transaction =  this.getActivity().
		          getSupportFragmentManager().beginTransaction();
		     NoteEditFragment editFragment = new NoteEditFragment();
		     Bundle bundle = new Bundle();
		     bundle.putString("note_content", noteContent);
		     bundle.putString("note_id", id);
		     bundle.putString("note_picture", picturePath);
		     bundle.putString("note_record", recordPath);
		     editFragment.setArguments(bundle);
			 transaction.replace(R.id.container, editFragment);
			 transaction.addToBackStack(null);
			 transaction.commit();
			
		}else if (delete.getId() == v.getId()) {
			recordsDAO.deleteRecordById(id);
			this.getActivity().getSupportFragmentManager().popBackStack();			
			
		}

	}

}
