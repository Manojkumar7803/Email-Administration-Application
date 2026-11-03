package com.emailadmin;

public class Employee {
    private final int id;
    private final String name;
    private final String department;
    private final String email;
    private final String alternateEmail;
    private final int capacityMb;

    public Employee(int id, String name, String department, String email,
                    String alternateEmail, int capacityMb) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.email = email;
        this.alternateEmail = alternateEmail;
        this.capacityMb = capacityMb;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getEmail() { return email; }
    public String getAlternateEmail() { return alternateEmail; }
    public int getCapacityMb() { return capacityMb; }
}
