# UNB Course Scheduler

**CS1083 — Introduction to Programming II in Java**  
**Winter 2026**

**Student:** Praise Chopde  
**Student ID:** 3779552  
**Instructor:** Dr. Peash Saha  
**University:** University of New Brunswick, Saint John  

---

## Project Overview

The **UNB Course Scheduler** is a JavaFX desktop application that simulates an academic scheduling system for a university environment. The application allows students and advisors to manage courses, enrollments, prerequisites, and academic records through a user-friendly graphical interface.

The system demonstrates core Object-Oriented Programming (OOP) concepts taught in CS1083, including:

- Inheritance
- Polymorphism
- Encapsulation
- Abstract Classes
- Exception Handling
- File I/O
- Recursion
- Collections

---

## Why the Project Changed

The original project idea was a **Library Management System** where an administrator could manage books, members, and borrowing operations.

As development progressed, the project was changed into a **UNB Course Scheduler** because it felt more relevant to university life and academic systems.

Although the project domain changed, the core software structure remained the same:

| Original System | Final System |
|----------------|-------------|
| Books | Courses |
| Library Members | Students |
| Librarian | Advisors |
| Head Librarian | Head Advisor |

All required OOP concepts and project requirements remained fully implemented.

---

## Features

The application allows users to:

### Student Management
- Register new students
- Store:
  - Name
  - Student ID
  - Email
  - Phone number
  - Program

### Course Management
- Add courses with:
  - Course code
  - Course name
  - Instructor
  - Credit hours
  - Semester
  - Prerequisites

### Enrollment System
- Enroll students into courses
- Automatically checks:
  - Prerequisites
  - Enrollment limits
  - Seat availability

### Course Actions
- Drop enrolled courses
- Mark completed courses

### Searching
- Linear student search
- Recursive student search

### Administrative Functions
(Head Advisor only)

- Generate academic reports
- Remove courses
- Approve prerequisite overrides

### Activity Tracking
- Stack-based activity log
- Most recent actions displayed first

### File Logging
All actions are written to:

```txt
schedule_log.txt
```

Examples:

- Student registration
- Course creation
- Enrollment actions
- Course drops
- Administrative actions

---

## Class Structure

The project contains **7 Java classes**.

### 1. Person (Abstract)

Base class containing:

Fields:

- Name
- ID
- Email
- Phone Number

Abstract methods:

```java
displayInfo()
getRole()
```

---

### 2. Student

Extends:

```java
Person
```

Additional fields:

- Program
- GPA
- Current courses
- Completed courses

Methods:

```java
enrollCourse()
dropCourse()
completeCourse()
checkPrerequisites()
```

---

### 3. Advisor (Abstract)

Extends:

```java
Person
```

Additional fields:

- Employee ID
- Shift

Methods:

```java
processEnrollment()
processDrop()
canRemoveCourses()
```

---

### 4. HeadAdvisor

Extends:

```java
Advisor
```

Privileges:

- Remove courses
- Generate reports
- Approve prerequisite overrides

```java
canRemoveCourses()
```

Returns:

```java
true
```

---

### 5. RegularAdvisor

Extends:

```java
Advisor
```

Privileges:

- Standard advisor permissions only

```java
canRemoveCourses()
```

Returns:

```java
false
```

---

### 6. Course

Stores:

- Course code
- Course name
- Instructor
- Credit hours
- Semester
- Prerequisites

Methods:

```java
enrollStudent()
dropStudent()
```

---

### 7. CourseScheduler

Main JavaFX application class.

Responsibilities:

- Managing students
- Managing courses
- Managing activity logs
- File writing
- GUI handling
- Event processing

---

## OOP Concepts Demonstrated

### Inheritance

Class hierarchy:

```text
Person
│
├── Student
│
└── Advisor
      │
      ├── HeadAdvisor
      │
      └── RegularAdvisor
```

---

### Polymorphism

The application stores objects using parent references and executes the correct method implementation at runtime.

Examples:

```java
displayInfo()
getRole()
```

Different object types produce different behavior automatically.

---

### Encapsulation

All fields are declared:

```java
private
```

Access occurs only through:

```java
getters
setters
```

---

### Abstract Classes

Abstract classes used:

- Person
- Advisor

These classes cannot be instantiated directly.

---

### Exception Handling

The application catches:

```java
NumberFormatException
```

Examples:

- Invalid student ID
- Invalid phone number
- Invalid credit hours

Also catches:

```java
IOException
```

during log file writing.

---

### File I/O

Uses:

```java
PrintWriter
```

to write system actions into:

```txt
schedule_log.txt
```

---

### Recursion

Student searching includes a recursive implementation:

```java
findStudentRecursive()
```

Base case:

```java
index < 0
```

---

### Collections

Collections used:

```java
ArrayList<Student>
ArrayList<Course>
Stack<String>
```

Purpose:

- Store students
- Store courses
- Maintain activity logs

---

## Project Requirements Met

✔ Multiple classes  

✔ Multiple subclasses  

✔ Inheritance hierarchy  

✔ Polymorphism  

✔ Encapsulation  

✔ Abstract classes  

✔ Exception handling  

✔ File I/O  

✔ Recursion  

✔ Collections framework  

✔ JavaFX graphical interface  

---

## Technologies Used

- Java
- JavaFX
- ArrayList
- Stack
- PrintWriter
- Exception Handling
- OOP Principles

---

## Running the Project

1. Open the project in your Java IDE
   - IntelliJ IDEA
   - Eclipse
   - VS Code

2. Ensure JavaFX libraries are installed and configured

3. Run:

```java
CourseScheduler.java
```

4. Use the application interface to:

- Register students
- Add courses
- Enroll students
- Generate reports
- View activity logs

---

## Author

**Praise Chopde**  
CS1083 — Introduction to Programming II in Java  
University of New Brunswick, Saint John  
Winter 2026
