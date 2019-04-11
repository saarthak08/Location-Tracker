package com.sg.hackamu.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ChatAdapter {
    Calendar calander = Calendar.getInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    String messageTime = simpleDateFormat.format(calander.getTime());
}
