package com.z.core.meilisearch.json;


/**
 * MeiliSearch
 * 2023年9月21日
 */
public class SearchResult1<T> extends com.meilisearch.sdk.model.SearchResult {

    private String query;
    private int offset;
    private int limit;
    private int processingTimeMs;
    private long nbHits;


    private long hitsPerPage;
    private long page;
    private long totalPages;
    private long totalHits;

    private boolean exhaustiveNbHits;


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getProcessingTimeMs() {
        return processingTimeMs;
    }

    public void setProcessingTimeMs(int processingTimeMs) {
        this.processingTimeMs = processingTimeMs;
    }

    public long getNbHits() {
        return nbHits;
    }

    public void setNbHits(long nbHits) {
        this.nbHits = nbHits;
    }

    public boolean isExhaustiveNbHits() {
        return exhaustiveNbHits;
    }

    public void setExhaustiveNbHits(boolean exhaustiveNbHits) {
        this.exhaustiveNbHits = exhaustiveNbHits;
    }


    @Override
    public String toString() {
        return "SearchResult{" +
                "query='" + query + '\'' +
                ", offset=" + offset +
                ", limit=" + limit +
                ", processingTimeMs=" + processingTimeMs +
                ", nbHits=" + nbHits +
                ", exhaustiveNbHits=" + exhaustiveNbHits +
                '}';
    }

    public long getHitsPerPage() {
        return hitsPerPage;
    }

    public void setHitsPerPage(long hitsPerPage) {
        this.hitsPerPage = hitsPerPage;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(long totalHits) {
        this.totalHits = totalHits;
    }
}