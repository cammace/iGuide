package team6.iguide;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<DetailItemRecycler> dataSource;
    private Context mContext;

    public RecyclerAdapter(Context context, List<DetailItemRecycler> dataSource){
        this.dataSource = dataSource;
        this.mContext = context;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, null);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DetailItemRecycler detailItem = dataSource.get(position);


        holder.titleTextView.setText(detailItem.getTitle());
        holder.descriptionTextView.setText(detailItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return (null != dataSource ? dataSource.size() : 0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView titleTextView;
        public TextView descriptionTextView;
        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView)itemView.findViewById(R.id.menuItemTitle);
            descriptionTextView = (TextView) itemView.findViewById(R.id.menuItemDescription);
        }
    }
}