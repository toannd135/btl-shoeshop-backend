package vn.edu.ptit.shoe_shop.common.constant;

import java.util.regex.Pattern;

public class EmailPattern {
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
}
