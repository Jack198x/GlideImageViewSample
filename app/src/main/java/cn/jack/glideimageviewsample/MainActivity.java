package cn.jack.glideimageviewsample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import cn.jack.glideimageview.GlideImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GlideImageView glideImageView=(GlideImageView)findViewById(R.id.glide);
        glideImageView.setImageUrl("http://img-download.pchome.net/download/1k1/k7/41/opkm3s-v07.jpg");
    }
}
