package co.edu.udea.compumovil.gr04_20171.proyectocm20171.cyclist;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import co.edu.udea.compumovil.gr04_20171.proyectocm20171.R;

/**
 * Created by jonnatan on 25/05/17.
 */

public class CyclistListAdapter extends RecyclerView.Adapter<CyclistListAdapter.CyclistViewHolder>
        implements View.OnClickListener {

    private ArrayList<Cyclist> cyclists;
    private View.OnClickListener listener;

    public CyclistListAdapter(ArrayList<Cyclist> datos) {
        this.cyclists = datos;
    }

    @Override
    public CyclistListAdapter.CyclistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        CyclistListAdapter.CyclistViewHolder evh = new CyclistListAdapter.CyclistViewHolder(view);
        view.setOnClickListener(this);
        return evh;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    @Override
    public int getItemCount() {
        return cyclists.size();
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public static class CyclistViewHolder extends RecyclerView.ViewHolder {
        TextView user;
        //TextView eventDescription;
        //ImageView eventImage;
        //ImageButton eventLocation;
        //ImageButton eventInformation;


        CyclistViewHolder(View itemView) {
            super(itemView);
            user = (TextView) itemView.findViewById(R.id.tv_user);
            //eventDescription = (TextView) itemView.findViewById(R.id.event_description);
            //eventImage = (ImageButton) itemView.findViewById(R.id.event_location);
            //eventInformation = (ImageButton) itemView.findViewById(R.id.event_information);
        }

    }

    @Override
    public void onBindViewHolder(CyclistViewHolder holder, int position) {
        Log.e("user in recycler", cyclists.get(position).getUser());
        holder.user.setText(cyclists.get(position).getUser());
    }


}
