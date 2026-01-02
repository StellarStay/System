# StellarStay Backend System

## Configuration Setup

### First Time Setup

1. Copy the example configuration file:
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

2. Edit `application.properties` and fill in your actual values:
   - Database credentials (PostgreSQL)
   - Redis connection details
   - Email configuration (Gmail SMTP)
   - JWT secret key
   - MoMo payment credentials
   - AWS S3 credentials

### Important Notes

- **NEVER** commit `application.properties` to git - it contains sensitive credentials
- Always use `application.properties.example` as a template
- Keep your credentials secure and do not share them

### Required Services

- PostgreSQL database
- Redis server
- Gmail account with App Password enabled
- MoMo payment account (for production)
- AWS S3 bucket

### Environment Variables (Alternative)

You can also use environment variables instead of the properties file for production deployments.

