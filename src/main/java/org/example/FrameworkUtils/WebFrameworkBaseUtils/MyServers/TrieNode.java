package org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers;

import lombok.Data;
import org.example.FrameworkUtils.DataStructure.MethodWrapper;

import java.util.HashMap;
import java.util.Map;


@Data
public class TrieNode {

    Map<String, TrieNode> staticChildren = new HashMap<>();

    TrieNode paramChild = null;

    String paramName = null;

    TrieNode wildcardChild = null;

    boolean isEndpoint = false;

    MethodWrapper handlerInfo = null;
}
