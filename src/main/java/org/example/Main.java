package org.example;

import org.example.FrameworkUtils.AutumnCore.Annotation.EnableAutoConfiguration;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnFrameworkRunner;

import java.util.HashSet;
import java.util.Set;


/*
  @author ziyuan
 * @since 2023.10
 */
@MyConfig
@EnableAutoConfiguration
//@CompomentScan({"org.example"})
//@EnableAutumnFramework
public class Main {
    public static void main(String[] args) {
      AutumnFrameworkRunner autumnFrameworkRunner=new AutumnFrameworkRunner();
      autumnFrameworkRunner.run(Main.class);
    }


    int maxDiameter = 0;

    public int diameterOfBinaryTree(TreeNode root) {
        maxDepth(root);
        return maxDiameter;
    }

    int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        int leftMax = maxDepth(root.left);
        int rightMax = maxDepth(root.right);
        // 后序位置，顺便计算最大直径
        int myDiameter = leftMax + rightMax;
        maxDiameter = Math.max(maxDiameter, myDiameter);

        return 1 + Math.max(leftMax, rightMax);
    }


}

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }

    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

