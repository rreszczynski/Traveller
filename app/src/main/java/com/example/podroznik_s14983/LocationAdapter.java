package com.example.podroznik_s14983;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationHolder> {
    private List<MyLocation> locations = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    @NonNull
    @Override
    public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_item, parent, false);
        return new LocationHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationHolder holder, int position) {
        MyLocation currentLocation = locations.get(position);
        holder.textViewLocationName.setText(currentLocation.getName());
        holder.textViewCircle.setText(String.valueOf(currentLocation.getCircle()));

        Bitmap bitmap = MyLocation.decodeBitmapFromString(currentLocation.getEncodedPhoto());
        if(bitmap != null) {
            holder.imageViewPhoto.setImageBitmap(bitmap);
        } else {
            holder.imageViewPhoto.setImageResource(R.drawable.ic_picture_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    public void setLocations(List<MyLocation> locations) {
        this.locations = locations;
        notifyDataSetChanged();
    }

    class LocationHolder extends RecyclerView.ViewHolder {
        private TextView textViewLocationName;
        private TextView textViewCircle;
        private ImageView imageViewPhoto;

        public LocationHolder(@NonNull View itemView) {
            super(itemView);
            textViewLocationName = itemView.findViewById(R.id.text_view_location_name);
            textViewCircle = itemView.findViewById(R.id.text_view_circle);
            imageViewPhoto = itemView.findViewById(R.id.image_view_picture);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (onItemClickListener != null && position != RecyclerView.NO_POSITION) {
                        onItemClickListener.OnItemClick(locations.get(position));
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (onItemLongClickListener != null && position != RecyclerView.NO_POSITION) {
                        onItemLongClickListener.OnItemLongClick(locations.get(position));
                        return true;
                    } else return false;
                }
            });
        }
    }

    public interface OnItemClickListener {
        void OnItemClick(MyLocation location);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemLongClickListener {
        void OnItemLongClick(MyLocation location);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }
}

