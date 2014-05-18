package com.mindpin.note;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Xml.Encoding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mindpin.note.dao.RecordsDAO;

public class NoteListFragment extends Fragment implements OnClickListener { 
    
    private ListView listView;
    private ListAdapter adapter;
    private List<NoteInfo> noteList;
    private RecordsDAO recordsDAO;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      
       View rootView = inflater.inflate(R.layout.list_main, container, false); 
       listView = (ListView)rootView.findViewById(R.id.list);
       noteList = new ArrayList<NoteInfo>();
       recordsDAO = new RecordsDAO(getActivity());
       initData();
       rootView.findViewById(R.id.newNote).setOnClickListener(this);
       adapter = new ListAdapter(this.getActivity(), noteList);
       listView.setAdapter(adapter);
       listView.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			  switchFragment(new NoteShowFragment(),view.getTag().toString(), noteList.get(position));
        }
       
       });
       
        return rootView;
    }
    

    public void initData () {
        noteList = recordsDAO.findAllRecordsByDate();
  
   }
    // 打开指定文件，读取其数据，返回字符串对象
    public String readFileData(String fileName) {
        String result = "";
        try {
            FileInputStream fin = this.getActivity().openFileInput(fileName);
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

	@Override
	public void onClick(View v) {
		switchFragment(new NoteEditFragment(),null,null);
	}
	 public void switchFragment (Fragment fragment,String content, NoteInfo info) {
		  FragmentTransaction transaction =  this.getActivity().
	               getSupportFragmentManager().beginTransaction();
		   
		    if (fragment instanceof NoteShowFragment) {
		    	 Bundle bundle = new Bundle();
			     bundle.putString("note_content", content);
				 bundle.putString("note_picture", info.getPicturePath());
			     bundle.putString("note_record", info.getAudioPath());
			     bundle.putString("note_id", info.getId());
		         fragment.setArguments(bundle);
		    }
		    
	       transaction.replace(R.id.container, fragment);
	       transaction.addToBackStack(null);
	       
	       transaction.commit();
	 } 
    

}
