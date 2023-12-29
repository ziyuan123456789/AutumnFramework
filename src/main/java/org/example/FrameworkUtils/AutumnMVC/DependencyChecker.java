package org.example.FrameworkUtils.AutumnMVC;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnMVC.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnMVC.Annotation.Value;
import org.example.FrameworkUtils.Exception.BeanCreationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2023.12
 */
@Slf4j
@MyComponent
public class DependencyChecker {
    private Map<String, List<Class<?>>> beanDependencies;
    @Value("allow-circular-references")
    private boolean enableCycleDependency;

    public void checkForCyclicDependencies(Map<String, List<Class<?>>> beanDependencies) {
        this.beanDependencies=beanDependencies;
        Set<String> visited = new HashSet<>();
        Map<String, String> parentMap = new HashMap<>();
        List<List<String>> allCycles = new ArrayList<>();

        for (String beanName : beanDependencies.keySet()) {
            if (!visited.contains(beanName)) {
                Set<String> localVisited = new HashSet<>();
                List<String> cycle = new ArrayList<>();
                if (isCyclic(beanName, localVisited, parentMap, cycle, visited, allCycles)) {
                    if (!cycle.isEmpty()) {
                        allCycles.add(new ArrayList<>(cycle));
                        cycle.clear();
                    }
                }
                visited.addAll(localVisited);
            }
        }

        printAllCycles(allCycles);
    }

    private boolean isCyclic(String current, Set<String> localVisited, Map<String, String> parentMap, List<String> cycle, Set<String> globalVisited, List<List<String>> allCycles) {
        localVisited.add(current);
        globalVisited.add(current);

        boolean foundCycle = false;

        if (beanDependencies.containsKey(current)) {
            for (Class<?> depClass : beanDependencies.get(current)) {
                String depName = depClass.getName();

                if (!globalVisited.contains(depName)) {
                    parentMap.put(depName, current); // Record parent

                    if (isCyclic(depName, localVisited, parentMap, cycle, globalVisited, allCycles)) {
                        foundCycle = true;
                        if (!cycle.isEmpty()) {
                            allCycles.add(new ArrayList<>(cycle));
                            cycle.clear();
                        }
                    }
                } else if (localVisited.contains(depName)) {
                    buildCyclePath(parentMap, current, depName, cycle);
                    foundCycle = true;
                    if (!cycle.isEmpty()) {
                        allCycles.add(new ArrayList<>(cycle));
                        cycle.clear();
                    }
                }
            }
        }

        localVisited.remove(current); // Remove from localVisited when backtracking
        return foundCycle;
    }

    private void buildCyclePath(Map<String, String> parentMap, String start, String end, List<String> cycle) {
        while (start != null && !start.equals(end)) {
            cycle.add(start);
            start = parentMap.get(start);
        }
        cycle.add(end);
    }

    private void printAllCycles(List<List<String>> allCycles) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n");
        sb.append("不鼓励循环引用,循环依赖的Bean如下:\n\n");
        for (List<String> cycle : allCycles) {
            sb.append("┌──->──┐\n");
            for (String bean : cycle) {
                sb.append("|  ").append(bean).append(" \n");
            }
            sb.append("└──<-──┘\n\n");
        }
        log.warn(sb.toString());
        if(! enableCycleDependency  ){
            throw new BeanCreationException("当前禁止循环依赖\n启动循环依赖请在配置文件中添加:allow-circular-references=true");
        }
    }
}
