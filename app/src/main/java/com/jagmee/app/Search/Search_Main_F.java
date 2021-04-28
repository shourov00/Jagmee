package com.jagmee.app.Search;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.jagmee.app.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.jagmee.app.R;
import com.jagmee.app.SimpleClasses.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class Search_Main_F extends RootFragment  {

    View view;
    Context context;

   public static  EditText search_edit;
    TextView search_btn;
    public Search_Main_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_search_main, container, false);
        context=getContext();

        search_edit=view.findViewById(R.id.search_edit);

        search_btn=view.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Perform_search();
            }
        });


        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(search_edit.getText().toString().length()>0){
                    search_btn.setVisibility(View.VISIBLE);
                }
                else {
                    search_btn.setVisibility(View.GONE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        search_edit.setFocusable(true);
        UIUtil.showKeyboard(context,search_edit);


        search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    Perform_search();
                    return true;
                }
                return false;
            }
        });
        return view;
    }


    public void Perform_search(){
        if(menu_pager!=null){
            menu_pager.removeAllViews();
        }
        Set_tabs();
    }


    protected TabLayout tabLayout;
    protected ViewPager menu_pager;
    ViewPagerAdapter adapter;
    public void Set_tabs(){

        adapter = new ViewPagerAdapter(getChildFragmentManager());
        menu_pager = (ViewPager) view.findViewById(R.id.viewpager);
        menu_pager.setOffscreenPageLimit(3);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);



        adapter.addFrag(new Search_F("users"),"Users");
        adapter.addFrag(new Search_F("video"),"Videos");
        adapter.addFrag(new SoundList_F("sound"),"Sounds");


        menu_pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(menu_pager);

    }



}
