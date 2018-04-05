package com.example.emmawegelid.unsplashapp.screen;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.emmawegelid.unsplashapp.R;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class SearchImagesFragment extends Fragment {

    public static final String TAG = Fragment.class.getSimpleName();

    private static final int SEARCH_QUERY_MIN_LENGTH = 3;
    private static final int SEARCH_DEBOUNCE = 300;

    private final CompositeDisposable disposables = new CompositeDisposable();

    @BindView(R.id.searchEditText)
    EditText searchEditText;

    public static SearchImagesFragment newInstance() {
        return new SearchImagesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Image search");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_images, container, false);
        ButterKnife.bind(this, view);
        initBindings();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    private void initBindings() {
        disposables.add(RxTextView.textChanges(searchEditText)
                .filter(charSequence -> charSequence.length() >= SEARCH_QUERY_MIN_LENGTH)
                .debounce(SEARCH_DEBOUNCE, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .map(String::toLowerCase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::search));
    }

    private void search(String query) {
        Log.d("Searching for: ", query);
    }

}
