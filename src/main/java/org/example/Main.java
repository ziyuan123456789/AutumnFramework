package org.example;

import com.autumn.async.EnableAutumnAsync;
import com.autumn.cache.EnableAutumnCache;
import org.example.FrameworkUtils.AutumnCore.Annotation.EnableAutoConfiguration;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;

import java.util.ArrayList;
import java.util.List;


/*
  @author ziyuan
 * @since 2023.10
 */
@MyConfig
@EnableAutoConfiguration
@EnableAutumnAsync
@EnableAutumnCache
//@CompomentScan({"org.example"})
//@EnableAutumnFramework
public class Main {
    public static void main(String[] args) {
//      AutumnFrameworkRunner autumnFrameworkRunner=new AutumnFrameworkRunner();
//      autumnFrameworkRunner.run(Main.class);

    }

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        if(headA.equals(headB)){
            return headA;
        }
        List<Integer> headListA = new ArrayList<>();
        List<Integer> headListB = new ArrayList<>();
        ListNode tempA = headA;
        ListNode tempB = headB;
        while (tempA != null) {
            headListA.add(tempA.val);
            tempA = tempA.next;
        }
        while (tempB != null) {
            headListB.add(tempB.val);
            tempB = tempB.next;
        }
        int startPoint;
        if (headListA.size() >= headListB.size()) {
            startPoint = headListA.size() - headListB.size();
            ListNode temp = headA;
            for (int i = 0; i < startPoint; i++) {
                temp = temp.next;
            }
            ListNode tempb = headB;
            while (temp != null) {
                if (temp.equals(tempb)) {
                    return temp;
                }
                temp = temp.next;
                tempb = tempb.next;
            }
            return null;
        } else {
            startPoint = headListB.size() - headListA.size();
            ListNode temp = headB;
            for (int i = 0; i < startPoint; i++) {
                temp = temp.next;
            }
            ListNode tempb = headA;
            while (temp != null) {
                if (temp.equals(tempb)) {
                    return temp;
                }
                temp = temp.next;
                tempb = tempb.next;

            }
            return null;
        }


    }

}

class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}


