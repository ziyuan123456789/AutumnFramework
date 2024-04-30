package org.example.FrameworkUtils.WebFrameworkBaseUtils.ThreadWatcher;


/**
 * @author ziyuan
 * @since 2023.10
 */
public interface Server {
    void registerObserver(Watcher watcher);
    void removeObserver(Watcher watcher);
    void tellObservers(String str) throws ClassNotFoundException;

}
