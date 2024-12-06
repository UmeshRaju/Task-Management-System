package com.example.taskmanager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class TaskManagementSystem {
    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        try {
            sessionFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Project.class)
                .addAnnotatedClass(Task.class)
                .buildSessionFactory();

            // Example operations
            int userId = createUser("john_doe");
            int projectId = createProject("Web Application");
            int taskId = createTask("Implement login", "Create login functionality", projectId, userId);
            updateTaskStatus(taskId, TaskStatus.IN_PROGRESS);
            listProjectTasks(projectId);
            listUserTasks(userId);

        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
        }
    }

    private static int createUser(String username) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            User user = new User();
            user.setUsername(username);
            session.persist(user);
            session.getTransaction().commit();
            System.out.println("User created: " + username);
            return user.getId();
        } finally {
            session.close();
        }
    }

    private static int createProject(String name) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Project project = new Project();
            project.setName(name);
            session.persist(project);
            session.getTransaction().commit();
            System.out.println("Project created: " + name);
            return project.getId();
        } finally {
            session.close();
        }
    }

    private static int createTask(String title, String description, int projectId, int userId) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Task task = new Task();
            task.setTitle(title);
            task.setDescription(description);
            task.setStatus(TaskStatus.TODO);
            task.setProject(session.get(Project.class, projectId));
            task.setAssignedUser(session.get(User.class, userId));
            session.persist(task);
            session.getTransaction().commit();
            System.out.println("Task created: " + title);
            return task.getId();
        } finally {
            session.close();
        }
    }

    private static void updateTaskStatus(int taskId, TaskStatus newStatus) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Task task = session.get(Task.class, taskId);
            if (task != null) {
                task.setStatus(newStatus);
                System.out.println("Task status updated: " + task.getTitle() + " - " + newStatus);
            }
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    private static void listProjectTasks(int projectId) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            Project project = session.get(Project.class, projectId);
            if (project != null) {
                System.out.println("Tasks for project: " + project.getName());
                for (Task task : project.getTasks()) {
                    System.out.println("- " + task.getTitle() + " (" + task.getStatus() + ")");
                }
            }
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    private static void listUserTasks(int userId) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                System.out.println("Tasks for user: " + user.getUsername());
                for (Task task : user.getTasks()) {
                    System.out.println("- " + task.getTitle() + " (" + task.getStatus() + ")");
                }
            }
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }
}