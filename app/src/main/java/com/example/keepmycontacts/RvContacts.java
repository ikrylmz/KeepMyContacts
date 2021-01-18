package com.example.keepmycontacts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RvContacts extends RecyclerView.Adapter<RvContacts.TypesListHolder> {

    private LayoutInflater inflater;
    static List<Conctact> listData;
    private CheckedListener mListener;

    public RvContacts(List<Conctact> listData, CheckedListener listener, Context context) {
        if (context == null)
            return;
        this.inflater = LayoutInflater.from(context);
        this.listData = listData;
        mListener = listener;

    }

    public interface CheckedListener {
        void onItemChecked(Conctact conctactData);
    }

    @Override
    public RvContacts.TypesListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_view_contacts, parent, false);
        return new RvContacts.TypesListHolder(view);
    }

    @Override
    public void onBindViewHolder(final RvContacts.TypesListHolder holder, final int position) {

        Conctact item = listData.get(position);
            if (item != null) {
                holder.checkBox_Contact.setText(item.getName());
            }
            else Log.e("Contact in Adapder","Contact is  null,List position is given as final");

        holder.checkBox_Contact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(isChecked){
                    Conctact conctact = listData.get(position);
                    if(conctact != null) {
                        Log.i("Contact in onChecked","Contact is not null,List position is given as final");
                        Log.i("Position in onChecked","position : "+ position);
                        mListener.onItemChecked(conctact);
                    }
                    else {
                        Log.e("Contact in onChecked", "Contact is  null,List position is given as final");
                        Log.e("Position in onChecked","position : "+ position);
                    }
                }
            }
        }
        );
            /*
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.checkBox.setChecked(true);
                    Conctact conctact = listData.get(position);
                    mListener.onItemClicked(conctact);

                }
            });

             */
    }
    @Override
    public int getItemCount() {
        return listData.size();
    }

    class TypesListHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox_Contact;

        public TypesListHolder(View itemView) {
            super(itemView);
            checkBox_Contact = (CheckBox) itemView.findViewById(R.id.this_contact_cbox);
        }
    }
}
