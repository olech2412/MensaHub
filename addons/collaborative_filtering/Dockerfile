# Verwenden eines offiziellen Python-Images als Basis
FROM python:3.8-slim

# Arbeitsverzeichnis im Container festlegen
WORKDIR /app

# Anforderungen kopieren und installieren
COPY requirements.txt requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

# Anwendungscode kopieren
COPY . .

# Flask-Umgebungsvariablen setzen
ENV FLASK_APP=collaborative_filtering.py
ENV FLASK_RUN_HOST=0.0.0.0

# Port für Flask öffnen
EXPOSE 5000

# Befehl zum Starten der Flask-Anwendung
CMD ["flask", "run"]