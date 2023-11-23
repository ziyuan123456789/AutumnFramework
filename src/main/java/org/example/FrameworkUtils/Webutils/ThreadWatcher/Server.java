package org.example.FrameworkUtils.Webutils.ThreadWatcher;


/**
 * @author wangzhiyi
 * @since 2023.10
 */
public interface Server {
    void registerObserver(Watcher watcher);
    void removeObserver(Watcher watcher);
    void tellObservers(String str);

}
