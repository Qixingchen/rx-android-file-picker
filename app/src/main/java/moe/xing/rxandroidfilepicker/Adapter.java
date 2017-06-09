package moe.xing.rxandroidfilepicker;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;

import moe.xing.rvutils.BaseRecyclerViewAdapter;
import moe.xing.rxandroidfilepicker.databinding.ItemFileBinding;

/**
 * Created by Qi Xingchen on 17-6-9.
 */

public class Adapter extends BaseRecyclerViewAdapter<File, Adapter.ViewHolder> {
    Adapter() {
        super(File.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_file, parent, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bindVH(datas.get(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemFileBinding mBinding;

        ViewHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.findBinding(itemView);
        }

        void bindVH(final File file) {
            mBinding.setFile(file);
            mBinding.executePendingBindings();
        }
    }
}
