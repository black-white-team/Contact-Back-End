# 🛠️  Contact Management Project

## 💡 Introduction

The `UserController` class handles user data operations, allowing you to create, update, delete, and retrieve users, as well as manage their associated contact details like phone numbers and addresses. Additionally, users can be bookmarked, and the data can be imported/exported using Excel files.

## 🔑 Features

- Create, update, delete, and retrieve user information along with contact details.
- Manage contact details, including phone numbers, emails, social media handles, and physical addresses.
- Bookmark users for easy access.
- Import and export user data through Excel files.

## 📋 How to Run the Application

### 🛡 Prerequisites
- **Java 17**: Required to run the Spring Boot application.
- **Maven**: Used for managing dependencies and building the project.
- **MySQL**: The application uses MySQL as its database.

### 📥 Installation Steps

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd contact-management-backend
   ```

2. **Build the Database**:
   Create the database by running the SQL script provided (`user.sql`) to set up the necessary tables (users, phone numbers, email addresses, etc.).

3. **Configure the Database**:
   Update the `application.properties` file with your MySQL configuration:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/your_db_name
   spring.datasource.username=your_db_user
   spring.datasource.password=your_db_password
   ```

4. **Install dependencies and run the project**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. **Access the API**:
   The backend API will be available at `http://localhost:8080`.

## 🚀 Contributing

1. **Fork the repository**.
2. **Create a new branch** (`git checkout -b feature/your-feature`).
3. **Commit your changes** (`git commit -m 'Add a new feature'`).
4. **Push to the branch** (`git push origin feature/your-feature`).
5. **Open a Pull Request**.

## 📄 License

This project is licensed under the MIT License - see the `LICENSE` file for details.

## 💬 Contact

If you have any questions, feel free to reach out by opening an issue on GitHub.

