package com.niiadotei.storehouse.ui.apprentice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.niiadotei.storehouse.databinding.FragmentApprenticeBinding;

public class ApprenticeFragment extends Fragment {
    private FragmentApprenticeBinding binding;

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
