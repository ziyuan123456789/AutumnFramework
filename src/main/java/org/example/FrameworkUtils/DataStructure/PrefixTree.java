package org.example.FrameworkUtils.DataStructure;

import lombok.Data;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2025.02
 */

@Data
public class PrefixTree {

    private Map<String, TrieNode> root;


    public PrefixTree() {
        root = new HashMap<>();
    }

    public void addUrl(String url, MethodWrapper wrapper) {
        String[] urlParts = url.split("/");
        TrieNode currentNode = null;
        if (currentNode != null) {
            currentNode.setWrapper(wrapper);
        }
        for (String part : urlParts) {
            if (part.isEmpty()) {
                continue;
            }
            if (currentNode == null) {
                currentNode = root.computeIfAbsent(part, k -> new TrieNode(part));
            } else {
                currentNode = currentNode.getNexts().computeIfAbsent(part, k -> new TrieNode(part));
            }
        }
    }


    class TrieNode {
        private String url;

        @Setter
        private MethodWrapper wrapper;

        private Map<String, TrieNode> nexts;

        public TrieNode(String url) {
            this.url = url;
            this.nexts = new HashMap<>();
        }

        public String getUrl() {
            return url;
        }

        public Map<String, TrieNode> getNexts() {
            return nexts;
        }

        public MethodWrapper getWrapper() {
            return wrapper;
        }

    }


}
