package es.formulastudent.app.mvp.view.activity.general.spinneradapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import es.formulastudent.app.R;
import es.formulastudent.app.mvp.data.model.Role;

public class RolesSpinnerAdapter extends ArrayAdapter<Role> {

    private Context context;
    private List<Role> roles;
    private LayoutInflater inflater;

    public RolesSpinnerAdapter(Context context, int textViewResourceId, List<Role> roles) {
        super(context, textViewResourceId, roles);
        this.context = context;
        this.roles = roles;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount(){
        return roles.size();
    }

    @Override
    public Role getItem(int position){
        return roles.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.activity_general_spinner, parent, false);

        Role role = roles.get(position);

        TextView teamName = view.findViewById(R.id.spinner_value);
        teamName.setText(role.getName());

        return view;
    }

}
