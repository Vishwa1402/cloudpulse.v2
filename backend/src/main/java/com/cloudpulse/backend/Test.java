public class Test {
    public static void main(String[] args) {
        for (java.lang.reflect.Method m : org.springframework.security.authentication.dao.DaoAuthenticationProvider.class
                .getMethods()) {
            System.out.println(m.getName() + "(" + java.util.Arrays.toString(m.getParameterTypes()) + ")");
        }
    }
}
