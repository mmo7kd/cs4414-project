package com.example.groundsGuide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.content.Context;
import android.content.Intent;
import android.view.View;


public class DirectionActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.fragment_direction);
    

    final ListView listview = (ListView) findViewById(R.id.listview);
    
    Intent intent = getIntent();
    String[] values = intent.getStringArrayExtra("directions");
   // for (String s: values){
   // System.out.println(s);
   // }
   // String[] values = {"hello", "world"};
   


    final ArrayList<String> list = new ArrayList<String>();
    for (int i = 0; i < values.length; ++i) {
      list.add(values[i]);
    }
    final StableArrayAdapter adapter = new StableArrayAdapter(this,
        android.R.layout.simple_list_item_1, list);
    listview.setAdapter(adapter);

    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @SuppressLint("NewApi")
	@Override
      public void onItemClick(AdapterView<?> parent, final View view,
          int position, long id) {
        final String item = (String) parent.getItemAtPosition(position);
        view.animate().setDuration(2000).alpha(0)
            .withEndAction(new Runnable() {
              @Override
              public void run() {
                list.remove(item);
                adapter.notifyDataSetChanged();
                view.setAlpha(1);
              }
            });
      }

    });
  }

  private class StableArrayAdapter extends ArrayAdapter<String> {

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

    public StableArrayAdapter(Context context, int textViewResourceId,
        List<String> objects) {
      super(context, textViewResourceId, objects);
      for (int i = 0; i < objects.size(); ++i) {
        mIdMap.put(objects.get(i), i);
      }
    }

    @Override
    public long getItemId(int position) {
      String item = getItem(position);
      return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
      return true;
    }

  }

} 
