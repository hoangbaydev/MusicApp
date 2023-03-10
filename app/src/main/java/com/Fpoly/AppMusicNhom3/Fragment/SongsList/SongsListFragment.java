package com.Fpoly.AppMusicNhom3.Fragment.SongsList;



import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Fpoly.AppMusicNhom3.Activity.SquareImageView;
import com.Fpoly.AppMusicNhom3.Database.DAO.SongsDAO;
import com.Fpoly.AppMusicNhom3.Fragment.Account.AccountFragment;
import com.Fpoly.AppMusicNhom3.Fragment.Music.PlayMusicFragment;
import com.Fpoly.AppMusicNhom3.Database.Services.CallBack.SongCallBack;
import com.Fpoly.AppMusicNhom3.Fragment.Home.HomeFragment;
import com.Fpoly.AppMusicNhom3.Fragment.SongsList.Adapter.SongOfAlbum_Adapter;
import com.Fpoly.AppMusicNhom3.Fragment.SongsList.Adapter.SongOfFavorite_Adapter;
import com.Fpoly.AppMusicNhom3.Fragment.SongsList.Adapter.SongOfPlaylist_Adapter;
import com.Fpoly.AppMusicNhom3.Fragment.UserPlayList.PlaylistFragment;
import com.Fpoly.AppMusicNhom3.Model.Album;
import com.Fpoly.AppMusicNhom3.Model.PlayList;
import com.Fpoly.AppMusicNhom3.Model.Song;
import com.Fpoly.AppMusicNhom3.Model.UserInfor;
import com.Fpoly.AppMusicNhom3.R;

import com.Fpoly.AppMusicNhom3.Model.SongIDList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SongsListFragment extends Fragment {
    ArrayList<String> list;
    ArrayList<Song> Songs;
    RecyclerView recyclerView;
    Boolean isPlayList;
    Boolean isFavorites;


    TextView tvCategory,tvTitleSongList ;
    SquareImageView imgHeader ;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_songslist,container,false);
      recyclerView = view.findViewById(R.id.rcvsonglist);
      UserInfor userInfor = UserInfor.getInstance();
      isPlayList =  userInfor.getisPlayList();
      isFavorites = userInfor.getisFavorites();

        tvCategory = view.findViewById(R.id.tvCategory);
        tvTitleSongList = view.findViewById(R.id.tvTitleSongList);
        imgHeader = view.findViewById(R.id.imgHeader);


      Toolbar toolbar = view.findViewById(R.id.toolbar);
      toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
      toolbar.setNavigationOnClickListener(new View.OnClickListener() {
          @Override
            public void onClick(View view) {
                if(isPlayList){
                    if(isFavorites){
                        changeFragment(new AccountFragment(),true);
                    }else{
                        changeFragment( new PlaylistFragment(),true);
                    }
                }else{
                        changeFragment(new HomeFragment(),true);
//                    }
                }
            }
        });

        ImageView fab = view.findViewById(R.id.fab_list);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               changeFragment( new PlayMusicFragment(),false);
            }
        });
       /*L???y danh s??ch m?? b??i h??t y??u th??ch t??? c??c fragment
        Ki???m tra fragment ???????c g???i t??? adapter playlist hay t??? account fragment ho???c album
        n???u t??? playlist th?? list b??i h??t ???????c l???y t??? danh s??ch m?? b??i h??t t??? playlistsongID trong class Global*/
        if(isPlayList){
           list = isFavorites ? userInfor.getFavorites() : userInfor.getUserPlaylist();
        }else{
            list = userInfor.getCurrentAlbum();
            //n???u kh??ng ph???i playlist th?? x??t ti???p c?? ph???i t??? album hay kh??ng, n???u c?? l???y t??? album, ng?????c l???i l???y t??? favorites c???a global class


        }
        // check list
        try {
            if (list.size()>0) {
                getData(list);
            }
        } catch (Exception e) {
            Log.d("e",e.toString()) ;
        }
        return view;
    }

    private void getData(ArrayList<String> list) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.loading);
        dialog.show();
//        Log.d("fragment",list.toString());
        SongsDAO songsDao = new SongsDAO(getContext());
        songsDao.getSongsFromList(new SongIDList(list), new SongCallBack() {
            @Override
            public void getCallBack(ArrayList<Song> song) {
                Songs = song;
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                if(isPlayList) {
                    if (isFavorites) {
                        SongOfFavorite_Adapter p_adapter = new SongOfFavorite_Adapter(getContext(), Songs, SongsListFragment.this);
                        recyclerView.setAdapter(p_adapter);
                        tvCategory.setText("B??i h??t y??u th??ch");
                        tvTitleSongList.setText("");
                        Picasso.get().load(Songs.get(Songs.size() - 1).getImage()).into(imgHeader);
                    } else {
                        //N???u t??? adapter g???i ?????n th?? s??? d???ng adapter danh s??ch ID playlist ????? c?? th??? x??a ???????c b??i h??t trong playlist
                        SongOfPlaylist_Adapter p_adapter = new SongOfPlaylist_Adapter(getContext(), Songs, SongsListFragment.this);
                        recyclerView.setAdapter(p_adapter);
                        Bundle bundle = getArguments();
                        PlayList playList = bundle.getParcelable("PlayList");
                        tvCategory.setText("PlayList");
                        tvTitleSongList.setText(playList.getName());
                        Picasso.get().load(Songs.get(Songs.size() - 1).getImage()).into(imgHeader);
                    }
                }else{
                    SongOfAlbum_Adapter adapter = new SongOfAlbum_Adapter(getContext(),Songs,SongsListFragment.this);
                    recyclerView.setAdapter(adapter);
                    Bundle bundle = getArguments();
                    Album album = bundle.getParcelable("Album");
                    tvCategory.setText("Album");
                    tvTitleSongList.setText(album.getName());
                    Picasso.get().load(album.getImage()).into(imgHeader);
                }
                dialog.dismiss();
            }
        });
    }

    private void changeFragment(Fragment fragment, Boolean isback){
        FragmentTransaction ftm = this.getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        //Ki???m tra fragment s??? ??i qua fragment playmusic hay v??? l???i fragment tr?????c ????
        if(!isback){
            bundle.putParcelableArrayList("MultipleSongs",Songs);
            bundle.putInt("fragment",1);
            fragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
        }else{
            bundle.putBoolean("AddMusic",false);
            fragment.setArguments(bundle);
            ftm.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right);
            ftm.replace(R.id.nav_host_fragment,fragment);
            ftm.commit();
        }

    }

}
