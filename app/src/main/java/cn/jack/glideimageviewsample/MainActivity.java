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
        glideImageView.setImageUrl("http://img0.imgtn.bdimg.com/it/u=2158975693,779167620&fm=214&gp=0.jpg");
    }
}
