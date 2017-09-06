/*
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */

package com.baidu.mapapi.clusterutil.clustering.view;


import com.baidu.mapapi.clusterutil.clustering.Cluster;
import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager2;

import java.util.Set;

/**
 * Renders clusters.
 */
public interface ClusterRenderer2<T extends ClusterItem> {

    /**
     * Called when the view needs to be updated because new clusters need to be displayed.
     * @param clusters the clusters to be displayed.
     */
    void onClustersChanged(Set<? extends Cluster<T>> clusters);

    void setOnClusterClickListener(ClusterManager2.OnClusterClickListener<T> listener);

    void setOnClusterInfoWindowClickListener(ClusterManager2.OnClusterInfoWindowClickListener<T> listener);

    void setOnClusterItemClickListener(ClusterManager2.OnClusterItemClickListener<T> listener);

    void setOnClusterItemInfoWindowClickListener(ClusterManager2.OnClusterItemInfoWindowClickListener<T> listener);

    /**
     * Called when the view is added.
     */
    void onAdd();

    /**
     * Called when the view is removed.
     */
    void onRemove();
}