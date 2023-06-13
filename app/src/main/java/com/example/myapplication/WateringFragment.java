package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;

public class WateringFragment extends Fragment {
    private TextView tv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_watering, container, false);

        tv = view.findViewById(R.id.tv);
        tv.setSelected(true);
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);

            // GIF를 표시할 ImageView를 가져옴
            ImageView gifImageView = view.findViewById(R.id.gif_image);

            // Glide를 사용하여 GIF를 애니메이션으로 표시
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.wateringfg)
                    .into(new ImageViewTarget<GifDrawable>(gifImageView) {
                        @Override
                        protected void setResource(@Nullable GifDrawable resource) {
                            // 애니메이션 GIF를 ImageView에 설정
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

