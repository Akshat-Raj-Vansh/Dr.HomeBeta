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

import com.parse.starter.Booking;
import com.parse.starter.DoctorProfile;
import com.parse.starter.R;

import java.util.ArrayList;

public class DoctorsListAdapter extends RecyclerView.Adapter<DoctorsListAdapter.ViewHolder> {
    static ArrayList<String> names;
    static ArrayList<Bitmap> images;

    ArrayList<String> workExpp;
    ArrayList<String> PricePerSessionn;
    LayoutInflater inflater;

    public DoctorsListAdapter(Context context, ArrayList<String> nms, ArrayList<Bitmap> img, ArrayList<String> workExp, ArrayList<String> pricePerSession){
        this.names = nms;
        this.images = img;
        this.workExpp=workExp;
        this.PricePerSessionn=pricePerSession;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_doctorslist_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(names.get(position));
        try {
            holder.avt.setImageBitmap(images.get(position));
        }catch (Exception e){
            e.printStackTrace();
        }
        holder.details.setText("Experience: "+workExpp.get(position)+"\nPrice:"+PricePerSessionn.get(position));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView details;
        ImageView avt;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            name= itemView.findViewById(R.id.textViewDoctorName);
            avt=itemView.findViewById(R.id.imageViewDoctor);
            details=itemView.findViewById(R.id.details);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), Booking.class);
                    intent.putExtra("Doctor",names.get(getAdapterPosition()) );

                    Log.i("Doctor",names.get(getAdapterPosition()));
                    view.getContext().startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent=new Intent(v.getContext(), DoctorProfile.class);
                    intent.putExtra("Name",names.get(getAdapterPosition()));
                    v.getContext().startActivity(intent);
                    return true;
                }
            });

        }
    }
}
