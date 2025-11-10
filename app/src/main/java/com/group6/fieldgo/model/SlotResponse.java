package com.group6.fieldgo.model;

import java.util.List;

public class SlotResponse {
    private boolean success;
    private String message;
    private SlotData data;

    public static class SlotData {
        private List<WeekDay> weekDays;
        private List<Slot> slots;
        private List<BookedSlot> bookedSlots;

        public List<WeekDay> getWeekDays() { return weekDays; }
        public List<Slot> getSlots() { return slots; }
        public List<BookedSlot> getBookedSlots() { return bookedSlots; }
    }

    public static class WeekDay {
        private String date;
        private String day;

        public String getDate() { return date; }
        public String getDay() { return day; }
        public String getDisplay() { return day.substring(0, 3) + "\n" + date.substring(8); }
    }

    public static class Slot {
        private int id;
        private String startTime;
        private String endTime;
        private double price;

        public int getId() { return id; }
        public String getTimeRange() { return startTime.substring(0,5) + " - " + endTime.substring(0,5); }
        public double getPrice() { return price; }
    }

    public static class BookedSlot {
        private String date;
        private int slotId;
        private String status;

        public String getDate() { return date; }
        public int getSlotId() { return slotId; }
    }

    public SlotData getData() { return data; }
}