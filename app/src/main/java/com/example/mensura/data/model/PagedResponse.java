package com.example.mensura.data.model;

import java.util.List;

public class PagedResponse<T> {
    private List<T> content;
    private int totalPages;
    private int totalElements;
    private int number;
    private boolean last;
    private boolean first;


    public List<T> getContent() { return content; }
    public int getTotalPages() { return totalPages; }
    public int getTotalElements() { return totalElements; }
    public boolean isLast() { return last; }
    public boolean isFirst() { return first; }
    public int getNumber() { return number; }
}