package com.ridho.recycler;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvHeroes;
    private HeroAdapter heroAdapter;
    private List<Hero> heroList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Setup RecyclerView
        rvHeroes = findViewById(R.id.rvHeroes);
        rvHeroes.setLayoutManager(new LinearLayoutManager(this));

        // Bikin data hero ML
        heroList = new ArrayList<>();
        heroList.add(new Hero("Chou", "Fighter"));
        heroList.add(new Hero("Gusion", "Assassin"));
        heroList.add(new Hero("Franco", "Tank"));
        heroList.add(new Hero("Layla", "Marksman"));
        heroList.add(new Hero("Kagura", "Mage"));
        heroList.add(new Hero("Estes", "Support"));
        heroList.add(new Hero("Ling", "Assassin"));
        heroList.add(new Hero("Aldous", "Fighter"));
        heroList.add(new Hero("Tigreal", "Tank"));
        heroList.add(new Hero("Beatrix", "Marksman"));
        heroList.add(new Hero("Pharsa", "Mage"));
        heroList.add(new Hero("Zilong", "Fighter"));

        // Pasang Adapter ke RecyclerView
        heroAdapter = new HeroAdapter(heroList);
        rvHeroes.setAdapter(heroAdapter);
    }
}