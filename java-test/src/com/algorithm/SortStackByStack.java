package com.algorithm;

import java.util.Stack;

/**
 * SortStackByStack 使用两个栈与一个变量进行排序
 * 
 * 思想： 
 * 
 * 1. 数据栈存储数据，辅助栈为空，中间变量存储数据栈与辅助栈之间的中间值 <br>
 * 2. 数据栈依次抛出值，使用中间变量接受<br>
 * 3 若辅助栈为空，则将中间变量直接压栈<br>
 * 4. 若辅助栈不为空，将中间变量与辅助栈顶元素进行比较，若大于(小于)栈顶元素，<br>
 * 则将辅助栈顶元素压进*数据栈*中，再次判断，直至辅助栈为空或者中间变量值小于(大于)<br>
 * 栈顶元素时，将中间变量压栈<br>
 * 5 重复23，直至数据栈为空，依次抛出辅助栈便是从小到大的排序<br>
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