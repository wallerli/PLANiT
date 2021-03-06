package com.morphedstudio.planit;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class FirstFragment extends Fragment {

    RecyclerView recyclerView;
    View view, rootView;
    TextView emptyRecyclerText;
    String filter;
    Handler handler;
    List<UUID> filteredProjects;
    List<UUID> allProjects;
    MenuItem searchItem;
    SearchView searchView;
    FloatingActionButton fab;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        if (searchItem == null || !searchItem.isActionViewExpanded()) {
            filter = "";
            showAllProjects();
            if (searchView != null)
                searchView.clearFocus();
        } else {
            showFilteredProjects();
            searchView.requestFocus();
        }
        if (fab != null && fab.getVisibility() != View.VISIBLE) {
            fab.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onPause() {
        super.onPause();
        if (searchView != null)
            searchView.clearFocus();
        if (((TabLayout)getActivity().findViewById(R.id.tabLayout)).getSelectedTabPosition() == 1)
            new Handler().postDelayed(this::onResume, 400);
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
        emptyRecyclerText = view.findViewById(R.id.empty_recycler_text);
        fab = requireActivity().findViewById(R.id.fab);
        showAllProjects();

        handler = new Handler();
        setHasOptionsMenu(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE && !fab.isExpanded()) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        ((TabLayout) requireActivity().findViewById(R.id.tabLayout)).addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) { }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0)  {
                    onResume();
                    recyclerView.scrollToPosition(0);
                }
            }
        });

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
                filter = newText;
                showFilteredProjects();
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
            List<UUID> newFilteredProjects = globals.getOrderedProjects().stream().filter(p ->
                    globals.getProject(p).getTitle().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
            if (filteredProjects != null && recyclerView.getAdapter().getItemCount() == newFilteredProjects.size()
                    && filteredProjects.size() == newFilteredProjects.size() && filteredProjects.containsAll(newFilteredProjects)) {
                filteredProjects = newFilteredProjects;
                recyclerView.getAdapter().notifyDataSetChanged();
            } else {
                filteredProjects = newFilteredProjects;
                recyclerView.setAdapter(new ProjectAdapter(view.getContext(), filteredProjects));
            }
            if (filteredProjects.size() == 0)
                emptyRecyclerText.setVisibility(View.VISIBLE);
            else
                emptyRecyclerText.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showAllProjects() {
        filteredProjects = null;
        List<UUID> newProjects = Globals.getInstance().getOrderedProjects();
        if (allProjects != null && Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() == newProjects.size()
                && allProjects.equals(newProjects)) {
            allProjects = newProjects;
            recyclerView.getAdapter().notifyDataSetChanged();
        } else {
            allProjects = newProjects;
            recyclerView.setAdapter(new ProjectAdapter(view.getContext(), allProjects));
        }
        if (allProjects.size() == 0)
            emptyRecyclerText.setVisibility(View.VISIBLE);
        else
            emptyRecyclerText.setVisibility(View.GONE);
    }
}