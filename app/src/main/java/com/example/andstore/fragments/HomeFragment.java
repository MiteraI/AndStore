package com.example.andstore.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.andstore.R;
import com.example.andstore.adapters.ShopItemAdapter;
import com.example.andstore.models.ShopItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private GoogleMap mMap;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<ShopItem> shopItemList;
    private TextView welcomeText;
    private TextView usernameText;
    private SupportMapFragment mapFragment;
    private static final LatLng SYDNEY = new LatLng(-34, 151);
    private static final float DEFAULT_ZOOM = 12f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout, container, false);

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Initialize views
        welcomeText = view.findViewById(R.id.welcome_text);
        usernameText = view.findViewById(R.id.username_text);

        // Check if user has logged in
        if (mAuth.getCurrentUser() != null) {
            welcomeText.setText("Welcome");
            usernameText.setText(mAuth.getCurrentUser().getEmail());
        }

        // Set up banner slider
        setupBannerSlider(view);

        // Set up RecyclerView
        setupRecyclerView(view);


        if (getActivity() != null) {
            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                getChildFragmentManager().beginTransaction()
                        .add(R.id.google_map, mapFragment)
                        .commit();
            }
            mapFragment.getMapAsync(this);
        }

        return view;
    }

    private void setupBannerSlider(View view) {
        ArrayList<SlideModel> imageList = new ArrayList<>();
        imageList.add(new SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years.", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct.", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://bit.ly/3fLJf72", "And people do that.", ScaleTypes.FIT));

        ImageSlider imageSlider = view.findViewById(R.id.banner_slider);
        imageSlider.setImageList(imageList);
    }

    private void setupRecyclerView(View view) {
        shopItemList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.shop_item_recycler_view);
        layoutManager = new GridLayoutManager(getContext(), 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ShopItemAdapter(view.getContext() ,shopItemList);
        recyclerView.setAdapter(adapter);

        // Get items from Firestore
        fStore.collection("shop-products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    shopItemList.add(new ShopItem(
                            document.getId(),
                            document.getString("productName"),
                            document.getString("productImageUrl"),
                            document.getString("productDesc"),
                            "1",
                            document.getDouble("productPrice"),
                            document.getLong("productStockQuantity").intValue()
                    ));
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Error getting documents", Toast.LENGTH_SHORT).show();
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
    }

    private void setupMap() {
        if (mMap != null) {
            mMap.clear(); // Clear existing markers
            mMap.addMarker(new MarkerOptions().position(SYDNEY).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, DEFAULT_ZOOM));
        }
    }
}