package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.Data;
import org.example.FrameworkUtils.DataStructure.MethodWrapper;

import java.util.HashMap;
import java.util.Map;


@Data
public class TrieTree {

    private final TrieNode root = new TrieNode();

    public MethodWrapper search(String path) {
        String[] segments = path.split("/");
        TrieNode currentNode = this.root;
        Map<String, String> pathParameters = new HashMap<>();

        for (String segment : segments) {
            if (segment.isEmpty()) {
                continue;
            }
            if (currentNode.staticChildren.containsKey(segment)) {
                currentNode = currentNode.staticChildren.get(segment);
            } else if (currentNode.paramChild != null) {
                currentNode = currentNode.paramChild;
                pathParameters.put(currentNode.paramName, segment);
            } else if (currentNode.wildcardChild != null) {
                currentNode = currentNode.wildcardChild;
                break;
            } else {
                return null;
            }
        }
        if (currentNode != null && currentNode.isEndpoint) {
            MethodWrapper res = currentNode.handlerInfo;
            res.setParamMap(pathParameters);
            return currentNode.handlerInfo;
        }
        return null;
    }

    public void insert(String path, MethodWrapper handlerInfo) {
        String[] paths = path.split("/");
        TrieNode node = this.root;
        for (String segment : paths) {
            if (segment.isEmpty()) {
                continue;
            }
            if (segment.startsWith("{") && segment.endsWith("}")) {
                String paramName = segment.substring(1, segment.length() - 1);
                if (node.paramChild == null) {
                    node.paramChild = new TrieNode();
                    node.paramChild.paramName = paramName;
                } else {
                    if (!node.paramChild.paramName.equals(paramName)) {
                        throw new IllegalStateException("路由冲突:在同一路径级别不能定义多个不同的路径参数" + node.paramChild.paramName);
                    }
                }
                node = node.paramChild;

            } else if (segment.startsWith("*")) {
                if (node.wildcardChild == null) {
                    node.wildcardChild = new TrieNode();
                }
                node = node.wildcardChild;

            } else {
                node = node.staticChildren.computeIfAbsent(segment, k -> new TrieNode());
            }
        }
        node.isEndpoint = true;
        if (node.handlerInfo != null) {
            throw new IllegalStateException("路由重复定义:" + path);
        }
        node.handlerInfo = handlerInfo;
    }


}
