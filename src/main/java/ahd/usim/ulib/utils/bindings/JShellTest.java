package ahd.usim.ulib.utils.bindings;

import jdk.jshell.JShell;

public class JShellTest {
    public static void main(String[] args) {
        var shell = JShell.create();
        var res = shell.eval("""
                int a = 1
                for (int i = 1; i < 10; i++) {
                    a += a;
                }
                System.out.println(a)
                """);
        System.out.println(res);
    }
}
