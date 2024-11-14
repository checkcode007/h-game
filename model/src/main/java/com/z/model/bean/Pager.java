package com.z.model.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Created by YYL on 2018/4/25
 */
public class Pager<T> implements Serializable {

    private int page = 0;
    private int size = 30;
    private long total;
    private Map<String, Object> params;
    private List<T> list;
    private List<String> orders;
    private long pages=0;
    private int offset;
    private long startIndex;
    private Object[] lastSortValue;
    private List<Integer> index;

    public Pager(){}
    public Pager(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public Pager(int page, int size, List<T> list) {
        this.page = page;
        this.size = size;
        this.list = list;
    }

    public int getPage() {
        return page;
    }


    public int getOffset() {
        return page * size;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
    /**虚假分页 每页数量=分页数有下一页 否则没有下一页*/
    public static long getTotalSize(int listSize,int pageIndex,int size){
       return listSize==size?(((pageIndex+1)*size)+1):(pageIndex+1)*size;
    }

    public long getPages() {
        return total % size == 0 ? total / size : total / size + 1;
    }


    public long getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }
    public Object[] getLastSortValue() {
        return lastSortValue;
    }
    public void setLastSortValue(Object[] lastSortValue) {
        this.lastSortValue = lastSortValue;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<String> getOrders() {
        return orders;
    }

    public void setOrders(List<String> orders) {
        this.orders = orders;
    }

    public List<Integer> getIndex() {
        return index;
    }

    public void setIndex(List<Integer> index) {
        this.index = index;
    }

    @JsonIgnore
    public Map<String, Object> getQueryParams() {
        Map<String, Object> map = new HashMap<>();
        if (Objects.nonNull(this.params) && this.params.size() > 0) {
            this.params.forEach((k, v) -> {
                if (Objects.nonNull(v) && !StringUtils.isEmpty(v.toString())) {
                    map.put(k, v);
                }
            });
        }
        return map;
    }

    @JsonIgnore
    public Pageable getPageable() {
        if (Objects.nonNull(this.orders) && this.orders.size() > 0) {
            List<Sort.Order> orderList = new ArrayList<>();
            orders.forEach(order -> {
                if (order.trim().length() > 0) {
                    String[] keys = order.trim().split("\\|");
                    if (keys.length == 2) {
                        if (keys[1].toLowerCase().startsWith("asc")) {
                            orderList.add(Sort.Order.asc(keys[0]));
                        } else {
                            orderList.add(Sort.Order.desc(keys[0]));
                        }
                    }
                }
            });
            return PageRequest.of(page, size, Sort.by(orderList));
        } else {
            return PageRequest.of(page, size);
        }
    }

    @Override
    public String toString() {
        return "Pager{" +
                "page=" + page +
                ", size=" + size +
                ", total=" + total +
                ", params=" + params +
                ", list=" + list +
                ", orders=" + orders +
                ", index=" + index +
                '}';
    }
}
