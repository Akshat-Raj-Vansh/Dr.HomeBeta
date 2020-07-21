package com.parse.starter.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.starter.AppointmentDisplayForPatients;
import com.parse.starter.DoctorsList;
import com.parse.starter.R;

import java.util.ArrayList;

public class SpecialityAdapter extends RecyclerView.Adapter<SpecialityAdapter.ViewHolder> {
    static ArrayList<String > specialities;
    ArrayList<Bitmap> images;
    LayoutInflater layoutInflater;
    public SpecialityAdapter(Context context, ArrayList<String> spcl, ArrayList<Bitmap> img){
        this.images=img;
        this.specialities=spcl;
        this.layoutInflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_specialist_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.text.setText(specialities.get(position));
        holder.text.setAllCaps(true);
        Log.i("Text","SET!!");
        try {
            holder.icon.setImageBitmap(images.get(position));
            Log.i("Image", "SET!!!!!!!");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return specialities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        ImageView icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon=itemView.findViewById(R.id.imageViewSpeciality);
            text = itemView.findViewById(R.id.textViewSpeciality);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(specialities.get(getAdapterPosition()).toString().equals("Appointments")){
                        Intent intent = new Intent(view.getContext(), AppointmentDisplayForPatients.class);
                        view.getContext().startActivity(intent);
                    }else {
                        Intent intent = new Intent(view.getContext(), DoctorsList.class);
                        intent.putExtra("Type", specialities.get(getAdapterPosition()));
                        view.getContext().startActivity(intent);
                    }
                }
            });

        }
    }
}
