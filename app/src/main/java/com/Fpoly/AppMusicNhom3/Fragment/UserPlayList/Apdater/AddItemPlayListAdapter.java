package com.Fpoly.AppMusicNhom3.Fragment.UserPlayList.Apdater;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Fpoly.AppMusicNhom3.Fragment.Music.PlayMusicFragment;
import com.Fpoly.AppMusicNhom3.Fragment.UserPlayList.AddItemPlaylistFragment;
import com.Fpoly.AppMusicNhom3.Interface.ItemClickListener;
import com.Fpoly.AppMusicNhom3.Database.DAO.PlayListDAO;
import com.Fpoly.AppMusicNhom3.Database.Services.CallBack.PlayListCallBack;
import com.Fpoly.AppMusicNhom3.Model.PlayList;
import com.Fpoly.AppMusicNhom3.Model.UserInfor;
import com.Fpoly.AppMusicNhom3.R;

import java.util.ArrayList;

public class AddItemPlayListAdapter extends RecyclerView.Adapter<AddItemPlayListAdapter.ViewHolder> {
    Context context;
    ArrayList<PlayList> playlist;
    ArrayList<String> songArrayList = new ArrayList<>() ;
    AddItemPlaylistFragment addItemPlaylistFragment;
    public AddItemPlayListAdapter(Context context, ArrayList<PlayList> playlist, AddItemPlaylistFragment addItemPlaylistFragment) {
        this.context = context;
        this.playlist = playlist;
        this.addItemPlaylistFragment = addItemPlaylistFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_playlist,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        songArrayList = playlist.get(position).getSongs() ;
        Log.d("testSongs", String.valueOf(playlist.get(position).getSongs())) ;
        if (songArrayList == null) {
            holder.count_song.setText("B??i h??t: 0" );
        }else {
            holder.count_song.setText("B??i h??t: " + String.valueOf(songArrayList.size()));
            songArrayList.clear();
        }
        holder.tvname.setText(playlist.get(position).getName());
        holder.btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoDelete(position);
            }
        });
        holder.btn_rename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog(position);
            }
        });
        //s??? ki???n khi nh???n v??o m???t ph???n t??? trong danh s??ch, g???i ?????n h??m th??m v??o b??i h??t v??o playlist
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
               DoAddItemPlayList( playlist.get(position).getID());
            }
        });

    }

    @Override
    public int getItemCount() {
        return playlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tvname,count_song;
        ImageView btn_rename,btn_del;
        private ItemClickListener itemClickListener;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            count_song = itemView.findViewById(R.id.count_song);
            tvname = itemView.findViewById(R.id.tvplaylist_name);
            btn_del = itemView.findViewById(R.id.btn_del);
            btn_rename = itemView.findViewById(R.id.btn_rename);
            itemView.setOnClickListener(this);
        }
        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition(),false);
        }
    }

    // Them playlist
    private void ShowDialog(final int position) {
        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(context);
        LinearLayout linearLayout = new LinearLayout(context);
        final EditText name = new EditText(context);
        name.setHint("Nh???p T??n PlayList");
        name.setMinEms(16);
        linearLayout.addView(name);
        linearLayout.setPadding(10,50,10,10);
        builder.setView(linearLayout);
        //button Rename
        builder.setPositiveButton("?????i T??n", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                DoRename(position, name.getText().toString());
                dialog.dismiss();
            }
        });
        //button Cancel
        builder.setNegativeButton("H???y", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //Show Dialog
        builder.create().show();

    }

    //H??m ?????i t??n playlist, ???????c g???i khi nh???n v??o n??t h??nh c??y b??t tr??n giao di???n
    private void DoRename(int position, String name) {
        PlayListDAO playListDAO = new PlayListDAO(context);
//        UserInfor UserInfor = UserInfor.getInstance();
        UserInfor userInfor = UserInfor.getInstance();
        playListDAO.renamePlayList(userInfor.getID(),playlist.get(position).getID(),name,new PlayListCallBack() {
            @Override
            public void getCallBack(ArrayList<PlayList> playLists) {
                playlist.clear();
                playlist.addAll(playLists);
                notifyDataSetChanged();

            }
        });
    }

    //H??m x??a playlist, ???????c g???i khi nh???n v??o n??t h??nh th??ng r??c tr??n giao di???n
    private void DoDelete( final int position) {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context)
                .setMessage("B???n C?? Mu???n X??a Kh??ng")
                .setTitle("Th??ng B??o")
                .setPositiveButton("C??", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.loading);
                        dialog.show();
                        PlayListDAO playListDAO = new PlayListDAO(context);
//                        UserInfor UserInfor = UserInfor.getInstance();
                        UserInfor userInfor = UserInfor.getInstance();
                        playListDAO.deletePlaylist(userInfor.getID(), playlist.get(position).getID(), new PlayListCallBack() {
                            @Override
                            public void getCallBack(ArrayList<PlayList> playLists) {
                                playlist.clear();
                                playlist.addAll(playLists);
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                    }
                })
                .setNegativeButton("Kh??ng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#00ACC1"));
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#00ACC1"));
    }

    //H??m Th??m b??i h??t v??o playlist, ???????c g???i khi ng?????i d??ng nh???n v??o t??n playlist c???n th??m v??o
    private void DoAddItemPlayList(String PlayListID){
        UserInfor userInfor = UserInfor.getInstance();
        PlayListDAO playListDAO = new PlayListDAO(context);
        Log.e("chuyenPlaylist",userInfor.getID() + " " + PlayListID + " "+ userInfor.getTempID()) ;
        playListDAO.addItemPlayList(userInfor.getID(),PlayListID,userInfor.getTempID());
        PlayMusicFragment.bottomSheetFragment.dismiss();
    }

}
