package ecjtu.net.demon.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import ecjtu.net.demon.R;
import uk.co.senab.photoview.PhotoView;

/**
 * A placeholder fragment containing a simple view.
 */
public class Show_image_ActivityFragment extends Fragment {

    private DisplayImageOptions options;

    public Show_image_ActivityFragment() {


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_show_image_, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(getActivity());

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.thumb_default)
                .showImageOnFail(R.drawable.thumb_default)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ViewPager viewPager = (ViewPager) getView().findViewById(R.id.tushuo_viewpager);
        TushuoImageAdapeter tushuoImageAdapeter = new TushuoImageAdapeter();
        tushuoImageAdapeter.setContent(getcontent());
        viewPager.setAdapter(tushuoImageAdapeter);
    }

    private String[] getcontent() {
        String[] content = {
                "http://h.hiphotos.baidu.com/image/pic/item/00e93901213fb80e94fdd9d634d12f2eb9389487.jpg",
                "http://h.hiphotos.baidu.com/image/pic/item/03087bf40ad162d9651bdb1b13dfa9ec8b13cdc4.jpg",
                "http://c.hiphotos.baidu.com/image/pic/item/91529822720e0cf305a117f60846f21fbe09aa6d.jpg"
        };
        return content;
    }

    public class TushuoImageAdapeter extends PagerAdapter {

        private String[] urls;

        public void setContent(String[] urls) {
            this.urls = urls;
        }


        @Override
        public int getCount() {
            return urls.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            ImageLoader.getInstance().displayImage(urls[position], photoView, options);
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }
    }


}
