package com.example.myapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.interfaces.RecyclerViewClickListener;
import com.example.myapplication.model.PropertyModel;

import java.util.ArrayList;
import java.util.Random;

public class ExplorePropertyAdapter extends RecyclerView.Adapter<ExplorePropertyAdapter.MyViewHolder> {
    ArrayList<PropertyModel> arrayList;
    Context context;

    final private RecyclerViewClickListener clickListener;

    public ExplorePropertyAdapter(Context context, ArrayList<PropertyModel> arrayList, RecyclerViewClickListener clickListener) {
        this.arrayList = arrayList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.explore_item_holder, parent, false);

        final MyViewHolder myViewHolder = new MyViewHolder(view);
//        for random color of cards
//        int[] androidColors = view.getResources().getIntArray(R.array.androidcolors);
//        int randomColors = androidColors[new Random().nextInt(androidColors.length)];
//
//        myViewHolder.accordian_title.setBackgroundColor(randomColors);

        myViewHolder.arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(myViewHolder.accordian_body.getVisibility() == View.VISIBLE) {
                    myViewHolder.accordian_body.setVisibility(View.GONE);
                } else {
                    myViewHolder.accordian_body.setVisibility(View.VISIBLE);
                }
            }
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final String title = arrayList.get(position).getTitle();
        final String price = arrayList.get(position).getPrice();
        final String description = arrayList.get(position).getDescription();
        final String location = arrayList.get(position).getLocality() + " ," + arrayList.get(position).getCity();
        final String id = arrayList.get(position).getId();

        holder.titlePv.setText(title);
        holder.pricePv.setText(price);
        holder.locationPv.setText(location);
        if(!description.equals("")) {
            holder.descriptionPv.setText(description);
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView accordian_title;
        TextView titlePv, pricePv, locationPv,descriptionPv,statusPv,contact;
        RelativeLayout accordian_body;
        ImageView arrow, callButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titlePv = (TextView) itemView.findViewById(R.id.property_title);
            pricePv = (TextView) itemView.findViewById(R.id.price);
            locationPv = (TextView) itemView.findViewById(R.id.location);
            descriptionPv = (TextView) itemView.findViewById(R.id.property_description);
            accordian_title = (CardView) itemView.findViewById(R.id.accordian_title);
            accordian_body = (RelativeLayout) itemView.findViewById(R.id.accordian_body);
            arrow = (ImageView) itemView.findViewById(R.id.arrow);
            callButton = (ImageView) itemView.findViewById(R.id.call);
            contact = (TextView) itemView.findViewById(R.id.contact);


            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });

            contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });

        }
    }
}
