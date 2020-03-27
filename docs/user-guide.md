## XS2ASandbox User Guide

This document describes how to configure XS2ASandbox in your environment.

### E-mail configuration

To enable mail sending you should configure environment:

```
  - SPRING_MAIL_HOST=
  - SPRING_MAIL_PORT=
 ```
 
### Password reset

```
reset-password:
  expiration-code-minutes: 5
  email:
    template-message: "Please use this code %s to reset your password"
    subject: "Your code for password reset"
    from: noreply@adorsys.de
```
