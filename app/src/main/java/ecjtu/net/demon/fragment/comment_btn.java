package ecjtu.net.demon.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ecjtu.net.demon.R;
import ecjtu.net.demon.activitys.webview;

/**
 * Created by Ê¥÷ë on 2015/6/13.
 */
public class comment_btn extends Fragment {

    private View view;
    private Button commentBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        webview.isComment = false;
        view = inflater.inflate(R.layout.comment_btn, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        commentBtn = (Button) view.findViewById(R.id.comment_btn);
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.comment_layout, webview.commentText).commit();
            }
        });
    }
}
