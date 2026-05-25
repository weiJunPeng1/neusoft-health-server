package com.neusoft.health;

import cn.hutool.crypto.digest.BCrypt;

import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("please enter your pwd:");
        String Pwd = scanner.next();
        String PwdBcrypt = BCrypt.hashpw(Pwd, BCrypt.gensalt());
        System.out.println("PwdBcrypt: " + PwdBcrypt);
    }
}
