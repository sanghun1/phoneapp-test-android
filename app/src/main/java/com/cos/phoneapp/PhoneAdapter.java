package com.cos.phoneapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// 어댑터와 RecyclerView와 연결 (Databinding 사용금지) (MVVM 사용금지)
public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.MyViewHolder> {

    private static final String TAG = "PhoneAdapter";
    private List<Phone> phones;
    private MainActivity mainActivity;

    public PhoneAdapter(List<Phone> phones, MainActivity mainActivity) {
        this.phones = phones;
        this.mainActivity = mainActivity;
    }
    public void addPhone(Phone phone){
        phones.add(phone);
        notifyDataSetChanged();
    }

    public void removePhone(int pos){
        phones.remove(pos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.phone_item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setItem(phones.get(position));
    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textName, textTel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.name);
            textTel = itemView.findViewById(R.id.tel);

            itemView.setOnClickListener(v -> {
                Phone phone = phones.get(getAdapterPosition());
                mainActivity.edit(getAdapterPosition(),phone);
            });
     }

        public void setItem(Phone phone) {
            textName.setText(phone.getName());
            textTel.setText(phone.getTel());
        }
    }
}