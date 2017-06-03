package co.edu.udea.compumovil.gr04_20171.proyectocm20171.group;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.udea.compumovil.gr04_20171.proyectocm20171.R;

/**
 * Created by jonnatan on 30/05/17.
 */

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder>
        implements View.OnClickListener{

    private ArrayList<Group> groups;
    private View.OnClickListener listener;

    public GroupListAdapter(ArrayList<Group> datos) {
        this.groups = datos;
    }

    @Override
    public GroupListAdapter.GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        GroupViewHolder evh = new GroupViewHolder(view);
        view.setOnClickListener(this);
        return evh;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onBindViewHolder(GroupListAdapter.GroupViewHolder holder, int position) {
        holder.eventName.setText(groups.get(position).getGroupName());
        //holder.eventDescription.setText(events.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }

    public static class GroupViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView eventName;
        TextView eventDescription;
        ImageView eventImage;
        ImageButton eventLocation;
        ImageButton eventInformation;


        GroupViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv_group);
            //eventName = (TextView) itemView.findViewById(R.id.event_name);
            //eventDescription = (TextView) itemView.findViewById(R.id.event_description);
            //eventImage = (ImageButton) itemView.findViewById(R.id.event_location);
            //eventInformation = (ImageButton) itemView.findViewById(R.id.event_information);
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
