package iea.vaccum.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import iea.vaccum.CustomObjects.Tile;
import iea.vaccum.R;

public class TilesAdapter extends RecyclerView.Adapter<TilesAdapter.ViewHolder> {

    private List<Tile> data;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;
    private boolean dirtProducers;

    // data is passed into the constructor
    public TilesAdapter(Context context, List<Tile> data) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }
    public TilesAdapter(Context context, List<Tile> data, boolean dirtProducers) {
        this.mInflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
        this.dirtProducers = dirtProducers;
    }
    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = mInflater.inflate(R.layout.item_tiles, parent, false);
        return new ViewHolder(view);
    }
    // binds the data to the TextView in each row
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if(!dirtProducers){
            if(data.get(position).ismRightWall()){
                holder.rightWall.setVisibility(View.VISIBLE);
            }
            else{
                holder.rightWall.setVisibility(View.INVISIBLE);
            }
            if(data.get(position).ismDownWall()){
                holder.downWall.setVisibility(View.VISIBLE);
            }
            else{
                holder.downWall.setVisibility(View.INVISIBLE);
            }
            if(data.get(position).ismDirt()){
                holder.dirtImageView.setVisibility(View.VISIBLE);
            }
            else{
                holder.dirtImageView.setVisibility(View.INVISIBLE);
            }
            if(data.get(position).ismVaccumCleaner()){
                holder.vaccumImageView.setVisibility(View.VISIBLE);
            }
            else{
                holder.vaccumImageView.setVisibility(View.INVISIBLE);
            }

            if(data.get(position).ismSelected()){
                holder.selectedImageView.setVisibility(View.VISIBLE);
            }
            else{
                holder.selectedImageView.setVisibility(View.INVISIBLE);
            }
        }
        else
        {
            if(data.get(position).ismRightWall()){
                holder.rightWall.setVisibility(View.VISIBLE);
            }
            else{
                holder.rightWall.setVisibility(View.INVISIBLE);
            }
            if(data.get(position).ismDownWall()){
                holder.downWall.setVisibility(View.VISIBLE);
            }
            else{
                holder.downWall.setVisibility(View.INVISIBLE);
            }
            if(data.get(position).ismDirt()){
                holder.dirtImageView.setVisibility(View.VISIBLE);
            }
            else{
                holder.dirtImageView.setVisibility(View.INVISIBLE);
            }
            if(data.get(position).ismVaccumCleaner()){
                holder.vaccumImageView.setVisibility(View.VISIBLE);
            }
            else{
                holder.vaccumImageView.setVisibility(View.INVISIBLE);
            }
            if(data.get(position).isDirtProducers()){
                holder.dirtProducerImageView.setVisibility(View.VISIBLE);
            }
            else{
                holder.dirtProducerImageView.setVisibility(View.INVISIBLE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateThemWithWait() {
        notifyDataSetChanged();
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        View downWall,rightWall;
        ImageView dirtImageView,vaccumImageView,selectedImageView,dirtProducerImageView;
        ViewHolder(View itemView) {
            super(itemView);
            downWall = itemView.findViewById(R.id.downWall);
            rightWall = itemView.findViewById(R.id.rightWall);
            dirtImageView = itemView.findViewById(R.id.dirtImageView);
            vaccumImageView = itemView.findViewById(R.id.vaccumImageView);
            selectedImageView = itemView.findViewById(R.id.selectedImageView);
            dirtProducerImageView = itemView.findViewById(R.id.dirtProducerImageView);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public Tile getItem(int id) {
        return data.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void update(ArrayList<Tile> dataa) {
        data.clear();
        data.addAll(dataa);
        notifyDataSetChanged();

    }
}
