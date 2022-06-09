# AccountService

[Account Service](https://hyperskill.org/projects/217) from JetBrains Academy - Challenging Difficulty

[My JetBrainsAcademy Profile](https://hyperskill.org/profile/204045764)

First attempt at REST API with full intergration of Spring Boot:
- Spring Security (authorising and authenticating) 
- Spring Data (JPA using H2 DB)
- Spring Validation
- Logging (using DB entries)
- Securing connection via HTTPS

System for tracking payroll entries.

Allows Unauthorised users to:
- Register

Allows users to:
- Change Password
- Get payments - request by date or get all (for own acccount)

Allows Accountants to:
- All rights of a user (expanded access to all accounts) +
- POST new payments
- PUT (edit) existing payments

Allows Admin to:
- GET all information about a user (not including sensitive data such as payroll or passwords)
- DELETE a user
- PUT (edit) an existing user to change role
- (Admin role is restricted from any business functions (i.e. no access to Accountant functions)
