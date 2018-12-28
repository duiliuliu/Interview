package com.algorithm.reverseList;

/**
 * ListNode 反转链表 可以用循环或者递归方法实现
 */
public class ListNode {

    public int data;
    public ListNode next;

    public ListNode(int data, ListNode next) {
        this.data = data;
        this.next = next;
    }

    public static String printList(ListNode head) {
        ListNode node = head;
        StringBuilder sb = new StringBuilder();
        while (node != null) {
            sb.append(node.data + ", ");
            node = node.next;
        }
        return "[" + sb.deleteCharAt(sb.length() - 2).toString() + "]";
    }

    public static ListNode reverse1(ListNode head) {
        if (head == null) {
            return null;
        }
        ListNode pre = null; // ǰ��
        ListNode current = head; // ��ǰ�ڵ�
        ListNode tmp = null; // ��ʱ�ڵ�

        while (current != null) {
            tmp = current.next; // ������
            current.next = pre; // ��תָ��

            // ָ���»�
            pre = current;
            current = tmp;
        }

        return pre;
    }

    // ��ת��ǰ�ڵ�֮ǰ�ȷ�ת�����ڵ�
    public static ListNode reverse2(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode tmp = reverse2(head.next);
        ListNode post = head.next;
        post.next = head;
        head.next = null;

        return tmp;
    }

    public static void main(String[] args) {
        // ����
        ListNode list = new ListNode(0, new ListNode(1, new ListNode(2, new ListNode(3, new ListNode(4, null)))));
        System.out.println(printList(list));

        list = reverse2(list);

        System.out.println(printList(list));

    }

}