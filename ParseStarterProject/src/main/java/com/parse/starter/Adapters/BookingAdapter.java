package com.parse.starter.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.starter.Booking;
import com.parse.starter.R;

import java.util.ArrayList;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {
    ArrayList<String> slot;
    ArrayList<String> avail;
    ArrayList<String> timeavail;
    public int row_index=-1;
    LayoutInflater layoutInflater;
    //private onItemClickListener onSlotClickListener;
    public BookingAdapter(Context context, ArrayList<String> spcl,ArrayList<String> av,ArrayList<String> tmav){
        //,onItemClickListener slotClickListener){
        this.slot=spcl;
        this.avail = av;
        this.timeavail = tmav;
        Log.i("Size", String.valueOf(avail.size()));
        this.layoutInflater = LayoutInflater.from(context);

        //this.onSlotClickListener = slotClickListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.custom_booking_layout,parent,false);
        return new ViewHolder(view);
        //,onSlotClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {

        holder.text.setText(slot.get(position));
        //Log.i("Position"+String.valueOf(position),timeavail.get(position));
        if(timeavail.get(position).equals("Available")|| !Booking.onCurrentDay)
            holder.availability.setText(avail.get(position));
        else
            holder.availability.setText(timeavail.get(position));

        holder.layout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Booking.pos=position;
                row_index=position;
                notifyDataSetChanged();
            }});
        if((row_index==position||(timeavail.get(position).equals("Unavailable") && Booking.onCurrentDay )|| !avail.get(position).equals("Available")) )
            holder.layout.setBackgroundColor(Color.parseColor("#f7e1d3"));

        else
            holder.layout.setBackgroundColor(Color.parseColor("#ffffff"));



    }





    @Override
    public int getItemCount() {
        return slot.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder{
        //implements View.OnClickListener{
        TextView text;
        TextView availability;
        LinearLayout layout;
        // onItemClickListener onItemClickListener;
        public ViewHolder(@NonNull View itemView){
            //,onItemClickListener itemClickListener) {
            super(itemView);

            text = itemView.findViewById(R.id.slot);
            availability= itemView.findViewById(R.id.availability);
            layout = itemView.findViewById(R.id.BookinkLinearLayout);
            //this.onItemClickListener=itemClickListener;
            // itemView.setOnClickListener(this);


        }

//        @Override
//        public void onClick(View view) {
//
//                onItemClickListener.onSlotClick(getAdapterPosition());
////                Booking.pos=getAdapterPosition();
//        }
//    }
//    public interface onItemClickListener{
//
//         void onSlotClick(int position);
//
//    }
    }}
