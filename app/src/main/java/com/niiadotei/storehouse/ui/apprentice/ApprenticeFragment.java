package com.niiadotei.storehouse.ui.apprentice;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.niiadotei.storehouse.DrawerActivity;
import com.niiadotei.storehouse.R;
import com.niiadotei.storehouse.databinding.FragmentApprenticeBinding;

public class ApprenticeFragment extends Fragment {
    private FragmentApprenticeBinding binding;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ApprenticeViewModel apprenticeViewModel =
                new ViewModelProvider(this).get(ApprenticeViewModel.class);

        binding = FragmentApprenticeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textApprentice;
        apprenticeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.apprentice_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem dashboard = menu.findItem(R.id.drawer_menu);

        dashboard.setOnMenuItemClickListener(menuItem -> {
            Intent intent = new Intent(getActivity(), DrawerActivity.class);
            requireActivity().startActivity(intent);


           return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
