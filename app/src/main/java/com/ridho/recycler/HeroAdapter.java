package com.ridho.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HeroAdapter extends RecyclerView.Adapter<HeroAdapter.HeroViewHolder> {

    private List<Hero> heroList;

    public HeroAdapter(List<Hero> heroList) {
        this.heroList = heroList;
    }

    @NonNull
    @Override
    public HeroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hero, parent, false);
        return new HeroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HeroViewHolder holder, int position) {
        Hero hero = heroList.get(position);
        holder.tvHeroName.setText(hero.getName());
        holder.tvHeroRole.setText(hero.getRole());
        
        // Kasih event klik sekalian biar interaktif
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(holder.itemView.getContext(), "Kamu pick " + hero.getName() + "!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return heroList.size();
    }

    public static class HeroViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeroName, tvHeroRole;

        public HeroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeroName = itemView.findViewById(R.id.tvHeroName);
            tvHeroRole = itemView.findViewById(R.id.tvHeroRole);
        }
    }
}
