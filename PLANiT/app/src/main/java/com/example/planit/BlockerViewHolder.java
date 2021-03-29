package com.example.planit;

import android.os.Build;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class BlockerViewHolder extends RecyclerView.ViewHolder {

    CheckBox checkBox;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public BlockerViewHolder(@NonNull View itemView) {
        super(itemView);

        checkBox = itemView.findViewById(R.id.checkBox);
    }
}
