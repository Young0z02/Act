package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;

public class WateringFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watering, container, false);

        // GIF를 표시할 ImageView를 가져옵니다.
        ImageView gifImageView = view.findViewById(R.id.gif_image);

        // Glide를 사용하여 GIF를 애니메이션으로 표시합니다.
        Glide.with(this)
                .asGif()
                .load(R.drawable.wateringfg)
                .into(new ImageViewTarget<GifDrawable>(gifImageView) {
                    @Override
                    protected void setResource(@Nullable GifDrawable resource) {
                        // 애니메이션 GIF를 ImageView에 설정합니다.
                        gifImageView.setImageDrawable(resource);
                        // GIF 애니메이션을 시작합니다.
                        if (resource != null) {
                            resource.start();
                        }
                    }
                });

        return view;
    }
}

