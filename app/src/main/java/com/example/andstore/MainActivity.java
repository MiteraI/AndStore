package com.example.andstore;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.andstore.adapters.ShopItemAdapter;
import com.example.andstore.models.ShopItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<ShopItem> shopItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Banner slider settings
        ArrayList<SlideModel> imageList = new ArrayList<SlideModel>();

        imageList.add(new SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years.", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct.", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://bit.ly/3fLJf72", "And people do that.", ScaleTypes.FIT));

        ImageSlider imageSlider = (ImageSlider) findViewById(R.id.banner_slider);
        imageSlider.setImageList(imageList);

        //Set item recycler view
        shopItemList = new ArrayList<ShopItem>();
        shopItemList.add(new ShopItem("1", "Shoes", "", "Hi", "1"));
        shopItemList.add(new ShopItem("2", "Bags", "", "Hi", "1"));
        shopItemList.add(new ShopItem("3", "Pants", "", "Hi", "1"));
        shopItemList.add(new ShopItem("4", "Shirts", "", "Hi", "1"));
        shopItemList.add(new ShopItem("5", "Pens", "", "Hi", "1"));
        shopItemList.add(new ShopItem("1", "Shoes", "", "Hi", "1"));
        shopItemList.add(new ShopItem("2", "Bags", "", "Hi", "1"));
        shopItemList.add(new ShopItem("3", "Pants", "", "Hi", "1"));
        shopItemList.add(new ShopItem("4", "Shirts", "", "Hi", "1"));
        shopItemList.add(new ShopItem("5", "Pens", "", "Hi", "1"));

        recyclerView = (RecyclerView) findViewById(R.id.shop_item_recycler_view);
        layoutManager = new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);

        //Init adapter for recycler view
        adapter = new ShopItemAdapter(shopItemList);
        recyclerView.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}