FROM python:3.9-slim

# Install necessary system dependencies
RUN apt-get update && apt-get install -y \
    build-essential \
    libmariadb-dev-compat \
    libmariadb-dev

# Set work directory
WORKDIR /app

# Copy requirements file
COPY requirements.txt requirements.txt

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY . .

# Expose the port the app runs on
EXPOSE 8084

# Command to run the app
CMD ["python", "collaborative_filtering.py"]