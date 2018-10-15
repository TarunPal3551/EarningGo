package com.avilaksh.earningo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NotificationRecylerViewAdapter extends RecyclerView.Adapter<NotificationRecylerViewAdapter.ViewHolder> {
    private static final String TAG = "NotificationRecylerView";
    private List<NotificationItem> notificationItems;
    Context mContext;

    public NotificationRecylerViewAdapter(List<NotificationItem> notificationItems, Context mContext) {
        this.notificationItems = notificationItems;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item_layout, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tittle.setText(String.valueOf(notificationItems.get(position).getTittle()));
        holder.body.setText(String.valueOf(notificationItems.get(position).getBody()));
        //holder.offerimage.setImageResource(R.drawable.offer);

    }

    @Override
    public int getItemCount() {

        return notificationItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView offerimage;
        TextView tittle, body;

        public ViewHolder(View itemView) {
            super(itemView);
            offerimage = (ImageView) itemView.findViewById(R.id.imageViewoffer);
            tittle = (TextView) itemView.findViewById(R.id.tittlenotification);
            body = (TextView) itemView.findViewById(R.id.bodynotification);


        }
    }
}
