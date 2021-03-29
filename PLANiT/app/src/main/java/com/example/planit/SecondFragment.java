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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class SecondFragment extends Fragment {

    RecyclerView recyclerView;
    View view;
    TextView emptyRecyclerText;
    String filter;
    Handler handler;
    List<UUID> filteredTasks;
    List<UUID> allTasks;
    MenuItem searchItem;
    SearchView searchView;
    FloatingActionButton fab;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        if (searchItem == null || !searchItem.isActionViewExpanded()) {
            filter = "";
            showAllTasks();
            if (searchView != null)
                searchView.clearFocus();
        } else {
            showFilteredTasks();
        }
        if (fab != null && fab.getVisibility() != View.VISIBLE) {
            fab.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onStart() {
        super.onStart();
        if (searchItem != null && !searchItem.isActionViewExpanded()) {
            filter = "";
            showAllTasks();
            if (searchView != null)
                searchView.clearFocus();
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
        if (searchItem == null || !searchItem.isActionViewExpanded()) {
            filter = "";
            showAllTasks();
        } else {
            showFilteredTasks();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_second, container, false);

        // Add the following lines to create RecyclerView
        recyclerView = view.findViewById(R.id.tasksRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        emptyRecyclerText = view.findViewById(R.id.empty_recycler_text);
        fab = getActivity().findViewById(R.id.fab);
        showAllTasks();

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
                showFilteredTasks();
                searchView.requestFocus();
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                showAllTasks();
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
                    showFilteredTasks();
                }, 500);
                return false;
            }
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showFilteredTasks() {
        if (filter == null)
            showAllTasks();
        else {
            Globals globals = Globals.getInstance();
            List<UUID> newFilteredTasks = globals.getOrderedTasks().stream().filter(t ->
                    globals.getTask(t).getTitle().toLowerCase().contains(filter.toLowerCase())).collect(Collectors.toList());
            if (filteredTasks != null && recyclerView.getAdapter().getItemCount() == newFilteredTasks.size()
                    && filteredTasks.size() == newFilteredTasks.size() && filteredTasks.containsAll(newFilteredTasks)) {
                filteredTasks = newFilteredTasks;
                recyclerView.getAdapter().notifyDataSetChanged();
            } else {
                filteredTasks = newFilteredTasks;
                recyclerView.setAdapter(new ProjectAdapter(view.getContext(), filteredTasks));
            }
            recyclerView.setAdapter(new TaskAdapter(view.getContext(), filteredTasks));
            if (filteredTasks.size() == 0)
                emptyRecyclerText.setVisibility(View.VISIBLE);
            else
                emptyRecyclerText.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showAllTasks() {
        filteredTasks = null;
        List<UUID> newTasks = Globals.getInstance().getOrderedTasks();
        if (allTasks != null && Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() == newTasks.size()
                && allTasks.equals(newTasks)) {
            allTasks = newTasks;
            recyclerView.getAdapter().notifyDataSetChanged();
        } else {
            allTasks = newTasks;
            recyclerView.setAdapter(new TaskAdapter(view.getContext(), allTasks));
        }
        if (allTasks.size() == 0)
            emptyRecyclerText.setVisibility(View.VISIBLE);
        else
            emptyRecyclerText.setVisibility(View.GONE);
    }
}