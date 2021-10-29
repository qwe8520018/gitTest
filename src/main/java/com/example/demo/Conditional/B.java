package com.example.demo.Conditional;

public class B {
    String s = "hello";

    static {
        System.out.println("静态代码块---");
    }
    {
        System.out.println("代码块---"+s);
    }

   public B(){
        System.out.println("前--"+s);
        this.s = "aaa";

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("a");
            }
        };



        Runnable runnable1 = () -> {
            System.out.println("b");
        };

        

    }
}
