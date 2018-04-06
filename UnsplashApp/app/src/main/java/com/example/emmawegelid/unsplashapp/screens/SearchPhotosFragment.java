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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.emmawegelid.unsplashapp.R;
import com.example.emmawegelid.unsplashapp.listeners.PhotoSearchListener;
import com.example.emmawegelid.unsplashapp.models.Photo;
import com.example.emmawegelid.unsplashapp.network.NetworkManager;
import com.example.emmawegelid.unsplashapp.network.wrappers.PhotoSearchWrapper;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.emmawegelid.unsplashapp.screens.PhotoItemViewHolder.PHOTO_TAPPED;

public class SearchPhotosFragment extends Fragment {

    public static final String TAG = SearchPhotosFragment.class.getSimpleName();

    private static final int SEARCH_QUERY_MIN_LENGTH = 3;
    private static final int SEARCH_DEBOUNCE = 300;
    private static final int GRID_NUMBER_OF_COLUMNS = 2;

    private final CompositeDisposable disposables = new CompositeDisposable();

    private RecyclerMultiAdapter photosAdapter;
    private PhotoSearchListener photoSearchListener;

    private int pageToFetch;
    private int totalPages;
    private boolean isLoading;
    private String currentQuery;
    private List<Photo> photoItems;

    @BindView(R.id.searchEditText)
    EditText searchEditText;

    @BindView(R.id.infoTextView)
    TextView infoTextView;

    @BindView(R.id.photosRecyclerView)
    RecyclerView photosRecyclerView;

    @BindView(R.id.loadingIndicator)
    ProgressBar loadingIndicator;

    @BindView(R.id.loadingBottomIndicator)
    ProgressBar loadingBottomIndicator;

    public static SearchPhotosFragment newInstance() {
        return new SearchPhotosFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PhotoSearchListener) {
            photoSearchListener = (PhotoSearchListener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_search, container, false);
        ButterKnife.bind(this, view);
        initBindings();
        initRecyclerView();
        initPhotoViewEventListener();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pageToFetch = 1;
        photoItems = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.photo_search_title));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        photoSearchListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }

    private void initBindings() {
        disposables.add(RxTextView.textChanges(searchEditText)
                .map(charSequence -> {
                    if (charSequence.toString().isEmpty()) {
                        clearSearch();
                    }
                    return charSequence;
                })
                .filter(charSequence -> charSequence.length() >= SEARCH_QUERY_MIN_LENGTH)
                .debounce(SEARCH_DEBOUNCE, TimeUnit.MILLISECONDS)
                .map(CharSequence::toString)
                .map(String::toLowerCase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(query -> {
                    currentQuery = query;
                    photoItems.clear();
                    pageToFetch = 1;
                    showLoading();
                    search();
                }));
    }

    private void initRecyclerView() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), GRID_NUMBER_OF_COLUMNS);
        photosRecyclerView.setLayoutManager(gridLayoutManager);

        photosAdapter = SmartAdapter.empty()
                .map(Photo.class, PhotoItemViewHolder.class)
                .into(photosRecyclerView);

        photosRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0) {
                    return;
                }
                super.onScrolled(recyclerView, dx, dy);
                hideKeyboard();
                if (shouldFetchMorePhotos()) {
                    showLoadingBottom();
                    pageToFetch++;
                    search();
                }
            }
        });
    }

    private void initPhotoViewEventListener() {
        ViewEventListener listener = (actionId, object, position, view) -> {
            Photo photo = (Photo) object;
            switch (actionId) {
                case PHOTO_TAPPED:
                    showFullScreenPhoto(photo.urls.regular);
                    break;
            }
        };

        photosAdapter.setViewEventListener(listener);
    }

    private void clearSearch() {
        hideInfoTextView();
        if (photoItems != null) {
            photoItems.clear();
            photosAdapter.setItems(photoItems);
        }
    }

    private void showFullScreenPhoto(String photoUrl) {
        hideKeyboard();
        photoSearchListener.showFullScreenPhoto(photoUrl);
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void search() {
        hideInfoTextView();
        isLoading = true;
        disposables.add(NetworkManager.getInstance()
                .getApiClient()
                .searchPhotos(currentQuery, pageToFetch)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSearchSuccess, throwable -> onSearchError()));
    }

    private void onSearchSuccess(PhotoSearchWrapper.Response response) {
        hideLoadingIndicators();
        totalPages = response.total_pages;
        photoItems.addAll(response.results);
        photosAdapter.setItems(photoItems);
        if (photoItems.isEmpty()) {
            showInfoTextView(R.string.empty_search_result);
        }
    }

    private void onSearchError() {
        hideLoadingIndicators();
        showInfoTextView(R.string.search_error);
    }

    private boolean shouldFetchMorePhotos() {
        GridLayoutManager gridLayoutManager = (GridLayoutManager) photosRecyclerView.getLayoutManager();
        final int itemCount = gridLayoutManager.getItemCount();
        final int lastItemPosition = gridLayoutManager.findLastVisibleItemPosition();
        return !isLoading && lastItemPosition + 1 >= itemCount && pageToFetch <= totalPages;
    }

    private void showInfoTextView(int resourceId) {
        infoTextView.setVisibility(View.VISIBLE);
        infoTextView.setText(resourceId);
    }

    private void hideInfoTextView() {
        infoTextView.setVisibility(View.GONE);
    }

    private void hideLoadingIndicators() {
        isLoading = false;
        hideLoading();
        hideLoadingBottom();
    }

    private void showLoadingBottom() {
        loadingBottomIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingBottom() {
        loadingBottomIndicator.setVisibility(View.GONE);
    }

    private void showLoading() {
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingIndicator.setVisibility(View.GONE);
    }

}
