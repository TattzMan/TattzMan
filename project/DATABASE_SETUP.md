# 🎓 Learning Management System - Database Setup Guide

## 📋 System Requirements

- **Java**: 17 or higher
- **MySQL**: 8.0 or higher
- **Maven**: 3.6 or higher
- **IntelliJ IDEA**: 2021.3 or higher (recommended)

## 🗄️ Database Configuration

### Step 1: Install and Start MySQL

1. **Download MySQL**: https://dev.mysql.com/downloads/mysql/
2. **Install MySQL Server** with the following settings:
   - **Port**: 3306 (default)
   - **Root Password**: `2001` (or update in application.properties)

### Step 2: Create Database

Open MySQL Command Line or MySQL Workbench and run:

```sql
-- Create the database
CREATE DATABASE project;

-- Verify database creation
SHOW DATABASES;
```

### Step 3: Create MySQL User (Optional but Recommended)

```sql
-- Create dedicated user for the application
CREATE USER 'lms_user'@'localhost' IDENTIFIED BY '2001';

-- Grant privileges
GRANT ALL PRIVILEGES ON project.* TO 'lms_user'@'localhost';

-- Flush privileges
FLUSH PRIVILEGES;
```

## 🔧 Application Configuration

The application is already configured with the following database settings in `application.properties`:

```properties
spring.application.name=demo
spring.datasource.url=jdbc:mysql://localhost:3306/project
spring.datasource.username=root
spring.datasource.password=2001
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.show-sql=true
spring.jpa.generate.ddl=true
spring.jpa.hibernate.ddl.auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true
```

**⚠️ Important**: If your MySQL root password is different from `2001`, update the password in `application.properties`.

## 📊 Database Schema

The application will automatically create the following tables:

### 1. `admins` Table
```sql
CREATE TABLE admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(255) DEFAULT 'SYSTEM_ADMIN',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 2. `students` Table
```sql
CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    candidate_number VARCHAR(255) UNIQUE,
    date_of_birth DATE,
    session ENUM('JUNE', 'NOVEMBER'),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 3. `teachers` Table
```sql
CREATE TABLE teachers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 4. `courses` Table
```sql
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(255),
    description TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

### 5. `enrollments` Table
```sql
CREATE TABLE enrollments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    course_id BIGINT,
    enrollment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);
```

### 6. `materials` Table
```sql
CREATE TABLE materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT,
    title VARCHAR(255),
    file_path VARCHAR(255),
    uploaded_by BIGINT,
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (uploaded_by) REFERENCES teachers(id)
);
```

### 7. `assignments` Table
```sql
CREATE TABLE assignments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT,
    title VARCHAR(255),
    description TEXT,
    due_date DATETIME,
    created_by BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id),
    FOREIGN KEY (created_by) REFERENCES teachers(id)
);
```

### 8. `submissions` Table
```sql
CREATE TABLE submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    assignment_id BIGINT,
    student_id BIGINT,
    file_path VARCHAR(255),
    submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    grade DOUBLE,
    feedback TEXT,
    marked_by BIGINT,
    marked_at DATETIME,
    FOREIGN KEY (assignment_id) REFERENCES assignments(id),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (marked_by) REFERENCES teachers(id)
);
```

## 🚀 Running the Application in IntelliJ IDEA

### Step 1: Import Project

1. Open IntelliJ IDEA
2. Click **File** → **Open**
3. Navigate to the `/app/project` folder
4. Select the folder and click **OK**
5. IntelliJ will detect it as a Maven project

### Step 2: Configure Project SDK

1. Go to **File** → **Project Structure**
2. Under **Project Settings** → **Project**
3. Set **Project SDK** to Java 17 or higher
4. Set **Project language level** to 17

### Step 3: Maven Configuration

1. IntelliJ should automatically detect the `pom.xml`
2. If not, right-click on `pom.xml` → **Add as Maven Project**
3. Wait for Maven to download dependencies

### Step 4: Database Connection Test

1. Go to **View** → **Tool Windows** → **Database**
2. Click **+** → **Data Source** → **MySQL**
3. Configure connection:
   - **Host**: localhost
   - **Port**: 3306
   - **Database**: project
   - **User**: root
   - **Password**: 2001
4. Click **Test Connection**

### Step 5: Run the Application

1. Locate `ProjectApplication.java` in the project explorer
2. Right-click → **Run 'ProjectApplication'**
3. Or use the green run button in the toolbar
4. Check the console for startup messages

## 🔐 Default Login Credentials

The system automatically creates sample users on first startup:

| Role    | Email              | Password    | Access Level        |
|---------|--------------------|-------------|---------------------|
| Admin   | admin@system.com   | admin123    | Full system access  |
| Teacher | teacher@demo.com   | teacher123  | Course management   |
| Student | student@demo.com   | student123  | Course enrollment   |

## 🌐 Application URLs

Once running, access the application at:

- **Home Page**: http://localhost:8080/
- **Login Page**: http://localhost:8080/login
- **Student Dashboard**: http://localhost:8080/student/dashboard
- **Teacher Dashboard**: http://localhost:8080/teacher/dashboard
- **Admin Dashboard**: http://localhost:8080/admin/dashboard

## 🛠️ Troubleshooting

### Common Issues:

1. **Connection refused**:
   - Ensure MySQL is running
   - Check port 3306 is not blocked
   - Verify database `project` exists

2. **Authentication failed**:
   - Check MySQL username/password
   - Verify user has proper privileges

3. **Table doesn't exist**:
   - Ensure `spring.jpa.hibernate.ddl-auto=update` in properties
   - Check MySQL user has CREATE privileges

4. **Port 8080 already in use**:
   - Add `server.port=8081` to application.properties
   - Or kill the process using port 8080

### Verification Steps:

1. **Check Database Connection**:
   ```sql
   USE project;
   SHOW TABLES;
   ```

2. **Verify Sample Data**:
   ```sql
   SELECT * FROM admins;
   SELECT * FROM students;
   SELECT * FROM teachers;
   ```

3. **Check Application Logs**:
   - Look for "Database initialization completed!" message
   - Verify all tables are created successfully

## 📁 Project Structure

```
project/
├── src/
│   ├── main/
│   │   ├── java/com/example/project/
│   │   │   ├── controllers/     # Web controllers
│   │   │   ├── entities/        # Database entities
│   │   │   ├── repositories/    # Data access layer
│   │   │   ├── services/        # Business logic
│   │   │   ├── config/          # Configuration classes
│   │   │   └── ProjectApplication.java
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf templates
│   │       └── application.properties
├── pom.xml                      # Maven dependencies
└── README.md
```

## 🎯 Next Steps

1. **Start MySQL Service**
2. **Create Database**: `CREATE DATABASE project;`
3. **Open Project in IntelliJ**
4. **Run ProjectApplication**
5. **Access**: http://localhost:8080/
6. **Login with Default Credentials**

## 🆘 Support

If you encounter any issues:

1. Check MySQL service is running
2. Verify database connection in application.properties
3. Ensure Java 17+ is installed
4. Check IntelliJ project SDK settings
5. Review application console logs for errors

**The system is now ready for offline use with full functionality!** 🎉