package com.example.emmawegelid.unsplashapp.screens;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.emmawegelid.unsplashapp.R;
import com.example.emmawegelid.unsplashapp.listeners.ImageSearchListener;
import com.example.emmawegelid.unsplashapp.models.Image;
import com.example.emmawegelid.unsplashapp.network.NetworkManager;
import com.example.emmawegelid.unsplashapp.network.wrappers.ImageSearchWrapper;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.emmawegelid.unsplashapp.screens.ImageItemViewHolder.IMAGE_TAPPED;

public class ImageSearchFragment extends Fragment {

    public static final String TAG = ImageSearchFragment.class.getSimpleName();

    private static final int SEARCH_QUERY_MIN_LENGTH = 3;
    private static final int SEARCH_DEBOUNCE = 300;
    private static final int GRID_NUMBER_OF_COLUMNS = 2;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private RecyclerMultiAdapter imagesAdapter;
    private ImageSearchListener imageSearchListener;

    @BindView(R.id.searchEditText)
    EditText searchEditText;

    @BindView(R.id.imagesRecyclerView)
    RecyclerView imagesRecyclerView;

    public static ImageSearchFragment newInstance() {
        return new ImageSearchFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ImageSearchListener) {
            imageSearchListener = (ImageSearchListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_search, container, false);
        ButterKnife.bind(this, view);
        initBindings();
        initRecyclerView();
        initListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Image search");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        imageSearchListener = null;
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

    private void initRecyclerView() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), GRID_NUMBER_OF_COLUMNS);
        imagesRecyclerView.setLayoutManager(gridLayoutManager);

        imagesAdapter = SmartAdapter.empty()
                .map(Image.class, ImageItemViewHolder.class)
                .into(imagesRecyclerView);

        imagesRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0) {
                    return;
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initListener() {
        ViewEventListener listener = (actionId, object, position, view) -> {
            Image image = (Image) object;
            switch (actionId) {
                case IMAGE_TAPPED:
                    showFullScreenImage(image.urls.regular);
                    break;
            }
        };

        imagesAdapter.setViewEventListener(listener);
    }

    private void showFullScreenImage(String imageUrl) {
        hideKeyboard();
        imageSearchListener.openFullScreenImage(imageUrl);
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void search(String query) {
        disposables.clear();
        disposables.add(NetworkManager.getInstance()
                .getApiClient()
                .searchForImages(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSearchForImagesSuccess));

    }

    private void onSearchForImagesSuccess(ImageSearchWrapper.Response response) {
        imagesAdapter.setItems(response.results);
    }

}