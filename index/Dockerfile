FROM python:3

WORKDIR /app

RUN pip install --upgrade pip && \
  pip install requests

COPY index.py .

ENTRYPOINT ["python", "index.py"]
