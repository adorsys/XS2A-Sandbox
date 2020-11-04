## ModelBank User Guide

This document describes how to configure ModelBank in your environment.

### E-mail configuration

To enable mail sending you should configure environment variables for ledgers service:

```
  - SPRING_MAIL_HOST=
  - SPRING_MAIL_PORT=
  - SPRING_MAIL_USERNAME=
  - SPRING_MAIL_PASSWORD=
  - SPRING_MAIL_PROPERTIES_MAIL_FROM=
  - SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=
  - SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=
 ```

 If you want to debug mail messages you can add this properties as well:
 
```
  - SPRING_MAIL_PROPERTIES_MAIL_DEBUG=true
```

**Note**: To use for example Gmail account and SMTP server to sent emails you should configure two-factor authentication and create application password.

### Password reset

```
reset-password:
  expiration-code-minutes: 5
  email:
    template-message: "Please use this code %s to reset your password"
    subject: "Your code for password reset"
    from: noreply@adorsys.de
```
