package com.mokhtarabadi.tun2socks.sample;

import static com.mokhtarabadi.tun2socks.sample.PreferenceHelper.PREFERENCE_EXCLUDED_APPS;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.mokhtarabadi.tun2socks.sample.databinding.ActivityExcludedAppsBinding;
import com.mokhtarabadi.tun2socks.sample.databinding.LayoutAppItemBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;


public class ExcludedAppsActivity extends AppCompatActivity {

    private ActivityExcludedAppsBinding binding;

    private List<PackageInfo> original;
    private List<PackageInfo> dataSet;

    private List<String> selected;

    private AppAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExcludedAppsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        original = new ArrayList<>();
        dataSet = new ArrayList<>();

        selected = new ArrayList<>(PreferenceHelper.getExcludedApps());

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recycler.setAdapter(adapter = new AppAdapter(this));

        loadPackages();
    }

    @Override
    protected void onDestroy() {
        PreferenceHelper.preferences.edit().putStringSet(PREFERENCE_EXCLUDED_APPS, new HashSet<>(selected)).apply();

        super.onDestroy();
    }

    private void loadPackages() {
        new Thread(() -> {
            for (PackageInfo installedPackage : getPackageManager().getInstalledPackages(PackageManager.GET_PERMISSIONS)) {
                if (installedPackage.packageName.equals("android") || installedPackage.packageName.equals(getPackageName())) {
                    continue;
                }

                if (installedPackage.requestedPermissions != null) {
                    for (String requestedPermission : installedPackage.requestedPermissions) {
                        if (requestedPermission.equals(Manifest.permission.INTERNET)) {
                            original.add(installedPackage);
                        }
                    }
                }
            }

            Collections.sort(original, (o1, o2) -> {
                int diff = Integer.compare(selected.indexOf(o2.packageName), selected.indexOf(o1.packageName));

                if (diff != 0) {
                    return diff;
                }

                diff = getPackageManager().getApplicationLabel(o1.applicationInfo).toString()
                        .compareToIgnoreCase(getPackageManager().getApplicationLabel(o2.applicationInfo).toString());

                return diff;
            });

            runOnUiThread(() -> {
                binding.loading.setVisibility(View.GONE);
                binding.recycler.setVisibility(View.VISIBLE);
                binding.search.setVisibility(View.VISIBLE);

                dataSet.addAll(original);
                adapter.notifyItemRangeInserted(0, dataSet.size());

                binding.search.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            });
        }).start();
    }

    private class AppViewHolder extends RecyclerView.ViewHolder {

        private final Context mContext;
        private final LayoutAppItemBinding itemBinding;

        public AppViewHolder(@NonNull LayoutAppItemBinding itemBinding, Context context) {
            super(itemBinding.getRoot());

            mContext = context;
            this.itemBinding = itemBinding;
        }

        private void bind(PackageInfo packageInfo) {
            itemBinding.label.setText(mContext.getPackageManager().getApplicationLabel(packageInfo.applicationInfo));
            itemBinding.packageName.setText(packageInfo.packageName);
            itemBinding.excluded.setChecked(selected.contains(packageInfo.packageName));
            itemBinding.icon.setImageDrawable(mContext.getPackageManager().getApplicationIcon(packageInfo.applicationInfo));
        }
    }

    private class AppClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String packageName = ((MaterialTextView) v.findViewById(R.id.package_name)).getText().toString();
            PackageInfo packageInfo = null;
            for (PackageInfo info : dataSet) {
                if (info.packageName.equals(packageName)) {
                    packageInfo = info;
                    break;
                }
            }
            int position = dataSet.indexOf(packageInfo);

            if (!selected.contains(packageName)) {
                selected.add(packageName);
            } else {
                selected.remove(packageName);
            }
            adapter.notifyItemChanged(position);
        }
    }

    private class AppAdapter extends RecyclerView.Adapter<AppViewHolder> implements Filterable {

        private final Context mContext;
        private AppFilter appFilter;

        public AppAdapter(Context context) {
            mContext = context;
        }

        private void filter(List<PackageInfo> newItems) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new AppDiffCallback(dataSet, newItems));
            diffResult.dispatchUpdatesTo(this);
            dataSet.clear();
            dataSet.addAll(newItems);
        }

        @NonNull
        @Override
        public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutAppItemBinding itemBinding = LayoutAppItemBinding.inflate(LayoutInflater.from(mContext), parent, false);
            itemBinding.getRoot().setOnClickListener(new AppClickListener());
            return new AppViewHolder(itemBinding, mContext);
        }

        @Override
        public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
            holder.bind(dataSet.get(position));
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        @Override
        public Filter getFilter() {
            if (appFilter == null) {
                appFilter = new AppFilter(mContext);
            }
            return appFilter;
        }
    }

    private class AppFilter extends Filter {

        private final Context mContext;

        public AppFilter(Context context) {
            mContext = context;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (TextUtils.isEmpty(constraint)) {
                filterResults.values = original;
                filterResults.count = original.size();
            } else {
                List<PackageInfo> filteredDataSet = new ArrayList<>();
                String query = constraint.toString().toUpperCase(Locale.getDefault()); // search both lower/upper case

                for (PackageInfo packageInfo : original) {
                    if (mContext.getPackageManager().getApplicationLabel(packageInfo.applicationInfo).toString().toUpperCase(Locale.getDefault()).contains(query) || packageInfo.packageName.toUpperCase(Locale.US).contains(query)) {
                        filteredDataSet.add(packageInfo);
                    }
                }

                filterResults.values = filteredDataSet;
                filterResults.count = filteredDataSet.size();
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filter((List<PackageInfo>) results.values);
        }
    }

    private static class AppDiffCallback extends DiffUtil.Callback {

        private final List<PackageInfo> oldDataSet;
        private final List<PackageInfo> newDataSet;

        public AppDiffCallback(List<PackageInfo> oldDataSet, List<PackageInfo> newDataSet) {
            this.oldDataSet = oldDataSet;
            this.newDataSet = newDataSet;
        }

        @Override
        public int getOldListSize() {
            return oldDataSet.size();
        }

        @Override
        public int getNewListSize() {
            return newDataSet.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldDataSet.get(oldItemPosition).packageName.equals(newDataSet.get(newItemPosition).packageName);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldDataSet.get(oldItemPosition).equals(newDataSet.get(newItemPosition));
        }
    }
}
