package com.example.myapplication;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Integer page;
    private Integer limit;
    private Integer total;
    private Integer totalPages;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public Integer getPage() { return page; }
    public Integer getLimit() { return limit; }
    public Integer getTotal() { return total; }
    public Integer getTotalPages() { return totalPages; }
}
