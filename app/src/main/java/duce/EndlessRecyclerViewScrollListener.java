package duce;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    private static final int startingPageIndex = 0;
    private int visibleThreshold = 10;
    private int currentPage = 0;
    private int previousTotalItemCount = 0;
    private boolean loading = true;     // Used to know if there is data missing to fetch or not
    private RecyclerView.LayoutManager mLayoutManager;
    private ScrollDirection mScrollDirection;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager,
        ScrollDirection direction) {
        this.mLayoutManager = layoutManager;
        mScrollDirection = direction;
    }

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager,
        ScrollDirection direction) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        mScrollDirection = direction;
    }

    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager,
        ScrollDirection direction) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        mScrollDirection = direction;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        int firstVisibleItemPosition = 0;
        int totalItemCount = mLayoutManager.getItemCount();

        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions =
                    ((StaggeredGridLayoutManager) mLayoutManager)
                    .findLastVisibleItemPositions(null);
            int[] firstVisibleItemPositions =
                    ((StaggeredGridLayoutManager) mLayoutManager)
                    .findFirstVisibleItemPositions(null);
            // get maximum element within the list
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
            firstVisibleItemPosition = getFirstVisibleItem(firstVisibleItemPositions);
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition =
                    ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            firstVisibleItemPosition =
                    ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        } else if (mLayoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition =
                    ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            firstVisibleItemPosition =
                    ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        }

        switch (mScrollDirection) {
            case DOWN:
                if (totalItemCount < previousTotalItemCount) {
                    this.currentPage = startingPageIndex;
                    this.previousTotalItemCount = totalItemCount;
                    if (totalItemCount == 0) {
                        this.loading = true;
                    }
                }

                if (loading && (totalItemCount > previousTotalItemCount)) {
                    loading = false;
                    previousTotalItemCount = totalItemCount;
                }

                if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                    currentPage++;
                    onLoadMore(currentPage, totalItemCount);
                    loading = true;
                }
                break;
            case UP:
                if (totalItemCount < previousTotalItemCount) {
                    this.currentPage = startingPageIndex;
                    this.previousTotalItemCount = totalItemCount;
                    if (totalItemCount == 0) {
                        this.loading = true;
                    }
                }

                if (loading && (totalItemCount > previousTotalItemCount)) {
                    loading = false;
                    previousTotalItemCount = totalItemCount;
                }

                if (!loading && firstVisibleItemPosition < visibleThreshold) {
                    currentPage++;
                    onLoadMore(currentPage, totalItemCount);
                    loading = true;
                }
                break;
        }
    }

    private int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    private int getFirstVisibleItem(int[] firstVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < firstVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = firstVisibleItemPositions[i];
            } else if (firstVisibleItemPositions[i] > maxSize) {
                maxSize = firstVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    public void resetState() {
        this.currentPage = this.startingPageIndex;
        this.previousTotalItemCount = 0;
        this.loading = true;
    }

    public abstract void onLoadMore(int page, int totalItemsCount);

    public enum ScrollDirection {
        UP,
        DOWN
    }
}