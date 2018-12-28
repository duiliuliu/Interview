package com.algorithm;

import java.util.Stack;

/**
 * SortStackByStack ʹ������ջ��һ��������������
 * 
 * ˼�룺 
 * 
 * 1. ����ջ�洢���ݣ�����ջΪ�գ��м�����洢����ջ�븨��ջ֮����м�ֵ <br>
 * 2. ����ջ�����׳�ֵ��ʹ���м��������<br>
 * 3 ������ջΪ�գ����м����ֱ��ѹջ<br>
 * 4. ������ջ��Ϊ�գ����м�����븨��ջ��Ԫ�ؽ��бȽϣ�������(С��)ջ��Ԫ�أ�<br>
 * �򽫸���ջ��Ԫ��ѹ��*����ջ*�У��ٴ��жϣ�ֱ������ջΪ�ջ����м����ֵС��(����)<br>
 * ջ��Ԫ��ʱ�����м����ѹջ<br>
 * 5 �ظ�23��ֱ������ջΪ�գ������׳�����ջ���Ǵ�С���������<br>
 */
public class SortStackByStack {

    public static void sortStackByStack(Stack<Integer> stack) {
        Stack<Integer> helpsStack = new Stack<>();
        int tmp = 0;
        while (!stack.isEmpty()) {
            tmp = stack.pop();
            while (!helpsStack.isEmpty()) {
                if (tmp > helpsStack.peek()) {
                    stack.push(helpsStack.pop());
                } else {
                    break;
                }
            }
            helpsStack.push(tmp);
        }

        while (!helpsStack.isEmpty()) {
            stack.push(helpsStack.pop());
        }
    }

    public static void main(String[] args) {
        Stack<Integer> s;
        s = new Stack<>();
        s.push(1);
        s.push(5);
        s.push(3);
        s.push(4);
        s.push(2);
        s.push(5);
        s.push(3);
        sortStackByStack(s);
        while (!s.isEmpty()) {
            System.out.println(s.pop());
        }
    }

}