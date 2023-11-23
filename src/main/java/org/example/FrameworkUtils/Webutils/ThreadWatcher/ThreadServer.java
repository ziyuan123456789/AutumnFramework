package org.example.FrameworkUtils.Webutils.ThreadWatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangzhiyi
 * @since 2023.10
 */
public class ThreadServer implements Server{

    private List<Watcher> watchers=new ArrayList<>();
    @Override
    public void registerObserver(Watcher watcher) {
        watchers.add(watcher);

    }

    @Override
    public void removeObserver(Watcher watcher) {
        watchers.remove(watcher);

    }

    @Override
    public void tellObservers(String str) {
        for(Watcher watcher:watchers){
            watcher.getmessage(str);
        }
    }


}
