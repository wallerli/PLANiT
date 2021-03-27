package com.example.planit;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FirstFragment extends Fragment {

    RecyclerView recyclerView;
    View view, rootView;
    String filter;
    Handler handler;
    List<UUID> filteredProjects;
    List<UUID> allProjects;
    MenuItem searchItem;
    SearchView searchView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        if (searchItem != null && !searchItem.isActionViewExpanded()) {
            filter = "";
            showAllProjects();
            if (searchView != null)
                searchView.clearFocus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchView != null)
            searchView.clearFocus();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_first, container, false);
        rootView = inflater.inflate(R.layout.activity_main, container, false);

        // Add the following lines to create RecyclerView
        recyclerView = view.findViewById(R.id.projectsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 2));
        showAllProjects();

        handler = new Handler();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        searchItem = menu.findItem(R.id.action_search);
        searchView = (android.widget.SearchView) searchItem.getActionView();
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                showFilteredProjects();
                searchView.requestFocus();
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                showAllProjects();
                searchView.clearFocus();
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter = query;
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onQueryTextChange(String newText) {
                new Handler().postDelayed(() -> {
                    filter = newText;
                    showFilteredProjects();
                }, 500);
                return false;
            }
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showFilteredProjects() {
        if (filter == null)
            showAllProjects();
        else {
            Globals globals = Globals.getInstance();
            filteredProjects = globals.getOrderedProjects().stream().filter(p ->
                    globals.getProject(p).getTitle().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
            recyclerView.setAdapter(new ProjectAdapter(view.getContext(),filteredProjects));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showAllProjects() {
        allProjects = Globals.getInstance().getOrderedProjects();
        recyclerView.setAdapter(new ProjectAdapter(view.getContext(), allProjects));
    }
}