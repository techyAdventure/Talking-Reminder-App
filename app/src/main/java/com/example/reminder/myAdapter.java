package com.example.reminder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class myAdapter extends RecyclerView.Adapter<myAdapter.myviewholder> {
    ArrayList<Model> dataholder = new ArrayList<Model>();
    //array list to hold the reminders
    private AdapterView.OnItemClickListener onItemClickListener;

    public myAdapter(ArrayList<Model> dataholder) {
        this.dataholder = dataholder;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_reminder_file, parent, false);  //inflates the xml file in recyclerview
        return new myviewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, @SuppressLint("RecyclerView") int position) {
        holder.mTitle.setText(dataholder.get(position).getTitle());                                 //Binds the single reminder objects to recycler view
        holder.mDate.setText(dataholder.get(position).getDate());
        holder.mTime.setText(dataholder.get(position).getTime());


        holder.mdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Confirmtion!!");
                builder.setIcon(R.drawable.ic_baseline_highlight_off_24);
                builder.setMessage("Are you sure you want to delete?");
                myAdapter myAdapter = new myAdapter(dataholder);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dbManager Db = new dbManager(v.getContext());
                        Model model = dataholder.get(position);
                        String sid = model.getTitle();

                        int result = Db.deleteList(sid);
                        if( result > 0)
                        {
                            Toast.makeText(v.getContext(), "Deletion Successful", Toast.LENGTH_SHORT).show();
                            dataholder.remove(model);
                            myAdapter.notifyDataSetChanged();
                        }
                        else
                        {
                            Toast.makeText(v.getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        dialog.toString();
                    }
                });
                builder.create().show();



            }
        });

    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    class myviewholder extends RecyclerView.ViewHolder {

        TextView mTitle, mDate, mTime;
        Button mdel;

        public myviewholder(@NonNull View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.txtTitle);                               //holds the reference of the materials to show data in recyclerview
            mDate = (TextView) itemView.findViewById(R.id.txtDate);
            mTime = (TextView) itemView.findViewById(R.id.txtTime);
            mdel = itemView.findViewById(R.id.del);



        }
    }
}
