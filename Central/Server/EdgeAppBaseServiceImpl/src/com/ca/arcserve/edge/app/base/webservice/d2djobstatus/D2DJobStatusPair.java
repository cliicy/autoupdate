package com.ca.arcserve.edge.app.base.webservice.d2djobstatus;


public class D2DJobStatusPair<K, V> {
    public K nodeId;

    public V infoBean;
    
    public D2DJobStatusPair() {}
    
    public D2DJobStatusPair(K first, V second) {
        this.nodeId = first;
        this.infoBean = second;
    }
}