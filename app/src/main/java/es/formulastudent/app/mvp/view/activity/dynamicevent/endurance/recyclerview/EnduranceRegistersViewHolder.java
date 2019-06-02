package es.formulastudent.app.mvp.view.activity.dynamicevent.endurance.recyclerview;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import es.formulastudent.app.R;

public class EnduranceRegistersViewHolder extends RecyclerView.ViewHolder{

    ImageView profileImage;
    TextView userName;
    TextView userTeam;
    TextView registerDate;
    TextView carNumber;
    ImageView carTypeIcon;

    public EnduranceRegistersViewHolder(View itemView) {
        super(itemView);
        profileImage =  itemView.findViewById(R.id.user_profile_image);
        userName =  itemView.findViewById(R.id.endurance_item_user);
        userTeam =  itemView.findViewById(R.id.endurance_item_team);
        registerDate = itemView.findViewById(R.id.endurance_item_date);
        carNumber = itemView.findViewById(R.id.carNumber);
        carTypeIcon = itemView.findViewById(R.id.carTypeIcon);
    }
}
