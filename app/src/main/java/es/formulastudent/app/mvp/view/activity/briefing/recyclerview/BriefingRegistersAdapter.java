package es.formulastudent.app.mvp.view.activity.briefing.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import es.formulastudent.app.R;
import es.formulastudent.app.mvp.data.model.BriefingRegister;


public class BriefingRegistersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<BriefingRegister> briefingRegisterList;
    private Context context;
    private LayoutInflater mLayoutInflater;


    public BriefingRegistersAdapter(List<BriefingRegister> briefingRegisterList, Context context) {
        this.briefingRegisterList = briefingRegisterList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mLayoutInflater = LayoutInflater.from(context);

        View view;

        view = mLayoutInflater.inflate(R.layout.activity_briefing_list_item, parent, false);
        return new BriefingRegistersViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        BriefingRegister register = briefingRegisterList.get(position);

        DateFormat sdf = new SimpleDateFormat("EEE, dd MMM 'at' HH:mm", Locale.US);

        BriefingRegistersViewHolder briefingRegistersViewHolder = (BriefingRegistersViewHolder)holder;
        briefingRegistersViewHolder.userName.setText(register.getUser());
        briefingRegistersViewHolder.userTeam.setText(register.getTeam());
        briefingRegistersViewHolder.registerDate.setText(sdf.format(register.getDate()));

        Picasso.get().load(register.getUserImage()).into(briefingRegistersViewHolder.profileImage);

    }

    @Override
    public int getItemCount() {
        return briefingRegisterList.size();
    }

}


