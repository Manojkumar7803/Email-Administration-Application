# Email Administration Application (Java Swing)

A desktop starter project for managing employee email-account records.

## Features
- Demo administrator login
- Create employee records with department-based email IDs
- Generate strong temporary passwords using `SecureRandom`
- Store only BCrypt password hashes in MySQL
- View, update alternate email/mailbox capacity, and delete employee records
- Test SMTP authentication using JavaMail

## Requirements
- JDK 11+
- Maven
- MySQL 8+
- Eclipse or IntelliJ IDEA

## Setup
1. Run `database/schema.sql` in MySQL Workbench.
2. Configure database connection using environment variables:
   - `DB_URL` (default: `jdbc:mysql://localhost:3306/email_admin?serverTimezone=UTC`)
   - `DB_USER` (default: `root`)
   - `DB_PASSWORD` (set this to your MySQL password)
3. Open this folder as a Maven project and allow Maven to download dependencies.
4. Run `com.emailadmin.EmailAdministrationApp`.

Demo login:
- Username: `admin`
- Password: `admin123`

Change/remove this demo login before any real deployment. It is intentionally only a local demonstration, not production authentication.

## SMTP verification
Use an SMTP host, port, username, and provider-approved app password. For Gmail, use an app password where eligible; a normal Google account password generally will not work. Credentials are entered for the test and are not saved by this application.

## Important scope note
This project creates and manages records in the local database. It does not provision real mailboxes at an email provider. Real mailbox creation requires integration with an authorized provider API or an organization-managed mail server. Mailbox capacity is stored as an administrative setting and is not enforced by a mail server.

## Email address format
`firstname.lastname.department@company.com`

Change `company.com` in `EmailGenerator.java` to your organization's domain. Ensure you own or administer that domain before using the addresses.
