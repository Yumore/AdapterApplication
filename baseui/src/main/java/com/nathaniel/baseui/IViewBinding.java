package com.nathaniel.baseui;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.hjq.base.binding
 * @datetime 2021/3/31 - 20:33
 */
interface IViewBinding {
    /**
     * loading data
     */
    void loadData();

    /**
     * bind view with default value or bind click listener to view
     */
    void bindView();
}
