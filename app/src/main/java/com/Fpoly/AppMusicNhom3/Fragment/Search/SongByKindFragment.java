package com.Fpoly.AppMusicNhom3.Fragment.Search;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Fpoly.AppMusicNhom3.Activity.SquareImageView;
import com.Fpoly.AppMusicNhom3.Fragment.Music.PlayMusicFragment;
import com.Fpoly.AppMusicNhom3.Fragment.Search.Adapter.SongByKindAdapter;
import com.Fpoly.AppMusicNhom3.Database.DAO.SearchDAO;
import com.Fpoly.AppMusicNhom3.Database.Services.CallBack.SongCallBack;
import com.Fpoly.AppMusicNhom3.Model.Song;
import com.Fpoly.AppMusicNhom3.Model.UserInfor;
import com.Fpoly.AppMusicNhom3.R;

import java.util.ArrayList;

public class SongByKindFragment extends Fragment {
    Toolbar toolbarS;
    String kindID;
    ArrayList<Song> mySong;
    SongByKindAdapter adapter;
    private boolean loading = false;
    ProgressBar progressBar;
    LinearLayoutManager mLayoutManager;
    RecyclerView recyclerViewS;
    TextView tvCategoryS,tvTitleSongListS ;
    SquareImageView imgHeaderS ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_musicbykind, container, false);

        tvCategoryS = root.findViewById(R.id.tvCategoryS);
        tvTitleSongListS = root.findViewById(R.id.tvTitleSongListS);
        imgHeaderS = root.findViewById(R.id.imgHeaderS);


        toolbarS = root.findViewById(R.id.toolbarS);
        toolbarS.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        toolbarS.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new SearchFragment(), true);
            }
        });
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewS = root.findViewById(R.id.rcvsonglistS);
        progressBar = root.findViewById(R.id.progressBar);
        UserInfor userInfor = UserInfor.getInstance();
        kindID = userInfor.getKindID();
        getData(kindID);
        //b???t s??? ki???n khi k??o ?????n v??? tr?? cu???i c??ng trong danh s??ch
        recyclerViewS.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!loading){
                    if(mLayoutManager!=null && mLayoutManager.findLastCompletelyVisibleItemPosition() == mySong.size()-1){
                        loadMore(kindID);
                        loading = true;
                    }
                }
            }
        });

        ImageView fabS = root.findViewById(R.id.fab_listS);
        fabS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(new PlayMusicFragment(),false );
            }
        });

        return root;
    }

    // Load th??m b??i h??t khi ng?????i d??ng k??o xu???ng
    private void loadMore(String kindID){
        progressBar.setVisibility(View.VISIBLE);
        SearchDAO searchDAO = new SearchDAO(getContext());
        searchDAO.getNextMusicByKind(kindID, mySong.get(mySong.size() - 1).getID(), new SongCallBack() {
            @Override
            public void getCallBack(ArrayList<Song> song) {
                for(int i= 0; i<song.size();i ++){
                    mySong.add(song.get(i));
                }
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Get d??? li???u theo kindID t????ng ???ng
    private void getData(final String kindID) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.loading);
        dialog.show();
        SearchDAO searchDAO = new SearchDAO(getContext());
        searchDAO.getMusicByKind(kindID, new SongCallBack() {
            @Override
            public void getCallBack(ArrayList<Song> song) {
                Log.d("hehe", "123456");
                mySong = song;
                adapter = new SongByKindAdapter(getContext(),mySong,SongByKindFragment.this);
                recyclerViewS.setLayoutManager(mLayoutManager);
                recyclerViewS.setAdapter(adapter);
                tvCategoryS.setText("Th??? Lo???i");
                if (kindID == "T01") {
                    tvTitleSongListS.setText("Vi???t Nam");
                    imgHeaderS.setImageResource(R.drawable.vietnam);
                } else if (kindID == "T02") {
                    tvTitleSongListS.setText("US - UK");
                    imgHeaderS.setImageResource(R.drawable.usuk);
                }  else if (kindID == "T03") {
                    tvTitleSongListS.setText("H??n Qu???c");
                    imgHeaderS.setImageResource(R.drawable.korea);
                } else if (kindID == "T04") {
                    tvTitleSongListS.setText("Trung Qu???c");
                    imgHeaderS.setImageResource(R.drawable.china);
            }

                dialog.dismiss();
            }
        });
    }

    private void changeFragment(Fragment fragment, Boolean isback){
        FragmentTransaction ftm = this.getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        if(!isback){
            bundle.putParcelableArrayList("MultipleSongs",mySong);
            bundle.putInt("fragment",3);
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
        }else{
            ftm.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right);
            ftm.replace(R.id.nav_host_fragment,fragment);
            ftm.commit();
        }
    }
}